package com.example.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardThemeColors
import com.example.data.model.KeyboardThemePreset
import com.example.ime.view.KeyButton
import com.example.ui.theme.*

@Composable
fun ThemeSettingsScreen(
    currentSettings: KeyboardSettings,
    onSaveSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(currentSettings) }
    val colors = settings.getColors()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Live Preview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(14.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRÉVIA EM TEMPO REAL",
                        color = KeyBoardXSecondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    // Mini Keyboard Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(colors.background.copy(alpha = settings.backgroundOpacity))
                            .padding(8.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
                            ) {
                                listOf("Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P").forEach { char ->
                                    KeyButton(
                                        modifier = Modifier.weight(1f),
                                        text = char,
                                        colors = colors,
                                        height = settings.keyHeightDp.dp,
                                        fontSize = settings.keyFontSizeSp.sp,
                                        cornerRadius = settings.keyCornerRadiusDp.dp,
                                        onClick = {}
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(settings.keySpacingDp.dp)
                            ) {
                                listOf("A", "S", "D", "F", "G", "H", "J", "K", "L", "Ç").forEach { char ->
                                    KeyButton(
                                        modifier = Modifier.weight(1f),
                                        text = char,
                                        colors = colors,
                                        height = settings.keyHeightDp.dp,
                                        fontSize = settings.keyFontSizeSp.sp,
                                        cornerRadius = settings.keyCornerRadiusDp.dp,
                                        onClick = {}
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Theme Preset Selector
        item {
            Text(
                text = "TEMA VISUAL",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KeyboardThemePreset.values().forEach { preset ->
                    val isSelected = settings.themePreset == preset
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KeyBoardXRed.copy(alpha = 0.2f) else KeyBoardXCard
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(
                                1.5.dp,
                                if (isSelected) KeyBoardXRed else KeyBoardXBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                val updated = settings.copy(themePreset = preset)
                                settings = updated
                                onSaveSettings(updated)
                            }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = preset.displayName,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = KeyBoardXRed,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Key Height Slider
        item {
            SettingSliderItem(
                label = "Altura das Teclas: ${settings.keyHeightDp} dp",
                value = settings.keyHeightDp.toFloat(),
                valueRange = 36f..64f,
                onValueChange = {
                    val updated = settings.copy(keyHeightDp = it.toInt())
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        // Font Size Slider
        item {
            SettingSliderItem(
                label = "Tamanho das Letras: ${settings.keyFontSizeSp} sp",
                value = settings.keyFontSizeSp.toFloat(),
                valueRange = 14f..26f,
                onValueChange = {
                    val updated = settings.copy(keyFontSizeSp = it.toInt())
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        // Corner Radius Slider
        item {
            SettingSliderItem(
                label = "Arredondamento dos Cantos: ${settings.keyCornerRadiusDp} dp",
                value = settings.keyCornerRadiusDp.toFloat(),
                valueRange = 0f..20f,
                onValueChange = {
                    val updated = settings.copy(keyCornerRadiusDp = it.toInt())
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        // Key Spacing Slider
        item {
            SettingSliderItem(
                label = "Espaçamento entre Teclas: ${settings.keySpacingDp} dp",
                value = settings.keySpacingDp.toFloat(),
                valueRange = 1f..8f,
                onValueChange = {
                    val updated = settings.copy(keySpacingDp = it.toInt())
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        // Opacity Slider
        item {
            SettingSliderItem(
                label = "Opacidade do Fundo: ${(settings.backgroundOpacity * 100).toInt()}%",
                value = settings.backgroundOpacity,
                valueRange = 0.4f..1.0f,
                onValueChange = {
                    val updated = settings.copy(backgroundOpacity = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }
    }
}

@Composable
fun SettingSliderItem(
    label: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, KeyBoardXBorder, RoundedCornerShape(10.dp))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = label,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                colors = SliderDefaults.colors(
                    thumbColor = KeyBoardXRed,
                    activeTrackColor = KeyBoardXRed,
                    inactiveTrackColor = KeyBoardXBorder
                )
            )
        }
    }
}

@Composable
fun TypingSettingsScreen(
    currentSettings: KeyboardSettings,
    onSaveSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(currentSettings) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "IDIOMA PRINCIPAL",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("pt_BR", "🇧🇷 Português"),
                    Pair("en_US", "🇺🇸 English"),
                    Pair("es_ES", "🇪🇸 Español")
                ).forEach { (code, name) ->
                    val isSelected = settings.currentLanguage == code
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KeyBoardXRed.copy(alpha = 0.2f) else KeyBoardXCard
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) KeyBoardXRed else KeyBoardXBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val updated = settings.copy(currentLanguage = code)
                                settings = updated
                                onSaveSettings(updated)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = name,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "RECURSOS DE DIGITAÇÃO",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            SettingToggleItem(
                title = "Autocorreção Local",
                subtitle = "Corrige automaticamente erros frequentes sem envio a servidores.",
                checked = settings.autocorrectEnabled,
                onCheckedChange = {
                    val updated = settings.copy(autocorrectEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Sugestões de Palavras",
                subtitle = "Exibe barra horizontal com próximas palavras prováveis.",
                checked = settings.suggestionsEnabled,
                onCheckedChange = {
                    val updated = settings.copy(suggestionsEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Linha de Números Permanente",
                subtitle = "Mantém 1-0 sempre visíveis no topo do teclado.",
                checked = settings.numberRowPermanent,
                onCheckedChange = {
                    val updated = settings.copy(numberRowPermanent = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Gesto na Barra de Espaço",
                subtitle = "Deslize o dedo sobre a barra de espaço para movimentar o cursor com precisão.",
                checked = settings.swipeCursorEnabled,
                onCheckedChange = {
                    val updated = settings.copy(swipeCursorEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Expansão Automática de Atalhos",
                subtitle = "Transforma atalhos como 'meuemail' no texto completo ao pressionar espaço.",
                checked = settings.autoExpandShortcuts,
                onCheckedChange = {
                    val updated = settings.copy(autoExpandShortcuts = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            Text(
                text = "SOM & VIBRAÇÃO",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            SettingToggleItem(
                title = "Som ao Digitar",
                subtitle = "Feedback auditivo de clique de tecla.",
                checked = settings.soundEnabled,
                onCheckedChange = {
                    val updated = settings.copy(soundEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Vibração Tátil",
                subtitle = "Feedback háptico a cada toque.",
                checked = settings.vibrationEnabled,
                onCheckedChange = {
                    val updated = settings.copy(vibrationEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingSliderItem(
                label = "Intensidade da Vibração: ${settings.vibrationIntensityMs} ms",
                value = settings.vibrationIntensityMs.toFloat(),
                valueRange = 10f..80f,
                onValueChange = {
                    val updated = settings.copy(vibrationIntensityMs = it.toInt())
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            Text(
                text = "MODO DE UMA MÃO",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Pair("off", "Desativado"),
                    Pair("left", "Esquerda"),
                    Pair("right", "Direita")
                ).forEach { (mode, label) ->
                    val isSelected = settings.oneHandedMode == mode
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KeyBoardXRed.copy(alpha = 0.2f) else KeyBoardXCard
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) KeyBoardXRed else KeyBoardXBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val updated = settings.copy(oneHandedMode = mode)
                                settings = updated
                                onSaveSettings(updated)
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, KeyBoardXBorder, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = KeyBoardXSecondaryText,
                    fontSize = 11.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = KeyBoardXRed,
                    uncheckedThumbColor = KeyBoardXSecondaryText,
                    uncheckedTrackColor = KeyBoardXSurface
                )
            )
        }
    }
}
