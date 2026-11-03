package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardThemeColors
import com.example.translator.MLKitTranslatorManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorPanelView(
    initialText: String,
    translatorManager: MLKitTranslatorManager,
    colors: KeyboardThemeColors,
    onInsertTranslation: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf(initialText) }
    var sourceLang by remember { mutableStateOf("pt") }
    var targetLang by remember { mutableStateOf("en") }
    var isTranslating by remember { mutableStateOf(false) }
    var isDownloadingModel by remember { mutableStateOf(false) }
    var translatedText by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var downloadedModels by remember { mutableStateOf<Set<String>>(emptySet()) }

    var expandedSource by remember { mutableStateOf(false) }
    var expandedTarget by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        downloadedModels = translatorManager.getDownloadedModels()
    }

    val isTargetDownloaded = downloadedModels.contains(targetLang)
    val isSourceDownloaded = downloadedModels.contains(sourceLang)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(colors.background)
            .padding(8.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.keyBackground)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = colors.keyText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "Tradutor ML Kit (Offline/Online)",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Status chip
            val allDownloaded = isSourceDownloaded && isTargetDownloaded
            Text(
                text = if (allDownloaded) "● 100% Offline" else "○ Requer Download",
                color = if (allDownloaded) Color(0xFF10B981) else Color(0xFFF59E0B),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Language selector row: [Source] <-> [Target]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Source Dropdown Box
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { expandedSource = true },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.keyBackground),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    val lang = translatorManager.supportedLanguages.find { it.code == sourceLang }
                    Text(
                        text = "${lang?.flagEmoji} ${lang?.displayName}",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expandedSource,
                    onDismissRequest = { expandedSource = false },
                    modifier = Modifier.background(colors.background)
                ) {
                    translatorManager.supportedLanguages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.flagEmoji} ${lang.displayName}", color = Color.White) },
                            onClick = {
                                sourceLang = lang.code
                                expandedSource = false
                            }
                        )
                    }
                }
            }

            // Invert Languages Button
            IconButton(
                onClick = {
                    val temp = sourceLang
                    sourceLang = targetLang
                    targetLang = temp
                    val tempText = inputText
                    inputText = translatedText
                    translatedText = tempText
                },
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.accent)
            ) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = "Inverter Idiomas",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Target Dropdown Box
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { expandedTarget = true },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.keyBackground),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    val lang = translatorManager.supportedLanguages.find { it.code == targetLang }
                    Text(
                        text = "${lang?.flagEmoji} ${lang?.displayName}",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
                DropdownMenu(
                    expanded = expandedTarget,
                    onDismissRequest = { expandedTarget = false },
                    modifier = Modifier.background(colors.background)
                ) {
                    translatorManager.supportedLanguages.forEach { lang ->
                        DropdownMenuItem(
                            text = { Text("${lang.flagEmoji} ${lang.displayName}", color = Color.White) },
                            onClick = {
                                targetLang = lang.code
                                expandedTarget = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Input and Result split
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Source text input
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Texto para traduzir...", color = colors.secondaryText, fontSize = 11.sp) },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.keyBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            // Result preview
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.keyBackground)
                    .padding(8.dp)
            ) {
                if (isTranslating) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center),
                        color = colors.accent,
                        strokeWidth = 2.dp
                    )
                } else if (translatedText.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        item {
                            Text(
                                text = translatedText,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Text(
                        text = statusMessage ?: "A tradução aparecerá aqui.",
                        color = colors.secondaryText,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Bottom Actions: Download Offline Model & Translate & Insert
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Model download button if target not downloaded
            if (!isTargetDownloaded) {
                OutlinedButton(
                    onClick = {
                        isDownloadingModel = true
                        statusMessage = "Baixando modelo (~30MB)..."
                        coroutineScope.launch {
                            val res = translatorManager.downloadModel(targetLang)
                            isDownloadingModel = false
                            if (res.isSuccess) {
                                downloadedModels = translatorManager.getDownloadedModels()
                                statusMessage = "Modelo $targetLang baixado!"
                            } else {
                                statusMessage = "Erro ao baixar modelo."
                            }
                        }
                    },
                    enabled = !isDownloadingModel,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDownload,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        if (isDownloadingModel) "Baixando..." else "Baixar Offline (~30MB)",
                        fontSize = 10.sp
                    )
                }
            } else {
                Text(
                    text = "✓ Offline pronto",
                    color = Color(0xFF10B981),
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (inputText.isNotBlank()) {
                        isTranslating = true
                        statusMessage = null
                        coroutineScope.launch {
                            val res = translatorManager.translateText(inputText, sourceLang, targetLang)
                            isTranslating = false
                            res.onSuccess {
                                translatedText = it
                                downloadedModels = translatorManager.getDownloadedModels()
                            }.onFailure {
                                statusMessage = "Falha: ${it.localizedMessage}"
                            }
                        }
                    }
                },
                enabled = !isTranslating && inputText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text("Traduzir", fontSize = 11.sp, color = Color.White)
            }

            if (translatedText.isNotEmpty()) {
                Button(
                    onClick = {
                        onInsertTranslation(translatedText)
                        onClose()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("Inserir", fontSize = 11.sp, color = Color.White)
                }
            }
        }
    }
}
