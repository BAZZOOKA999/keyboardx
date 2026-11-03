package com.example.ime.view

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardThemeColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun KeyButton(
    modifier: Modifier = Modifier,
    text: String? = null,
    icon: ImageVector? = null,
    subText: String? = null,
    colors: KeyboardThemeColors,
    height: Dp = 48.dp,
    fontSize: TextUnit = 18.sp,
    cornerRadius: Dp = 8.dp,
    isHighlighted: Boolean = false,
    isActiveToggle: Boolean = false,
    accents: List<String> = emptyList(),
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    onAccentSelected: ((String) -> Unit)? = null
) {
    val view = LocalView.current
    var isPressed by remember { mutableStateOf(false) }
    var showAccentsMenu by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(targetValue = if (isPressed) 0.92f else 1.0f, label = "key_scale")

    val bg = when {
        isActiveToggle -> colors.accent
        isHighlighted -> colors.accent
        else -> colors.keyBackground
    }

    val contentColor = when {
        isActiveToggle -> Color.White
        isHighlighted -> Color.White
        else -> colors.keyText
    }

    Box(
        modifier = modifier
            .height(height)
            .scale(scale)
            .clip(RoundedCornerShape(cornerRadius))
            .background(bg)
            .combinedClickable(
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    onClick()
                },
                onLongClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                    if (accents.isNotEmpty()) {
                        showAccentsMenu = true
                    } else if (onLongClick != null) {
                        onLongClick()
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (text != null) {
            Text(
                text = text,
                color = contentColor,
                fontSize = fontSize,
                fontWeight = if (isHighlighted || isActiveToggle) FontWeight.Bold else FontWeight.Medium
            )
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
        }

        if (subText != null && text != null && text.length == 1) {
            Text(
                text = subText,
                color = colors.secondaryText,
                fontSize = 10.sp,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 4.dp, top = 2.dp)
            )
        }

        if (accents.isNotEmpty()) {
            DropdownMenu(
                expanded = showAccentsMenu,
                onDismissRequest = { showAccentsMenu = false },
                modifier = Modifier.background(colors.background)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    accents.forEach { accentChar ->
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(colors.keyBackground)
                                .combinedClickable {
                                    showAccentsMenu = false
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    onAccentSelected?.invoke(accentChar) ?: onClick()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = accentChar,
                                color = colors.keyText,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
