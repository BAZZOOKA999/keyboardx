package com.example.ime.view

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardSettings
import com.example.ime.KeyboardAction

@Composable
fun TermuxTopRow(
    settings: KeyboardSettings,
    isCtrlActive: Boolean,
    isAltActive: Boolean,
    onToggleCtrl: () -> Unit,
    onToggleAlt: () -> Unit,
    onAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ESC
        KeyButton(
            modifier = Modifier.width(46.dp),
            text = "ESC",
            colors = colors,
            height = 36.dp,
            fontSize = 12.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_ESCAPE)) }
        )

        // TAB
        KeyButton(
            modifier = Modifier.width(46.dp),
            text = "TAB",
            colors = colors,
            height = 36.dp,
            fontSize = 12.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_TAB)) }
        )

        // CTRL (Sticky modifier toggle)
        KeyButton(
            modifier = Modifier.width(50.dp),
            text = "CTRL",
            colors = colors,
            height = 36.dp,
            fontSize = 12.sp,
            isActiveToggle = isCtrlActive,
            onClick = onToggleCtrl
        )

        // ALT (Sticky modifier toggle)
        KeyButton(
            modifier = Modifier.width(46.dp),
            text = "ALT",
            colors = colors,
            height = 36.dp,
            fontSize = 12.sp,
            isActiveToggle = isAltActive,
            onClick = onToggleAlt
        )

        // Arrow UP
        KeyButton(
            modifier = Modifier.width(40.dp),
            text = "↑",
            colors = colors,
            height = 36.dp,
            fontSize = 16.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_DPAD_UP)) }
        )

        // Arrow DOWN
        KeyButton(
            modifier = Modifier.width(40.dp),
            text = "↓",
            colors = colors,
            height = 36.dp,
            fontSize = 16.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_DPAD_DOWN)) }
        )

        // Arrow LEFT
        KeyButton(
            modifier = Modifier.width(40.dp),
            text = "←",
            colors = colors,
            height = 36.dp,
            fontSize = 16.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_DPAD_LEFT)) }
        )

        // Arrow RIGHT
        KeyButton(
            modifier = Modifier.width(40.dp),
            text = "→",
            colors = colors,
            height = 36.dp,
            fontSize = 16.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_DPAD_RIGHT)) }
        )

        // HOME
        KeyButton(
            modifier = Modifier.width(48.dp),
            text = "HOME",
            colors = colors,
            height = 36.dp,
            fontSize = 11.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_MOVE_HOME)) }
        )

        // END
        KeyButton(
            modifier = Modifier.width(46.dp),
            text = "END",
            colors = colors,
            height = 36.dp,
            fontSize = 11.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_MOVE_END)) }
        )

        // PGUP
        KeyButton(
            modifier = Modifier.width(48.dp),
            text = "PGUP",
            colors = colors,
            height = 36.dp,
            fontSize = 11.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_PAGE_UP)) }
        )

        // PGDN
        KeyButton(
            modifier = Modifier.width(48.dp),
            text = "PGDN",
            colors = colors,
            height = 36.dp,
            fontSize = 11.sp,
            onClick = { onAction(KeyboardAction.SendKeyEvent(KeyEvent.KEYCODE_PAGE_DOWN)) }
        )
    }
}

@Composable
fun TermuxExpandedPanel(
    settings: KeyboardSettings,
    onAction: (KeyboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = settings.getColors()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Shortcuts, 1: Symbols, 2: F1-F12

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.termuxKeyBg)
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        // Mode Tabs: Shortcuts, Symbols, Function Keys
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("Atalhos Ctrl", "Símbolos Dev", "Teclas F1–F12")
            tabs.forEachIndexed { index, title ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (selectedTab == index) colors.accent else colors.keyBackground)
                        .clickable { selectedTab = index },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Common Terminal Shortcuts: Ctrl+C, Ctrl+Z, Ctrl+A, Ctrl+D, Ctrl+L, Ctrl+X, Ctrl+V, Ctrl+W, Ctrl+E
                val shortcuts = listOf(
                    Pair("Ctrl+C (Interromper)", KeyEvent.KEYCODE_C),
                    Pair("Ctrl+Z (Suspender)", KeyEvent.KEYCODE_Z),
                    Pair("Ctrl+D (EOF/Sair)", KeyEvent.KEYCODE_D),
                    Pair("Ctrl+L (Limpar)", KeyEvent.KEYCODE_L),
                    Pair("Ctrl+A (Início)", KeyEvent.KEYCODE_A),
                    Pair("Ctrl+E (Fim)", KeyEvent.KEYCODE_E),
                    Pair("Ctrl+X (Cortar)", KeyEvent.KEYCODE_X),
                    Pair("Ctrl+V (Colar)", KeyEvent.KEYCODE_V),
                    Pair("Ctrl+W (Del Palavra)", KeyEvent.KEYCODE_W)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    shortcuts.forEach { (label, keyCode) ->
                        KeyButton(
                            modifier = Modifier.width(110.dp),
                            text = label,
                            colors = colors,
                            height = 36.dp,
                            fontSize = 11.sp,
                            onClick = { onAction(KeyboardAction.SendShortcut(keyCode, metaCtrl = true)) }
                        )
                    }
                }
            }

            1 -> {
                // Programming symbols: "/", "\", "|", "~", "$", "#", "&", "*", "{}", "[]", "()", "<>", "=", "_", "-", ":", ";", "'", "\"", "`"
                val devSymbols = listOf(
                    "/", "\\", "|", "~", "$", "#", "&", "*", "{}", "[]", "()", "<>", "=", "_", "-", ":", ";", "'", "\"", "`"
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    devSymbols.forEach { sym ->
                        KeyButton(
                            modifier = Modifier.width(42.dp),
                            text = sym,
                            colors = colors,
                            height = 36.dp,
                            fontSize = 14.sp,
                            onClick = {
                                if (sym == "{}" || sym == "[]" || sym == "()" || sym == "<>") {
                                    onAction(KeyboardAction.CommitString(sym))
                                    onAction(KeyboardAction.MoveCursor(-1))
                                } else {
                                    onAction(KeyboardAction.InputChar(sym))
                                }
                            }
                        )
                    }
                }
            }

            2 -> {
                // Function Keys F1 - F12
                val fKeys = listOf(
                    Pair("F1", KeyEvent.KEYCODE_F1),
                    Pair("F2", KeyEvent.KEYCODE_F2),
                    Pair("F3", KeyEvent.KEYCODE_F3),
                    Pair("F4", KeyEvent.KEYCODE_F4),
                    Pair("F5", KeyEvent.KEYCODE_F5),
                    Pair("F6", KeyEvent.KEYCODE_F6),
                    Pair("F7", KeyEvent.KEYCODE_F7),
                    Pair("F8", KeyEvent.KEYCODE_F8),
                    Pair("F9", KeyEvent.KEYCODE_F9),
                    Pair("F10", KeyEvent.KEYCODE_F10),
                    Pair("F11", KeyEvent.KEYCODE_F11),
                    Pair("F12", KeyEvent.KEYCODE_F12)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    fKeys.forEach { (label, keyCode) ->
                        KeyButton(
                            modifier = Modifier.width(46.dp),
                            text = label,
                            colors = colors,
                            height = 36.dp,
                            fontSize = 12.sp,
                            onClick = { onAction(KeyboardAction.SendKeyEvent(keyCode)) }
                        )
                    }
                }
            }
        }
    }
}
