package com.example.ime.view

import android.view.HapticFeedbackConstants
import android.view.inputmethod.EditorInfo
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardSettings
import com.example.ime.KeyboardAction
import com.example.ime.KeyboardLayoutType
import com.example.ime.KeyboardPanel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QwertyKeyboardView(
    settings: KeyboardSettings,
    layoutType: KeyboardLayoutType,
    imeAction: Int,
    onAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()
    val isUpper = layoutType == KeyboardLayoutType.QWERTY_UPPER || layoutType == KeyboardLayoutType.QWERTY_CAPS_LOCK
    val isCapsLock = layoutType == KeyboardLayoutType.QWERTY_CAPS_LOCK
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    val row1 = listOf("q", "w", "e", "r", "t", "y", "u", "i", "o", "p")
    val row2 = if (settings.currentLanguage == "pt_BR") {
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l", "ç")
    } else {
        listOf("a", "s", "d", "f", "g", "h", "j", "k", "l")
    }
    val row3 = if (settings.currentLanguage == "es_ES") {
        listOf("z", "x", "c", "v", "b", "n", "m", "ñ")
    } else {
        listOf("z", "x", "c", "v", "b", "n", "m")
    }

    val numberSubTexts = mapOf(
        "q" to "1", "w" to "2", "e" to "3", "r" to "4", "t" to "5",
        "y" to "6", "u" to "7", "i" to "8", "o" to "9", "p" to "0"
    )

    fun getAccents(char: String): List<String> {
        val lower = char.lowercase()
        val list = when (lower) {
            "a" -> listOf("á", "à", "ã", "â", "ä")
            "e" -> listOf("é", "ê", "è", "ë")
            "i" -> listOf("í", "î", "ì", "ï")
            "o" -> listOf("ó", "ô", "õ", "ò", "ö")
            "u" -> listOf("ú", "û", "ù", "ü")
            "c" -> listOf("ç")
            "n" -> listOf("ñ")
            else -> emptyList()
        }
        return if (isUpper) list.map { it.uppercase() } else list
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
    ) {
        // Optional Permanent Number Row
        if (settings.numberRowPermanent) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
            ) {
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0").forEach { num ->
                    KeyButton(
                        modifier = Modifier.weight(1f),
                        text = num,
                        colors = colors,
                        height = (settings.keyHeightDp - 6).coerceAtLeast(36).dp,
                        fontSize = (settings.keyFontSizeSp - 2).sp,
                        cornerRadius = settings.keyCornerRadiusDp.dp,
                        onClick = { onAction(KeyboardAction.InputChar(num)) }
                    )
                }
            }
        }

        // Row 1
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            row1.forEach { letter ->
                val display = if (isUpper) letter.uppercase() else letter
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = display,
                    subText = if (!settings.numberRowPermanent) numberSubTexts[letter] else null,
                    accents = getAccents(letter),
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(display)) },
                    onAccentSelected = { onAction(KeyboardAction.InputChar(it)) }
                )
            }
        }

        // Row 2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            if (row2.size < 10) {
                Spacer(modifier = Modifier.weight(0.5f))
            }
            row2.forEach { letter ->
                val display = if (isUpper) letter.uppercase() else letter
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = display,
                    accents = getAccents(letter),
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(display)) },
                    onAccentSelected = { onAction(KeyboardAction.InputChar(it)) }
                )
            }
            if (row2.size < 10) {
                Spacer(modifier = Modifier.weight(0.5f))
            }
        }

        // Row 3: Shift, letters, Backspace
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
        ) {
            // Shift / Capslock Button
            KeyButton(
                modifier = Modifier.weight(1.5f),
                icon = if (isCapsLock) Icons.Default.KeyboardCapslock else Icons.Default.ArrowUpward,
                colors = colors,
                height = settings.keyHeightDp.dp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                isActiveToggle = isUpper,
                isHighlighted = isCapsLock,
                onClick = { onAction(KeyboardAction.ToggleShift) },
                onLongClick = { onAction(KeyboardAction.CapsLock) }
            )

            row3.forEach { letter ->
                val display = if (isUpper) letter.uppercase() else letter
                KeyButton(
                    modifier = Modifier.weight(1f),
                    text = display,
                    accents = getAccents(letter),
                    colors = colors,
                    height = settings.keyHeightDp.dp,
                    fontSize = settings.keyFontSizeSp.sp,
                    cornerRadius = settings.keyCornerRadiusDp.dp,
                    onClick = { onAction(KeyboardAction.InputChar(display)) },
                    onAccentSelected = { onAction(KeyboardAction.InputChar(it)) }
                )
            }

            // Backspace with repeat on hold
            var isHoldingBackspace by remember { mutableStateOf(false) }
            DisposableEffect(isHoldingBackspace) {
                val job = if (isHoldingBackspace) {
                    coroutineScope.launch {
                        delay(350)
                        while (isActive && isHoldingBackspace) {
                            onAction(KeyboardAction.Backspace)
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            delay(60)
                        }
                    }
                } else null
                onDispose { job?.cancel() }
            }

            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .height(settings.keyHeightDp.dp)
                    .clip(RoundedCornerShape(settings.keyCornerRadiusDp.dp))
                    .background(colors.keyBackground)
                    .combinedClickable(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onAction(KeyboardAction.Backspace)
                        },
                        onLongClick = {
                            isHoldingBackspace = true
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Backspace",
                    tint = colors.keyText,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        // Row 4: ?123, Emoji, Comma, Space (with swipe cursor), Period, Enter
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Symbols Switch
            KeyButton(
                modifier = Modifier.weight(1.3f),
                text = "?123",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = 14.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.SwitchLayout(KeyboardLayoutType.SYMBOLS_1)) }
            )

            // Emoji
            KeyButton(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.SentimentSatisfiedAlt,
                colors = colors,
                height = settings.keyHeightDp.dp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.EMOJIS)) }
            )

            // Comma
            KeyButton(
                modifier = Modifier.weight(0.9f),
                text = ",",
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = settings.keyFontSizeSp.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.InputChar(",")) }
            )

            // Space Bar with Swipe to Move Cursor
            var dragDistance by remember { mutableFloatStateOf(0f) }
            val spaceThreshold = 35f

            Box(
                modifier = Modifier
                    .weight(4.0f)
                    .height(settings.keyHeightDp.dp)
                    .clip(RoundedCornerShape(settings.keyCornerRadiusDp.dp))
                    .background(colors.keyBackground)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (settings.swipeCursorEnabled) {
                                    dragDistance += dragAmount.x
                                    if (dragDistance > spaceThreshold) {
                                        onAction(KeyboardAction.MoveCursor(1))
                                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                        dragDistance = 0f
                                    } else if (dragDistance < -spaceThreshold) {
                                        onAction(KeyboardAction.MoveCursor(-1))
                                        view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
                                        dragDistance = 0f
                                    }
                                }
                            },
                            onDragEnd = { dragDistance = 0f },
                            onDragCancel = { dragDistance = 0f }
                        )
                    }
                    .combinedClickable(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            onAction(KeyboardAction.Space)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = when (settings.currentLanguage) {
                        "en_US" -> "English"
                        "es_ES" -> "Español"
                        else -> "Português"
                    },
                    color = colors.secondaryText,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Period
            KeyButton(
                modifier = Modifier.weight(0.9f),
                text = ".",
                accents = listOf(".", "!", "?", "-", ":", ";"),
                colors = colors,
                height = settings.keyHeightDp.dp,
                fontSize = settings.keyFontSizeSp.sp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                onClick = { onAction(KeyboardAction.InputChar(".")) },
                onAccentSelected = { onAction(KeyboardAction.InputChar(it)) }
            )

            // Enter Action (Search / Send / Go / Next / Done / Return)
            val enterIcon: ImageVector = when (imeAction) {
                EditorInfo.IME_ACTION_SEARCH -> Icons.Default.Search
                EditorInfo.IME_ACTION_SEND -> Icons.AutoMirrored.Filled.Send
                EditorInfo.IME_ACTION_GO -> Icons.Default.ChevronRight
                EditorInfo.IME_ACTION_NEXT -> Icons.Default.NavigateNext
                EditorInfo.IME_ACTION_DONE -> Icons.Default.Check
                else -> Icons.Default.KeyboardReturn
            }

            KeyButton(
                modifier = Modifier.weight(1.5f),
                icon = enterIcon,
                colors = colors,
                height = settings.keyHeightDp.dp,
                cornerRadius = settings.keyCornerRadiusDp.dp,
                isHighlighted = true,
                onClick = { onAction(KeyboardAction.Enter) }
            )
        }
    }
}
