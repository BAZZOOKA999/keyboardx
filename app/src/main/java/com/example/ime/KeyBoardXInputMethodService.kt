package com.example.ime

import android.content.Context
import android.inputmethodservice.InputMethodService
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.ai.FirebaseAIProvider
import com.example.data.local.DataStoreManager
import com.example.data.local.DictionaryHelper
import com.example.data.local.EncryptedStorageHelper
import com.example.data.local.KeyBoardXDatabase
import com.example.data.model.KeyboardSettings
import com.example.ime.view.KeyBoardRootView
import com.example.translator.MLKitTranslatorManager
import com.example.voice.VoiceInputManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class KeyBoardXInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    private val store = ViewModelStore()

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore get() = store

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var database: KeyBoardXDatabase
    private lateinit var dictionaryHelper: DictionaryHelper
    private lateinit var aiProvider: FirebaseAIProvider
    private lateinit var translatorManager: MLKitTranslatorManager
    private lateinit var voiceManager: VoiceInputManager
    private var audioManager: AudioManager? = null
    private var vibrator: Vibrator? = null

    // Mutable states for Compose
    private val settingsState = mutableStateOf(KeyboardSettings())
    private val layoutTypeState = mutableStateOf(KeyboardLayoutType.QWERTY_LOWER)
    private val activePanelState = mutableStateOf(KeyboardPanel.NONE)
    private val isTermuxActiveState = mutableStateOf(false)
    private val isCtrlActiveState = mutableStateOf(false)
    private val isAltActiveState = mutableStateOf(false)
    private val isSensitiveFieldState = mutableStateOf(false)
    private val imeActionState = mutableIntStateOf(EditorInfo.IME_ACTION_NONE)
    private val suggestionsState = mutableStateOf<List<String>>(emptyList())
    private val currentTextContextState = mutableStateOf("")

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)

        dataStoreManager = DataStoreManager(this)
        database = KeyBoardXDatabase.getInstance(this)
        dictionaryHelper = DictionaryHelper(database.personalWordDao())
        aiProvider = FirebaseAIProvider()
        translatorManager = MLKitTranslatorManager()
        voiceManager = VoiceInputManager(this)
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator

        serviceScope.launch {
            dataStoreManager.settingsFlow.collectLatest { newSettings ->
                settingsState.value = newSettings
                isTermuxActiveState.value = newSettings.termuxModeEnabled
            }
        }
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setViewTreeLifecycleOwner(this@KeyBoardXInputMethodService)
            setViewTreeViewModelStoreOwner(this@KeyBoardXInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@KeyBoardXInputMethodService)

            setContent {
                val settings by settingsState
                val layoutType by layoutTypeState
                val activePanel by activePanelState
                val isTermuxActive by isTermuxActiveState
                val isCtrlActive by isCtrlActiveState
                val isAltActive by isAltActiveState
                val isSensitive by isSensitiveFieldState
                val imeAction by imeActionState
                val suggestions by suggestionsState
                val contextText by currentTextContextState

                KeyBoardRootView(
                    settings = settings,
                    layoutType = layoutType,
                    activePanel = activePanel,
                    isTermuxActive = isTermuxActive,
                    isCtrlActive = isCtrlActive,
                    isAltActive = isAltActive,
                    isSensitiveField = isSensitive,
                    imeAction = imeAction,
                    suggestions = suggestions,
                    currentTextContext = contextText,
                    aiProvider = aiProvider,
                    translatorManager = translatorManager,
                    voiceManager = voiceManager,
                    quickTextDao = database.quickTextDao(),
                    clipboardDao = database.clipboardDao(),
                    onAction = { handleKeyboardAction(it) },
                    onToggleTermux = { isTermuxActiveState.value = !isTermuxActiveState.value },
                    onToggleCtrl = { isCtrlActiveState.value = !isCtrlActiveState.value },
                    onToggleAlt = { isAltActiveState.value = !isAltActiveState.value },
                    onSuggestionClicked = { word ->
                        commitSuggestion(word)
                    }
                )
            }
        }
        return composeView
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        info?.let {
            val variation = it.inputType and InputType.TYPE_MASK_VARIATION
            val isPassword = (variation == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
                variation == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD ||
                variation == InputType.TYPE_NUMBER_VARIATION_PASSWORD)

            val noSuggestions = (it.inputType and InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS) != 0

            isSensitiveFieldState.value = isPassword || noSuggestions || settingsState.value.privacyMode
            imeActionState.intValue = it.imeOptions and EditorInfo.IME_MASK_ACTION
        }

        // Reset modifier toggles & close active panel on new input session
        isCtrlActiveState.value = false
        isAltActiveState.value = false
        activePanelState.value = KeyboardPanel.NONE
        updateSuggestions()
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        super.onFinishInputView(finishingInput)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        voiceManager.cancelListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        store.clear()
        serviceScope.cancel()
    }

    private fun handleKeyboardAction(action: KeyboardAction) {
        val ic = currentInputConnection ?: return
        triggerFeedback()

        when (action) {
            is KeyboardAction.InputChar -> {
                // Check if Ctrl or Alt modifier is active (Termux mode)
                if (isCtrlActiveState.value || isAltActiveState.value) {
                    val char = action.char.firstOrNull() ?: ' '
                    val keyCode = KeyEvent.keyCodeFromString("KEYCODE_${char.uppercaseChar()}")
                    if (keyCode != KeyEvent.KEYCODE_UNKNOWN) {
                        sendTermuxKeyEvent(
                            keyCode = keyCode,
                            metaCtrl = isCtrlActiveState.value,
                            metaAlt = isAltActiveState.value
                        )
                    } else {
                        ic.commitText(action.char, 1)
                    }
                    isCtrlActiveState.value = false
                    isAltActiveState.value = false
                } else {
                    ic.commitText(action.char, 1)
                    if (layoutTypeState.value == KeyboardLayoutType.QWERTY_UPPER) {
                        layoutTypeState.value = KeyboardLayoutType.QWERTY_LOWER
                    }
                    updateSuggestions()
                }
            }

            is KeyboardAction.Backspace -> {
                val selected = ic.getSelectedText(0)
                if (!selected.isNullOrEmpty()) {
                    ic.commitText("", 1)
                } else {
                    ic.deleteSurroundingText(1, 0)
                }
                updateSuggestions()
            }

            is KeyboardAction.Space -> {
                checkAndExpandShortcut(ic)
                ic.commitText(" ", 1)
                updateSuggestions()
            }

            is KeyboardAction.Enter -> {
                val actionId = imeActionState.intValue
                if (actionId != EditorInfo.IME_ACTION_NONE && actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                    ic.performEditorAction(actionId)
                } else {
                    sendDefaultKeyEvent(KeyEvent.KEYCODE_ENTER)
                }
            }

            is KeyboardAction.ToggleShift -> {
                layoutTypeState.value = when (layoutTypeState.value) {
                    KeyboardLayoutType.QWERTY_LOWER -> KeyboardLayoutType.QWERTY_UPPER
                    KeyboardLayoutType.QWERTY_UPPER -> KeyboardLayoutType.QWERTY_LOWER
                    KeyboardLayoutType.QWERTY_CAPS_LOCK -> KeyboardLayoutType.QWERTY_LOWER
                    else -> KeyboardLayoutType.QWERTY_UPPER
                }
            }

            is KeyboardAction.CapsLock -> {
                layoutTypeState.value = KeyboardLayoutType.QWERTY_CAPS_LOCK
            }

            is KeyboardAction.SwitchLayout -> {
                layoutTypeState.value = action.layout
            }

            is KeyboardAction.SwitchPanel -> {
                activePanelState.value = action.panel
                if (action.panel == KeyboardPanel.AI || action.panel == KeyboardPanel.TRANSLATOR) {
                    // Extract selected text or text before cursor
                    val selected = ic.getSelectedText(0)?.toString()
                    val textBefore = ic.getTextBeforeCursor(150, 0)?.toString() ?: ""
                    currentTextContextState.value = selected?.ifBlank { textBefore } ?: textBefore
                }
            }

            is KeyboardAction.SendKeyEvent -> {
                sendDefaultKeyEvent(action.keyCode, action.metaState)
            }

            is KeyboardAction.SendShortcut -> {
                sendTermuxKeyEvent(
                    keyCode = action.keyCode,
                    metaCtrl = action.metaCtrl,
                    metaAlt = action.metaAlt
                )
            }

            is KeyboardAction.CommitString -> {
                ic.commitText(action.text, 1)
                updateSuggestions()
            }

            is KeyboardAction.MoveCursor -> {
                // Move cursor offset (-1 or +1)
                if (action.offset < 0) {
                    sendDefaultKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT)
                } else {
                    sendDefaultKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT)
                }
            }

            is KeyboardAction.SelectAll -> {
                ic.performContextMenuAction(android.R.id.selectAll)
            }

            is KeyboardAction.Copy -> {
                ic.performContextMenuAction(android.R.id.copy)
            }

            is KeyboardAction.Cut -> {
                ic.performContextMenuAction(android.R.id.cut)
            }

            is KeyboardAction.Paste -> {
                ic.performContextMenuAction(android.R.id.paste)
            }

            is KeyboardAction.Undo -> {
                sendTermuxKeyEvent(KeyEvent.KEYCODE_Z, metaCtrl = true)
            }

            is KeyboardAction.Redo -> {
                sendTermuxKeyEvent(KeyEvent.KEYCODE_Y, metaCtrl = true)
            }

            is KeyboardAction.HideKeyboard -> {
                requestHideSelf(0)
            }
        }
    }

    private fun checkAndExpandShortcut(ic: InputConnection) {
        if (!settingsState.value.autoExpandShortcuts) return
        val textBefore = ic.getTextBeforeCursor(30, 0)?.toString() ?: return
        val lastWord = textBefore.substringAfterLast(' ', "").trim()
        if (lastWord.isNotBlank()) {
            serviceScope.launch {
                val shortcutItem = database.quickTextDao().getByShortcut(lastWord)
                if (shortcutItem != null) {
                    val decrypted = EncryptedStorageHelper.decrypt(shortcutItem.content)
                    // Delete the shortcut word and replace with full content
                    ic.deleteSurroundingText(lastWord.length, 0)
                    ic.commitText(decrypted, 1)
                }
            }
        }
    }

    private fun updateSuggestions() {
        if (!settingsState.value.suggestionsEnabled || isSensitiveFieldState.value) {
            suggestionsState.value = emptyList()
            return
        }

        val ic = currentInputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(25, 0)?.toString() ?: ""
        val currentWord = textBefore.substringAfterLast(' ', "").substringAfterLast('\n', "").trim()

        if (currentWord.length >= 2) {
            serviceScope.launch {
                val words = dictionaryHelper.getSuggestions(currentWord, settingsState.value.currentLanguage)
                suggestionsState.value = words
            }
        } else {
            suggestionsState.value = emptyList()
        }
    }

    private fun commitSuggestion(word: String) {
        val ic = currentInputConnection ?: return
        val textBefore = ic.getTextBeforeCursor(25, 0)?.toString() ?: ""
        val currentWord = textBefore.substringAfterLast(' ', "").substringAfterLast('\n', "").trim()

        if (currentWord.isNotEmpty()) {
            ic.deleteSurroundingText(currentWord.length, 0)
        }
        ic.commitText("$word ", 1)
        suggestionsState.value = emptyList()

        // Learn selected word
        serviceScope.launch {
            dictionaryHelper.learnWord(word, settingsState.value.currentLanguage)
        }
    }

    private fun sendDefaultKeyEvent(keyCode: Int, metaState: Int = 0) {
        val ic = currentInputConnection ?: return
        val eventTime = SystemClock.uptimeMillis()
        ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0, metaState))
    }

    private fun sendTermuxKeyEvent(keyCode: Int, metaCtrl: Boolean = false, metaAlt: Boolean = false) {
        val ic = currentInputConnection ?: return
        val eventTime = SystemClock.uptimeMillis()
        var metaState = 0
        if (metaCtrl) {
            metaState = metaState or KeyEvent.META_CTRL_ON or KeyEvent.META_CTRL_LEFT_ON
        }
        if (metaAlt) {
            metaState = metaState or KeyEvent.META_ALT_ON or KeyEvent.META_ALT_LEFT_ON
        }

        ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0, metaState))
        ic.sendKeyEvent(KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0, metaState))
    }

    private fun triggerFeedback() {
        val settings = settingsState.value
        if (settings.soundEnabled) {
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, settings.soundVolume)
        }
        if (settings.vibrationEnabled) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        settings.vibrationIntensityMs.toLong().coerceIn(10L, 100L),
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(settings.vibrationIntensityMs.toLong().coerceIn(10L, 100L))
            }
        }
    }
}
