package com.example.ime.view

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardSettings
import com.example.ime.KeyboardAction
import com.example.ime.KeyboardLayoutType

@Composable
fun SymbolsKeyboardView(
    settings: KeyboardSettings,
    layoutType: KeyboardLayoutType,
    onAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()
    val isPage2 = layoutType == KeyboardLayoutType.SYMBOLS_2

    val row1 = if (!isPage2) {
        listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
    } else {
        listOf("~", "`", "|", "•", "√", "π", "÷", "×", "¶", "∆")
    }

    val row2 = if (!isPage2) {
        listOf("@", "#", "$", "%", "&", "*", "-", "+", "(", ")")
    } else {
        listOf("£", "€", "$", "¥", "¢", "^", "°", "=", "{", "}")
    }

    val row3 = if (!isPage2) {
        listOf("!", "\"", "'", ":", ";", "/", "?")
    } else {
        listOf("\\", "%", "©", "®", "™", "[", "]", "<", ">")
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
    ) {
        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            row1.forEach { char ->
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = char,
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(char)) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            row2.forEach { char ->
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = char,
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(char)) }
                )
            }
        }

        // Row 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            // Page toggle button 1/2 or 2/2
            KeyButton(
                modifier = Modifier.weight(1.5f),
                text = if (!isPage2) "=\\<" else "1/2",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = 14.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                isActiveToggle = isPage2,
                onClick = {
                    onAction(
                        KeyboardAction.SwitchLayout(
                            if (!isPage2) KeyboardLayoutType.SYMBOLS_2 else KeyboardLayoutType.SYMBOLS_1
                        )
                    )
                }
            )

            row3.forEach { char ->
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = char,
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(char)) }
                )
            }

            KeyButton(
                modifier = Modifier.weight(1.5f),
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                colors = colors,
                height = settings.keyHeightDp.dp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.Backspace) }
            )
        }

        // Row 4
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            // Return to ABC
            KeyButton(
                modifier = Modifier.weight(1.5f),
                text = "ABC",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = 14.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.SwitchLayout(KeyboardLayoutType.QWERTY_LOWER)) }
            )

            KeyButton(
                modifier = Modifier.weight(1f),
                text = ",",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = settings.keyFontSizeSp.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.InputChar(",")) }
            )

            // Space
            KeyButton(
                modifier = Modifier.weight(4.5f),
                text = "Espaço",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = 13.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.Space) }
            )

            KeyButton(
                modifier = Modifier.weight(1f),
                text = ".",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = settings.keyFontSizeSp.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.InputChar(".")) }
            )

            KeyButton(
                modifier = Modifier.weight(1.5f),
                icon = Icons.Default.KeyboardReturn,
                colors = colors,
                height = settings.keyHeightDp.dp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                isHighlighted = true,
                onClick = { onAction(KeyboardAction.Enter) }
            )
        }
    }
}
