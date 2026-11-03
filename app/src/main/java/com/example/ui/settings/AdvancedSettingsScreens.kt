package com.example.ui.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.AITool
import com.example.ai.FirebaseAIProvider
import com.example.data.local.EncryptedStorageHelper
import com.example.data.local.KeyBoardXDatabase
import com.example.data.model.KeyboardSettings
import com.example.data.model.QuickText
import com.example.translator.MLKitTranslatorManager
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun TermuxSettingsScreen(
    currentSettings: KeyboardSettings,
    onSaveSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(currentSettings) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = KeyBoardXRed)
                        Text(
                            text = "MODO TERMUX & PROGRAMAÇÃO",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Projetado para desenvolvedores e administradores de sistemas que utilizam Termux, SSH, Vim, Nano, Git e ambientes Linux no Android. Emite eventos reais de KeyEvent no sistema.",
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            SettingToggleItem(
                title = "Ativar Modo Termux por Padrão",
                subtitle = "Habilita os controles de terminal ao abrir o teclado.",
                checked = settings.termuxModeEnabled,
                onCheckedChange = {
                    val updated = settings.copy(termuxModeEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Linha Adicional de Navegação",
                subtitle = "Exibe barra horizontal com ESC, TAB, CTRL, ALT, Setas (↑↓←→), HOME, END, PGUP, PGDN.",
                checked = settings.termuxExtraRowEnabled,
                onCheckedChange = {
                    val updated = settings.copy(termuxExtraRowEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            Text(
                text = "LAYOUT DO TERMINAL",
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
                    Pair("normal", "Padrão"),
                    Pair("terminal", "Terminal Completo"),
                    Pair("coding", "Programação")
                ).forEach { (mode, label) ->
                    val isSelected = settings.termuxLayoutMode == mode
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) KeyBoardXRed.copy(alpha = 0.2f) else KeyBoardXCard
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .border(1.dp, if (isSelected) KeyBoardXRed else KeyBoardXBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                val updated = settings.copy(termuxLayoutMode = mode)
                                settings = updated
                                onSaveSettings(updated)
                            }
                    ) {
                        Box(modifier = Modifier.padding(10.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Recursos Avançados Implementados:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Teclas modificadoras reais: CTRL e ALT com estado alternável (sticky).\n" +
                            "• Atalhos nativos: Ctrl+C (SIGINT), Ctrl+Z (SIGTSTP), Ctrl+D (EOF), Ctrl+L (clear), Ctrl+A, Ctrl+E.\n" +
                            "• Teclas de função: F1 até F12 enviadas como KeyEvent.KEYCODE_F1..F12.\n" +
                            "• Símbolos de sintaxe: / \\ | ~ $ # & * {} [] () <> = _ - : ; ' \" ` com inserção pareada.",
                        color = KeyBoardXSecondaryText,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTextsSettingsScreen(
    database: KeyBoardXDatabase,
    modifier: Modifier = Modifier
) {
    val quickTexts by database.quickTextDao().getAllQuickTexts().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Encryption Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(KeyBoardXSuccess.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = KeyBoardXSuccess)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Cofre Criptografado AES-256 GCM",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Chaves gerenciadas no hardware AndroidKeyStore. Seus modelos permanecem inacessíveis para outros apps.",
                            color = KeyBoardXSecondaryText,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Action Buttons Row: Add & Export
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { showAddDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = KeyBoardXRed),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Novo Modelo", fontSize = 12.sp, color = Color.White)
                }

                OutlinedButton(
                    onClick = {
                        if (quickTexts.isNotEmpty()) {
                            val exportContent = quickTexts.joinToString("\n\n---\n\n") { item ->
                                "Título: ${item.title}\nAtalho: !${item.shortcut ?: "nenhum"}\nConteúdo: ${EncryptedStorageHelper.decrypt(item.content)}"
                            }
                            clipboardManager.setText(AnnotatedString(exportContent))
                            Toast.makeText(context, "Modelos exportados para a área de transferência!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Nenhum modelo para exportar.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exportar", fontSize = 12.sp, color = KeyBoardXText)
                }
            }
        }

        item {
            Text(
                text = "SEUS MODELOS (${quickTexts.size})",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (quickTexts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Nenhum modelo cadastrado ainda.",
                        color = KeyBoardXSecondaryText,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(quickTexts) { item ->
                val plainContent = remember(item.content) {
                    EncryptedStorageHelper.decrypt(item.content)
                }

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
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = item.title,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                if (item.shortcut != null) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(KeyBoardXRed.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Atalho: ${item.shortcut}",
                                            color = KeyBoardXRed,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = plainContent,
                                color = KeyBoardXSecondaryText,
                                fontSize = 12.sp,
                                maxLines = 2
                            )
                        }

                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    database.quickTextDao().delete(item)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir",
                                tint = KeyBoardXSecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var shortcut by remember { mutableStateOf("") }
        var content by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Novo Modelo Criptografado", fontSize = 16.sp, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Título (ex: Meu Email Comercial)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = shortcut,
                        onValueChange = { shortcut = it },
                        label = { Text("Atalho de expansão (ex: meuemail)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = content,
                        onValueChange = { content = it },
                        label = { Text("Conteúdo completo") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (title.isNotBlank() && content.isNotBlank()) {
                            coroutineScope.launch {
                                database.quickTextDao().insert(
                                    QuickText(
                                        title = title.trim(),
                                        shortcut = shortcut.trim().ifBlank { null },
                                        content = EncryptedStorageHelper.encrypt(content.trim())
                                    )
                                )
                            }
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Salvar", color = KeyBoardXRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun TranslatorSettingsScreen(
    translatorManager: MLKitTranslatorManager,
    modifier: Modifier = Modifier
) {
    var downloadedModels by remember { mutableStateOf<Set<String>>(emptySet()) }
    var downloadingCode by remember { mutableStateOf<String?>(null) }
    var testInput by remember { mutableStateOf("Olá, como você está?") }
    var testOutput by remember { mutableStateOf("") }
    var isTranslating by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    fun refreshModels() {
        coroutineScope.launch {
            downloadedModels = translatorManager.getDownloadedModels()
        }
    }

    LaunchedEffect(Unit) {
        refreshModels()
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Translate, contentDescription = null, tint = KeyBoardXRed)
                        Text(
                            text = "TRADUÇÃO OFFLINE GOOGLE ML KIT",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Baixe modelos neurais compactos (~30MB cada) para traduzir textos com rapidez sem conexão de internet.",
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "PACOTES DE IDIOMAS",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(translatorManager.supportedLanguages) { lang ->
            val isDownloaded = downloadedModels.contains(lang.code)
            val isDownloading = downloadingCode == lang.code

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
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = lang.flagEmoji, fontSize = 22.sp)
                        Column {
                            Text(
                                text = lang.displayName,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (isDownloaded) "Baixado (~30MB) • Pronto offline" else "Disponível para download (~30MB)",
                                color = if (isDownloaded) KeyBoardXSuccess else KeyBoardXSecondaryText,
                                fontSize = 11.sp
                            )
                        }
                    }

                    if (isDownloaded) {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    translatorManager.deleteModel(lang.code)
                                    refreshModels()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Remover modelo",
                                tint = KeyBoardXSecondaryText,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                downloadingCode = lang.code
                                coroutineScope.launch {
                                    translatorManager.downloadModel(lang.code)
                                    downloadingCode = null
                                    refreshModels()
                                }
                            },
                            enabled = !isDownloading,
                            colors = ButtonDefaults.buttonColors(containerColor = KeyBoardXRed),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            if (isDownloading) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), color = Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Baixar", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }

        // Live translator test
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "TESTE DE TRADUÇÃO (PT -> EN)",
                        color = KeyBoardXSecondaryText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = testInput,
                        onValueChange = { testInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = KeyBoardXRed,
                            unfocusedBorderColor = KeyBoardXBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            isTranslating = true
                            coroutineScope.launch {
                                val res = translatorManager.translateText(testInput, "pt", "en")
                                isTranslating = false
                                res.onSuccess {
                                    testOutput = it
                                    refreshModels()
                                }.onFailure {
                                    testOutput = "Erro: ${it.localizedMessage}"
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = KeyBoardXRed),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !isTranslating
                    ) {
                        Text("Traduzir Agora", color = Color.White)
                    }

                    if (testOutput.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Resultado: $testOutput",
                            color = KeyBoardXSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AISettingsScreen(
    aiProvider: FirebaseAIProvider,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = KeyBoardXRed)
                            Text(
                                text = "KEYBOARDX AI",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = if (aiProvider.isConfigured()) "Ativo" else "Pendente",
                            color = if (aiProvider.isConfigured()) KeyBoardXSuccess else Color(0xFFEAB308),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = aiProvider.getConfigurationMessage(),
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        item {
            Text(
                text = "FERRAMENTAS DE IA DISPONÍVEIS",
                color = KeyBoardXSecondaryText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        items(AITool.values()) { tool ->
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXBorder, RoundedCornerShape(10.dp))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = tool.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tool.description,
                        color = KeyBoardXSecondaryText,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PrivacySettingsScreen(
    currentSettings: KeyboardSettings,
    onSaveSettings: (KeyboardSettings) -> Unit,
    modifier: Modifier = Modifier
) {
    var settings by remember { mutableStateOf(currentSettings) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXSurface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KeyBoardXSuccess, RoundedCornerShape(12.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = KeyBoardXSuccess)
                        Text(
                            text = "PRIVACIDADE DESDE A ARQUITETURA",
                            color = KeyBoardXSuccess,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "O KeyBoardX foi construído sob o princípio de Privacidade Absoluta:\n\n" +
                            "✓ Zero telemetria de digitação.\n" +
                            "✓ Nenhum keylogger ou gravação de teclas.\n" +
                            "✓ Nenhum armazenamento de senhas digitadas.\n" +
                            "✓ Nenhuma captura de tela ou monitoramento de acessibilidade.\n" +
                            "✓ Teclado básico 100% offline.\n" +
                            "✓ Criptografia de hardware AES-256 GCM para textos salvos.",
                        color = Color(0xFFD4D4D8),
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            SettingToggleItem(
                title = "Modo Privado Estrito",
                subtitle = "Desativa qualquer aprendizado de palavras, sugestões personalizadas e histórico em todos os campos.",
                checked = settings.privacyMode,
                onCheckedChange = {
                    val updated = settings.copy(privacyMode = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            SettingToggleItem(
                title = "Histórico de Área de Transferência",
                subtitle = "Desativado por padrão. Quando ativado, salva apenas textos não sensíveis selecionados voluntariamente.",
                checked = settings.clipboardHistoryEnabled,
                onCheckedChange = {
                    val updated = settings.copy(clipboardHistoryEnabled = it)
                    settings = updated
                    onSaveSettings(updated)
                }
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = KeyBoardXCard),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Proteção Automática em Campos de Senha:",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Quando um campo de senha é detectado (InputType PASSWORD, WEB_PASSWORD, NO_SUGGESTIONS), o KeyBoardX automaticamente oculta sugestões, desativa gravações, bloqueia envio online e isola a memória.",
                        color = KeyBoardXSecondaryText,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
