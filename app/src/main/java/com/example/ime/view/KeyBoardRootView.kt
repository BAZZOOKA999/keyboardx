package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ai.AIProvider
import com.example.data.local.ClipboardDao
import com.example.data.local.QuickTextDao
import com.example.data.model.KeyboardSettings
import com.example.ime.KeyboardAction
import com.example.ime.KeyboardLayoutType
import com.example.ime.KeyboardPanel
import com.example.translator.MLKitTranslatorManager
import com.example.voice.VoiceInputManager

@Composable
fun KeyBoardRootView(
    settings: KeyboardSettings,
    layoutType: KeyboardLayoutType,
    activePanel: KeyboardPanel,
    isTermuxActive: Boolean,
    isCtrlActive: Boolean,
    isAltActive: Boolean,
    isSensitiveField: Boolean,
    imeAction: Int,
    suggestions: List<String>,
    currentTextContext: String,
    aiProvider: AIProvider,
    translatorManager: MLKitTranslatorManager,
    voiceManager: VoiceInputManager,
    quickTextDao: QuickTextDao,
    clipboardDao: ClipboardDao,
    onAction: (KeyboardAction) -> Unit,
    onToggleTermux: () -> Unit,
    onToggleCtrl: () -> Unit,
    onToggleAlt: () -> Unit,
    onSuggestionClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()

    // One-handed layout padding
    val contentModifier = when (settings.oneHandedMode) {
        "left" -> Modifier.fillMaxWidth(0.85f)
        "right" -> Modifier.fillMaxWidth(0.85f)
        else -> Modifier.fillMaxWidth()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.background.copy(alpha = settings.backgroundOpacity))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (settings.oneHandedMode == "right") {
                Spacer(modifier = Modifier.weight(0.15f))
            }

            Column(
                modifier = contentModifier
                    .background(colors.background)
                    .padding(bottom = 6.dp)
            ) {
                // 1. Top Toolbar
                ToolbarView(
                    settings = settings,
                    activePanel = activePanel,
                    isTermuxActive = isTermuxActive,
                    isSensitiveField = isSensitiveField,
                    onAction = onAction,
                    onToggleTermux = onToggleTermux
                )

                // 2. Termux Extra Row (if enabled and Termux is active)
                if (isTermuxActive && settings.termuxExtraRowEnabled && activePanel == KeyboardPanel.NONE) {
                    TermuxTopRow(
                        settings = settings,
                        isCtrlActive = isCtrlActive,
                        isAltActive = isAltActive,
                        onToggleCtrl = onToggleCtrl,
                        onToggleAlt = onToggleAlt,
                        onAction = onAction
                    )
                }

                // 3. Suggestions Bar (only when no panel is open and field is not sensitive)
                if (activePanel == KeyboardPanel.NONE &&
                    !isSensitiveField &&
                    settings.suggestionsEnabled &&
                    suggestions.isNotEmpty()
                ) {
                    SuggestionsBarView(
                        suggestions = suggestions,
                        colors = colors,
                        onSuggestionClicked = onSuggestionClicked
                    )
                }

                // 4. Content: either a panel or the keyboard layout
                when (activePanel) {
                    KeyboardPanel.AI -> {
                        AIPanelView(
                            initialText = currentTextContext,
                            aiProvider = aiProvider,
                            colors = colors,
                            onReplaceText = { onAction(KeyboardAction.CommitString(it)) },
                            onInsertText = { onAction(KeyboardAction.CommitString(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.TRANSLATOR -> {
                        TranslatorPanelView(
                            initialText = currentTextContext,
                            translatorManager = translatorManager,
                            colors = colors,
                            onInsertTranslation = { onAction(KeyboardAction.CommitString(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.VOICE -> {
                        VoiceInputOverlay(
                            voiceManager = voiceManager,
                            colors = colors,
                            defaultLanguage = settings.currentLanguage,
                            onTextRecognized = { onAction(KeyboardAction.CommitString(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.EMOJIS -> {
                        EmojiPickerView(
                            colors = colors,
                            onEmojiSelected = { onAction(KeyboardAction.InputChar(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.QUICK_TEXTS -> {
                        QuickTextsPanelView(
                            quickTextDao = quickTextDao,
                            colors = colors,
                            onInsertText = { onAction(KeyboardAction.CommitString(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.CLIPBOARD -> {
                        ClipboardPanelView(
                            clipboardDao = clipboardDao,
                            settings = settings,
                            colors = colors,
                            onInsertText = { onAction(KeyboardAction.CommitString(it)) },
                            onClose = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) }
                        )
                    }

                    KeyboardPanel.NONE -> {
                        // Display Active Keyboard Layout
                        when (layoutType) {
                            KeyboardLayoutType.QWERTY_LOWER,
                            KeyboardLayoutType.QWERTY_UPPER,
                            KeyboardLayoutType.QWERTY_CAPS_LOCK -> {
                                Column {
                                    if (isTermuxActive && settings.termuxLayoutMode != "normal") {
                                        TermuxExpandedPanel(
                                            settings = settings,
                                            onAction = onAction
                                        )
                                    }
                                    QwertyKeyboardView(
                                        settings = settings,
                                        layoutType = layoutType,
                                        imeAction = imeAction,
                                        onAction = onAction
                                    )
                                }
                            }

                            KeyboardLayoutType.SYMBOLS_1,
                            KeyboardLayoutType.SYMBOLS_2 -> {
                                SymbolsKeyboardView(
                                    settings = settings,
                                    layoutType = layoutType,
                                    onAction = onAction
                                )
                            }

                            KeyboardLayoutType.TERMINAL -> {
                                Column {
                                    TermuxExpandedPanel(
                                        settings = settings,
                                        onAction = onAction
                                    )
                                    QwertyKeyboardView(
                                        settings = settings,
                                        layoutType = KeyboardLayoutType.QWERTY_LOWER,
                                        imeAction = imeAction,
                                        onAction = onAction
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (settings.oneHandedMode == "left") {
                Spacer(modifier = Modifier.weight(0.15f))
            }
        }
    }
}
