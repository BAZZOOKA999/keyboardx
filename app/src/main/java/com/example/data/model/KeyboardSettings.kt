package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class KeyboardThemePreset(val id: String, val displayName: String) {
    DARK_RED("dark_red", "Dark Red (Padrão)"),
    PURE_BLACK("pure_black", "Pure Black (Minimalista)"),
    CUSTOM("custom", "Personalizado")
}

data class KeyboardThemeColors(
    val background: Color,
    val keyBackground: Color,
    val keyText: Color,
    val accent: Color,
    val secondaryText: Color,
    val termuxKeyBg: Color
) {
    companion object {
        val DarkRed = KeyboardThemeColors(
            background = Color(0xFF101014),
            keyBackground = Color(0xFF29292F),
            keyText = Color(0xFFFFFFFF),
            accent = Color(0xFFDC2626),
            secondaryText = Color(0xFFA1A1AA),
            termuxKeyBg = Color(0xFF1F1F24)
        )

        val PureBlack = KeyboardThemeColors(
            background = Color(0xFF000000),
            keyBackground = Color(0xFF1A1A1A),
            keyText = Color(0xFFFFFFFF),
            accent = Color(0xFFE5E5E5),
            secondaryText = Color(0xFF737373),
            termuxKeyBg = Color(0xFF121212)
        )
    }
}

data class KeyboardSettings(
    val themePreset: KeyboardThemePreset = KeyboardThemePreset.DARK_RED,
    val customBgColorHex: Long = 0xFF101014,
    val customKeyColorHex: Long = 0xFF29292F,
    val customAccentColorHex: Long = 0xFFDC2626,
    val customTextColorHex: Long = 0xFFFFFFFF,
    val keyHeightDp: Int = 48,
    val keyFontSizeSp: Int = 18,
    val keyCornerRadiusDp: Int = 8,
    val keySpacingDp: Int = 4,
    val backgroundOpacity: Float = 1.0f,
    val soundEnabled: Boolean = true,
    val soundVolume: Float = 0.5f,
    val vibrationEnabled: Boolean = true,
    val vibrationIntensityMs: Int = 30,
    val numberRowPermanent: Boolean = true,
    val oneHandedMode: String = "off", // "off", "left", "right"
    val autocorrectEnabled: Boolean = true,
    val suggestionsEnabled: Boolean = true,
    val swipeCursorEnabled: Boolean = true,
    val currentLanguage: String = "pt_BR", // "pt_BR", "en_US", "es_ES"
    val termuxModeEnabled: Boolean = false,
    val termuxExtraRowEnabled: Boolean = true,
    val termuxLayoutMode: String = "normal", // "normal", "terminal", "coding"
    val privacyMode: Boolean = false,
    val clipboardHistoryEnabled: Boolean = false, // Strictly disabled by default
    val autoExpandShortcuts: Boolean = true
) {
    fun getColors(): KeyboardThemeColors {
        return when (themePreset) {
            KeyboardThemePreset.DARK_RED -> KeyboardThemeColors.DarkRed
            KeyboardThemePreset.PURE_BLACK -> KeyboardThemeColors.PureBlack
            KeyboardThemePreset.CUSTOM -> KeyboardThemeColors(
                background = Color(customBgColorHex),
                keyBackground = Color(customKeyColorHex),
                keyText = Color(customTextColorHex),
                accent = Color(customAccentColorHex),
                secondaryText = Color(0xFFA1A1AA),
                termuxKeyBg = Color(customKeyColorHex)
            )
        }
    }
}
