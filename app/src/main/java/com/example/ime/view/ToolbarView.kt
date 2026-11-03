package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardThemeColors
import com.example.ime.KeyboardAction
import com.example.ime.KeyboardPanel

@Composable
fun ToolbarView(
    settings: KeyboardSettings,
    activePanel: KeyboardPanel,
    isTermuxActive: Boolean,
    isSensitiveField: Boolean,
    onAction: (KeyboardAction) -> Unit,
    onToggleTermux: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(colors.background)
            .horizontalScroll(scrollState)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (activePanel != KeyboardPanel.NONE) {
            // Close active panel button
            IconButton(
                onClick = { onAction(KeyboardAction.SwitchPanel(KeyboardPanel.NONE)) },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(colors.accent)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar Painel",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // AI Assistant Button (Only when not in sensitive password field)
        if (!isSensitiveField) {
            ToolbarIconButton(
                icon = Icons.Default.AutoAwesome,
                label = "IA",
                isActive = activePanel == KeyboardPanel.AI,
                colors = colors,
                onClick = {
                    onAction(
                        KeyboardAction.SwitchPanel(
                            if (activePanel == KeyboardPanel.AI) KeyboardPanel.NONE else KeyboardPanel.AI
                        )
                    )
                }
            )

            // ML Kit Translator Button
            ToolbarIconButton(
                icon = Icons.Default.Translate,
                label = "Traduzir",
                isActive = activePanel == KeyboardPanel.TRANSLATOR,
                colors = colors,
                onClick = {
                    onAction(
                        KeyboardAction.SwitchPanel(
                            if (activePanel == KeyboardPanel.TRANSLATOR) KeyboardPanel.NONE else KeyboardPanel.TRANSLATOR
                        )
                    )
                }
            )
        }

        // Voice Typing Button
        ToolbarIconButton(
            icon = Icons.Default.Mic,
            label = "Voz",
            isActive = activePanel == KeyboardPanel.VOICE,
            colors = colors,
            onClick = {
                onAction(
                    KeyboardAction.SwitchPanel(
                        if (activePanel == KeyboardPanel.VOICE) KeyboardPanel.NONE else KeyboardPanel.VOICE
                    )
                )
            }
        )

        // Termux Mode Toggle
        ToolbarIconButton(
            icon = Icons.Default.Terminal,
            label = "Termux",
            isActive = isTermuxActive,
            colors = colors,
            onClick = onToggleTermux
        )

        // Quick Texts (Meus Textos)
        ToolbarIconButton(
            icon = Icons.Default.FormatQuote,
            label = "Modelos",
            isActive = activePanel == KeyboardPanel.QUICK_TEXTS,
            colors = colors,
            onClick = {
                onAction(
                    KeyboardAction.SwitchPanel(
                        if (activePanel == KeyboardPanel.QUICK_TEXTS) KeyboardPanel.NONE else KeyboardPanel.QUICK_TEXTS
                    )
                )
            }
        )

        // Clipboard Panel (Only if not sensitive)
        if (!isSensitiveField) {
            ToolbarIconButton(
                icon = Icons.Default.ContentPaste,
                label = "Clipboard",
                isActive = activePanel == KeyboardPanel.CLIPBOARD,
                colors = colors,
                onClick = {
                    onAction(
                        KeyboardAction.SwitchPanel(
                            if (activePanel == KeyboardPanel.CLIPBOARD) KeyboardPanel.NONE else KeyboardPanel.CLIPBOARD
                        )
                    )
                }
            )
        }

        // Text editing actions
        ToolbarIconButton(
            icon = Icons.Default.SelectAll,
            label = "Sel. Tudo",
            colors = colors,
            onClick = { onAction(KeyboardAction.SelectAll) }
        )

        ToolbarIconButton(
            icon = Icons.Default.ContentCopy,
            label = "Copiar",
            colors = colors,
            onClick = { onAction(KeyboardAction.Copy) }
        )

        ToolbarIconButton(
            icon = Icons.Default.ContentCut,
            label = "Recortar",
            colors = colors,
            onClick = { onAction(KeyboardAction.Cut) }
        )

        ToolbarIconButton(
            icon = Icons.Default.ContentPasteGo,
            label = "Colar",
            colors = colors,
            onClick = { onAction(KeyboardAction.Paste) }
        )

        ToolbarIconButton(
            icon = Icons.AutoMirrored.Filled.Undo,
            label = "Desfazer",
            colors = colors,
            onClick = { onAction(KeyboardAction.Undo) }
        )

        ToolbarIconButton(
            icon = Icons.AutoMirrored.Filled.Redo,
            label = "Refazer",
            colors = colors,
            onClick = { onAction(KeyboardAction.Redo) }
        )

        // Dismiss keyboard
        ToolbarIconButton(
            icon = Icons.Default.KeyboardHide,
            label = "Ocultar",
            colors = colors,
            onClick = { onAction(KeyboardAction.HideKeyboard) }
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    label: String,
    colors: KeyboardThemeColors,
    isActive: Boolean = false,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isActive) colors.accent else colors.keyBackground)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) Color.White else colors.keyText,
            modifier = Modifier.size(18.dp)
        )
    }
}
