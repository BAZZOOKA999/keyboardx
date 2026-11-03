package com.example.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardThemePreset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dataStore by preferencesDataStore(name = "keyboardx_settings")

class DataStoreManager(private val context: Context) {

    private object PreferencesKeys {
        val THEME_PRESET = stringPreferencesKey("theme_preset")
        val CUSTOM_BG_COLOR = longPreferencesKey("custom_bg_color")
        val CUSTOM_KEY_COLOR = longPreferencesKey("custom_key_color")
        val CUSTOM_ACCENT_COLOR = longPreferencesKey("custom_accent_color")
        val CUSTOM_TEXT_COLOR = longPreferencesKey("custom_text_color")
        val KEY_HEIGHT = intPreferencesKey("key_height")
        val KEY_FONT_SIZE = intPreferencesKey("key_font_size")
        val KEY_RADIUS = intPreferencesKey("key_radius")
        val KEY_SPACING = intPreferencesKey("key_spacing")
        val BACKGROUND_OPACITY = floatPreferencesKey("background_opacity")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val SOUND_VOLUME = floatPreferencesKey("sound_volume")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val VIBRATION_INTENSITY = intPreferencesKey("vibration_intensity")
        val NUMBER_ROW_PERMANENT = booleanPreferencesKey("number_row_permanent")
        val ONE_HANDED_MODE = stringPreferencesKey("one_handed_mode")
        val AUTOCORRECT_ENABLED = booleanPreferencesKey("autocorrect_enabled")
        val SUGGESTIONS_ENABLED = booleanPreferencesKey("suggestions_enabled")
        val SWIPE_CURSOR_ENABLED = booleanPreferencesKey("swipe_cursor_enabled")
        val CURRENT_LANGUAGE = stringPreferencesKey("current_language")
        val TERMUX_MODE_ENABLED = booleanPreferencesKey("termux_mode_enabled")
        val TERMUX_EXTRA_ROW = booleanPreferencesKey("termux_extra_row")
        val TERMUX_LAYOUT_MODE = stringPreferencesKey("termux_layout_mode")
        val PRIVACY_MODE = booleanPreferencesKey("privacy_mode")
        val CLIPBOARD_HISTORY_ENABLED = booleanPreferencesKey("clipboard_history_enabled")
        val AUTO_EXPAND_SHORTCUTS = booleanPreferencesKey("auto_expand_shortcuts")
    }

    val settingsFlow: Flow<KeyboardSettings> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val presetStr = preferences[PreferencesKeys.THEME_PRESET] ?: KeyboardThemePreset.DARK_RED.id
            val preset = KeyboardThemePreset.values().find { it.id == presetStr } ?: KeyboardThemePreset.DARK_RED

            KeyboardSettings(
                themePreset = preset,
                customBgColorHex = preferences[PreferencesKeys.CUSTOM_BG_COLOR] ?: 0xFF101014,
                customKeyColorHex = preferences[PreferencesKeys.CUSTOM_KEY_COLOR] ?: 0xFF29292F,
                customAccentColorHex = preferences[PreferencesKeys.CUSTOM_ACCENT_COLOR] ?: 0xFFDC2626,
                customTextColorHex = preferences[PreferencesKeys.CUSTOM_TEXT_COLOR] ?: 0xFFFFFFFF,
                keyHeightDp = preferences[PreferencesKeys.KEY_HEIGHT] ?: 48,
                keyFontSizeSp = preferences[PreferencesKeys.KEY_FONT_SIZE] ?: 18,
                keyCornerRadiusDp = preferences[PreferencesKeys.KEY_RADIUS] ?: 8,
                keySpacingDp = preferences[PreferencesKeys.KEY_SPACING] ?: 4,
                backgroundOpacity = preferences[PreferencesKeys.BACKGROUND_OPACITY] ?: 1.0f,
                soundEnabled = preferences[PreferencesKeys.SOUND_ENABLED] ?: true,
                soundVolume = preferences[PreferencesKeys.SOUND_VOLUME] ?: 0.5f,
                vibrationEnabled = preferences[PreferencesKeys.VIBRATION_ENABLED] ?: true,
                vibrationIntensityMs = preferences[PreferencesKeys.VIBRATION_INTENSITY] ?: 30,
                numberRowPermanent = preferences[PreferencesKeys.NUMBER_ROW_PERMANENT] ?: true,
                oneHandedMode = preferences[PreferencesKeys.ONE_HANDED_MODE] ?: "off",
                autocorrectEnabled = preferences[PreferencesKeys.AUTOCORRECT_ENABLED] ?: true,
                suggestionsEnabled = preferences[PreferencesKeys.SUGGESTIONS_ENABLED] ?: true,
                swipeCursorEnabled = preferences[PreferencesKeys.SWIPE_CURSOR_ENABLED] ?: true,
                currentLanguage = preferences[PreferencesKeys.CURRENT_LANGUAGE] ?: "pt_BR",
                termuxModeEnabled = preferences[PreferencesKeys.TERMUX_MODE_ENABLED] ?: false,
                termuxExtraRowEnabled = preferences[PreferencesKeys.TERMUX_EXTRA_ROW] ?: true,
                termuxLayoutMode = preferences[PreferencesKeys.TERMUX_LAYOUT_MODE] ?: "normal",
                privacyMode = preferences[PreferencesKeys.PRIVACY_MODE] ?: false,
                clipboardHistoryEnabled = preferences[PreferencesKeys.CLIPBOARD_HISTORY_ENABLED] ?: false,
                autoExpandShortcuts = preferences[PreferencesKeys.AUTO_EXPAND_SHORTCUTS] ?: true
            )
        }

    suspend fun updateSettings(settings: KeyboardSettings) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_PRESET] = settings.themePreset.id
            preferences[PreferencesKeys.CUSTOM_BG_COLOR] = settings.customBgColorHex
            preferences[PreferencesKeys.CUSTOM_KEY_COLOR] = settings.customKeyColorHex
            preferences[PreferencesKeys.CUSTOM_ACCENT_COLOR] = settings.customAccentColorHex
            preferences[PreferencesKeys.CUSTOM_TEXT_COLOR] = settings.customTextColorHex
            preferences[PreferencesKeys.KEY_HEIGHT] = settings.keyHeightDp
            preferences[PreferencesKeys.KEY_FONT_SIZE] = settings.keyFontSizeSp
            preferences[PreferencesKeys.KEY_RADIUS] = settings.keyCornerRadiusDp
            preferences[PreferencesKeys.KEY_SPACING] = settings.keySpacingDp
            preferences[PreferencesKeys.BACKGROUND_OPACITY] = settings.backgroundOpacity
            preferences[PreferencesKeys.SOUND_ENABLED] = settings.soundEnabled
            preferences[PreferencesKeys.SOUND_VOLUME] = settings.soundVolume
            preferences[PreferencesKeys.VIBRATION_ENABLED] = settings.vibrationEnabled
            preferences[PreferencesKeys.VIBRATION_INTENSITY] = settings.vibrationIntensityMs
            preferences[PreferencesKeys.NUMBER_ROW_PERMANENT] = settings.numberRowPermanent
            preferences[PreferencesKeys.ONE_HANDED_MODE] = settings.oneHandedMode
            preferences[PreferencesKeys.AUTOCORRECT_ENABLED] = settings.autocorrectEnabled
            preferences[PreferencesKeys.SUGGESTIONS_ENABLED] = settings.suggestionsEnabled
            preferences[PreferencesKeys.SWIPE_CURSOR_ENABLED] = settings.swipeCursorEnabled
            preferences[PreferencesKeys.CURRENT_LANGUAGE] = settings.currentLanguage
            preferences[PreferencesKeys.TERMUX_MODE_ENABLED] = settings.termuxModeEnabled
            preferences[PreferencesKeys.TERMUX_EXTRA_ROW] = settings.termuxExtraRowEnabled
            preferences[PreferencesKeys.TERMUX_LAYOUT_MODE] = settings.termuxLayoutMode
            preferences[PreferencesKeys.PRIVACY_MODE] = settings.privacyMode
            preferences[PreferencesKeys.CLIPBOARD_HISTORY_ENABLED] = settings.clipboardHistoryEnabled
            preferences[PreferencesKeys.AUTO_EXPAND_SHORTCUTS] = settings.autoExpandShortcuts
        }
    }
}
