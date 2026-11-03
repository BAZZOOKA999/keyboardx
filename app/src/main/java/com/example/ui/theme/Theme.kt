package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val KeyBoardXColorScheme = darkColorScheme(
    primary = KeyBoardXRed,
    onPrimary = Color.White,
    primaryContainer = KeyBoardXCard,
    onPrimaryContainer = Color.White,
    secondary = KeyBoardXRedGlow,
    onSecondary = Color.White,
    secondaryContainer = KeyBoardXKeyBg,
    onSecondaryContainer = KeyBoardXText,
    tertiary = KeyBoardXSuccess,
    background = KeyBoardXBackground,
    onBackground = KeyBoardXText,
    surface = KeyBoardXSurface,
    onSurface = KeyBoardXText,
    surfaceVariant = KeyBoardXCard,
    onSurfaceVariant = KeyBoardXSecondaryText,
    outline = KeyBoardXBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KeyBoardXColorScheme,
        typography = Typography,
        content = content
    )
}
