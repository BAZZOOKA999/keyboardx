package com.example.ui.settings

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.FirebaseAIProvider
import com.example.data.local.EncryptedStorageHelper
import com.example.data.local.KeyBoardXDatabase
import com.example.data.model.KeyboardSettings
import com.example.data.model.KeyboardThemePreset
import com.example.data.model.QuickText
import com.example.ime.view.KeyButton
import com.example.translator.MLKitTranslatorManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun HomeSetupScreen(
    settings: KeyboardSettings,
    onNavigate: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEnabled by remember { mutableStateOf(false) }
    var isSelected by remember { mutableStateOf(false) }
    var testText by remember { mutableStateOf("") }

    fun refreshImeStatus() {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            val enabledList = imm.enabledInputMethodList
            val myPackage = context.packageName
            isEnabled = enabledList.any { it.packageName == myPackage }

            val currentIme = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.DEFAULT_INPUT_METHOD
            ) ?: ""
            isSelected = currentIme.contains(myPackage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        refreshImeStatus()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(16.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(KeyBoardXRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "X",
                                color = Color.White,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Column {
                            Text(
                                text = "KeyBoardX",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Teclado Inteligente & Seguro",
                                color = KeyBoardXSecondaryText,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Desenvolvido com arquitetura de privacidade absoluta, suporte completo a Termux, digitação com gestos, tradução offline e IA integrada.",
                        color = Color(0xFFD4D4D8),
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // IME Status Cards
        item {
            Text(
                text = "STATUS DE ATIVAÇÃO NO ANDROID",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Step 1: Enable KeyBoardX
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isEnabled) KeyBoardXSuccess else KeyBoardXBorder,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = if (isEnabled) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isEnabled) KeyBoardXSuccess else KeyBoardXSecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "1. Habilitar nas Configurações",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEnabled) "Teclado ativado no sistema Android." else "Ative o KeyBoardX na lista de teclados do Android.",
                            color = KeyBoardXSecondaryText,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS)
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnabled) KeyBoardXSurface else KeyBoardXRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isEnabled) "Configurado" else "Ativar", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Step 2: Select as default IME
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (isSelected) KeyBoardXSuccess else KeyBoardXBorder,
                        RoundedCornerShape(12.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(
                                imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (isSelected) KeyBoardXSuccess else KeyBoardXSecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "2. Selecionar como Padrão",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isSelected) "KeyBoardX é seu teclado atual ativo." else "Defina o KeyBoardX como método de entrada padrão.",
                            color = KeyBoardXSecondaryText,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.showInputMethodPicker()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) KeyBoardXSurface else KeyBoardXRed
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (isSelected) "Selecionado" else "Escolher", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }

        // Interactive Sandbox to test immediately
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TESTE INTERATIVO DO TECLADO",
                        color = KeyBoardXSecondaryText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Toque no campo abaixo para abrir o teclado e testar acentos, atalhos, modo Termux e tradução:",
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = testText,
                        onValueChange = { testText = it },
                        placeholder = { Text("Toque aqui para digitar...", color = KeyBoardXSecondaryText) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KeyBoardXRed,
                            unfocusedBorderColor = KeyBoardXBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Quick feature navigation chips
        item {
            Text(
                text = "CONFIGURAÇÕES RÁPIDAS",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SettingsNavigationRow(
                    icon = Icons.Default.Palette,
                    title = "Temas & Aparência",
                    subtitle = "Dark Red, Pure Black, personalização de teclas e cores",
                    onClick = { onNavigate(1) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.Keyboard,
                    title = "Digitação & Gestos",
                    subtitle = "Autocorreção, vibração, som, linha de números e cursor",
                    onClick = { onNavigate(2) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.Terminal,
                    title = "Modo Termux & Dev",
                    subtitle = "Linha extra, ESC, TAB, CTRL, ALT, teclas F1-F12 e atalhos",
                    onClick = { onNavigate(3) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.FormatQuote,
                    title = "Meus Textos & Atalhos",
                    subtitle = "Modelos rápidos com criptografia AES-GCM e expansão de texto",
                    onClick = { onNavigate(4) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.Translate,
                    title = "Tradutor ML Kit Offline",
                    subtitle = "Gerenciamento de pacotes de tradução offline (~30MB)",
                    onClick = { onNavigate(5) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.AutoAwesome,
                    title = "KeyBoardX AI",
                    subtitle = "Reescrita, correção gramatical e resumo inteligente",
                    onClick = { onNavigate(6) }
                )

                SettingsNavigationRow(
                    icon = Icons.Default.Security,
                    title = "Privacidade & Segurança",
                    subtitle = "Arquitetura sem telemetria, detecção de senhas e modo privado",
                    onClick = { onNavigate(7) }
                )
            }
        }
    }
}

@Composable
fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(1.dp, KeyBoardXBorder, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(KeyBoardXSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = KeyBoardXRed,
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = KeyBoardXSecondaryText,
                    fontSize = 11.sp
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = KeyBoardXSecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
