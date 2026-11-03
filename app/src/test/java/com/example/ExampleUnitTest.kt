package com.example

import com.example.ai.AITool
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardThemeColors
import com.example.data.model.KeyboardThemePreset
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testKeyboardDefaultSettings() {
        val settings = KeyboardSettings()
        assertEquals(KeyboardThemePreset.DARK_RED, settings.themePreset)
        assertEquals(48, settings.keyHeightDp)
        assertEquals(18, settings.keyFontSizeSp)
        assertTrue(settings.numberRowPermanent)
        assertTrue(settings.autocorrectEnabled)
        assertTrue(settings.suggestionsEnabled)
        assertFalse(settings.clipboardHistoryEnabled) // Strictly disabled by default
        assertFalse(settings.privacyMode)
    }

    @Test
    fun testThemeColors() {
        val darkRed = KeyboardSettings(themePreset = KeyboardThemePreset.DARK_RED).getColors()
        assertEquals(KeyboardThemeColors.DarkRed.background, darkRed.background)
        assertEquals(KeyboardThemeColors.DarkRed.accent, darkRed.accent)

        val pureBlack = KeyboardSettings(themePreset = KeyboardThemePreset.PURE_BLACK).getColors()
        assertEquals(KeyboardThemeColors.PureBlack.background, pureBlack.background)
    }

    @Test
    fun testAIToolsConfigured() {
        assertTrue(AITool.values().isNotEmpty())
        val grammarTool = AITool.GRAMMAR
        assertTrue(grammarTool.promptInstruction.isNotBlank())
        assertEquals("Corrigir Ortografia", grammarTool.title)
    }
}
