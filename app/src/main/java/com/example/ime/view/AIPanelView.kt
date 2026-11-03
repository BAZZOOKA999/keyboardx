package com.example.ime.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AIProvider
import com.example.ai.AITool
import com.example.data.model.KeyboardThemeColors
import kotlinx.coroutines.launch

@Composable
fun AIPanelView(
    initialText: String,
    aiProvider: AIProvider,
    colors: KeyboardThemeColors,
    onReplaceText: (String) -> Unit,
    onInsertText: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf(initialText) }
    var selectedTool by remember { mutableStateOf(AITool.GRAMMAR) }
    var customInstruction by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var resultText by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showConsentDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current

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

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = colors.accent,
                    modifier = Modifier.size(18.dp)
                )

                Text(
                    text = "KeyBoardX AI",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = if (aiProvider.isConfigured()) "● Conectado" else "○ Pendente",
                color = if (aiProvider.isConfigured()) Color(0xFF10B981) else Color(0xFFEAB308),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (resultText == null && errorMessage == null) {
            // Tool selector chip row
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(AITool.values()) { tool ->
                    val isSelected = tool == selectedTool
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) colors.accent else colors.keyBackground)
                            .clickable { selectedTool = tool }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = tool.title,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Text input area
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        "Digite ou selecione o texto para a IA...",
                        color = colors.secondaryText,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = colors.accent,
                    unfocusedBorderColor = colors.keyBackground,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )

            if (selectedTool == AITool.CUSTOM) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = customInstruction,
                    onValueChange = { customInstruction = it },
                    placeholder = {
                        Text(
                            "Instrução personalizada (ex: Reescreva como poema)",
                            color = colors.secondaryText,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.keyBackground,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Action row: Execute with confirmation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selectedTool.description,
                    color = colors.secondaryText,
                    fontSize = 10.sp,
                    modifier = Modifier.weight(1f)
                )

                Button(
                    onClick = {
                        if (!aiProvider.isConfigured()) {
                            errorMessage = aiProvider.getConfigurationMessage()
                        } else {
                            showConsentDialog = true
                        }
                    },
                    enabled = !isProcessing && (inputText.isNotBlank() || selectedTool == AITool.CUSTOM),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Processar", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        } else {
            // Preview / Result View
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colors.keyBackground)
                    .padding(8.dp)
            ) {
                Text(
                    text = if (errorMessage != null) "Aviso de Configuração" else "Resultado da IA",
                    color = if (errorMessage != null) colors.accent else Color(0xFF10B981),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    LazyColumn {
                        item {
                            Text(
                                text = errorMessage ?: (resultText ?: ""),
                                color = Color.White,
                                fontSize = 13.sp,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (resultText != null) {
                        Button(
                            onClick = {
                                onReplaceText(resultText!!)
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Substituir", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                onInsertText(resultText!!)
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.keyBackground),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Inserir", fontSize = 11.sp, color = Color.White)
                        }

                        Button(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(resultText!!))
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.keyBackground),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            Text("Copiar", fontSize = 11.sp, color = Color.White)
                        }
                    }

                    OutlinedButton(
                        onClick = {
                            resultText = null
                            errorMessage = null
                        },
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp)
                    ) {
                        Text("Voltar", fontSize = 11.sp, color = colors.keyText)
                    }
                }
            }
        }
    }

    // Consent Dialog for Online Transmission (Requirement 4 & 10)
    if (showConsentDialog) {
        AlertDialog(
            onDismissRequest = { showConsentDialog = false },
            title = { Text("Confirmar Envio Online", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "O texto selecionado será enviado ao Firebase AI Logic para processamento. Nenhum dado é salvo ou monitorado.\n\nDeseja continuar?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConsentDialog = false
                        isProcessing = true
                        coroutineScope.launch {
                            val result = aiProvider.processText(
                                tool = selectedTool,
                                text = inputText,
                                customPrompt = if (selectedTool == AITool.CUSTOM) customInstruction else null
                            )
                            isProcessing = false
                            result.onSuccess {
                                resultText = it
                                errorMessage = null
                            }.onFailure {
                                errorMessage = it.localizedMessage ?: "Erro ao processar com IA."
                            }
                        }
                    }
                ) {
                    Text("Confirmar Envio", color = colors.accent, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConsentDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
