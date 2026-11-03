package com.example.ime

enum class KeyboardLayoutType {
    QWERTY_LOWER,
    QWERTY_UPPER,
    QWERTY_CAPS_LOCK,
    SYMBOLS_1,
    SYMBOLS_2,
    TERMINAL
}

enum class KeyboardPanel {
    NONE,
    AI,
    TRANSLATOR,
    VOICE,
    EMOJIS,
    QUICK_TEXTS,
    CLIPBOARD
}

sealed interface KeyboardAction {
    data class InputChar(val char: String) : KeyboardAction
    object Backspace : KeyboardAction
    object Space : KeyboardAction
    object Enter : KeyboardAction
    object ToggleShift : KeyboardAction
    object CapsLock : KeyboardAction
    data class SwitchLayout(val layout: KeyboardLayoutType) : KeyboardAction
    data class SwitchPanel(val panel: KeyboardPanel) : KeyboardAction
    data class SendKeyEvent(val keyCode: Int, val metaState: Int = 0) : KeyboardAction
    data class SendShortcut(val keyCode: Int, val metaCtrl: Boolean = true, val metaAlt: Boolean = false) : KeyboardAction
    data class CommitString(val text: String) : KeyboardAction
    data class MoveCursor(val offset: Int) : KeyboardAction
    object SelectAll : KeyboardAction
    object Copy : KeyboardAction
    object Cut : KeyboardAction
    object Paste : KeyboardAction
    object Undo : KeyboardAction
    object Redo : KeyboardAction
    object HideKeyboard : KeyboardAction
}
