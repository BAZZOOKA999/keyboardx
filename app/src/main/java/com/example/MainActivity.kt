package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ai.FirebaseAIProvider
import com.example.data.local.DataStoreManager
import com.example.data.local.KeyBoardXDatabase
import com.example.data.model.KeyboardSettings
import com.example.translator.MLKitTranslatorManager
import com.example.ui.settings.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var database: KeyBoardXDatabase
    private lateinit var translatorManager: MLKitTranslatorManager
    private lateinit var aiProvider: FirebaseAIProvider

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dataStoreManager = DataStoreManager(this)
        database = KeyBoardXDatabase.getInstance(this)
        translatorManager = MLKitTranslatorManager()
        aiProvider = FirebaseAIProvider()

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()
                val settings by dataStoreManager.settingsFlow.collectAsState(initial = KeyboardSettings())

                var currentScreenIndex by remember { mutableIntStateOf(0) }

                BackHandler(enabled = currentScreenIndex != 0) {
                    currentScreenIndex = 0
                }

                val screenTitles = listOf(
                    "KeyBoardX",
                    "Temas & Cores",
                    "Digitação & Gestos",
                    "Modo Termux",
                    "Meus Textos & Modelos",
                    "Tradutor ML Kit",
                    "KeyBoardX AI",
                    "Privacidade & Segurança"
                )

                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = screenTitles[currentScreenIndex],
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            navigationIcon = {
                                if (currentScreenIndex != 0) {
                                    IconButton(onClick = { currentScreenIndex = 0 }) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Voltar",
                                            tint = Color.White
                                        )
                                    }
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Keyboard,
                                        contentDescription = null,
                                        tint = KeyBoardXRed,
                                        modifier = Modifier.padding(start = 16.dp, end = 8.dp)
                                    )
                                }
                            },
                            actions = {
                                IconButton(onClick = {
                                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                    imm.showInputMethodPicker()
                                }) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = "Selecionar Teclado",
                                        tint = KeyBoardXRed
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = KeyBoardXSurface
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = KeyBoardXSurface,
                            contentColor = KeyBoardXRed
                        ) {
                            NavigationBarItem(
                                selected = currentScreenIndex == 0,
                                onClick = { currentScreenIndex = 0 },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Início") },
                                label = { Text("Início", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KeyBoardXRed,
                                    selectedTextColor = KeyBoardXRed,
                                    unselectedIconColor = KeyBoardXSecondaryText,
                                    unselectedTextColor = KeyBoardXSecondaryText,
                                    indicatorColor = KeyBoardXCard
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 1,
                                onClick = { currentScreenIndex = 1 },
                                icon = { Icon(Icons.Default.Palette, contentDescription = "Temas") },
                                label = { Text("Temas", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KeyBoardXRed,
                                    selectedTextColor = KeyBoardXRed,
                                    unselectedIconColor = KeyBoardXSecondaryText,
                                    unselectedTextColor = KeyBoardXSecondaryText,
                                    indicatorColor = KeyBoardXCard
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 2,
                                onClick = { currentScreenIndex = 2 },
                                icon = { Icon(Icons.Default.Keyboard, contentDescription = "Digitação") },
                                label = { Text("Digitação", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KeyBoardXRed,
                                    selectedTextColor = KeyBoardXRed,
                                    unselectedIconColor = KeyBoardXSecondaryText,
                                    unselectedTextColor = KeyBoardXSecondaryText,
                                    indicatorColor = KeyBoardXCard
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 3,
                                onClick = { currentScreenIndex = 3 },
                                icon = { Icon(Icons.Default.Terminal, contentDescription = "Termux") },
                                label = { Text("Termux", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KeyBoardXRed,
                                    selectedTextColor = KeyBoardXRed,
                                    unselectedIconColor = KeyBoardXSecondaryText,
                                    unselectedTextColor = KeyBoardXSecondaryText,
                                    indicatorColor = KeyBoardXCard
                                )
                            )

                            NavigationBarItem(
                                selected = currentScreenIndex == 7,
                                onClick = { currentScreenIndex = 7 },
                                icon = { Icon(Icons.Default.Security, contentDescription = "Privacidade") },
                                label = { Text("Segurança", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = KeyBoardXRed,
                                    selectedTextColor = KeyBoardXRed,
                                    unselectedIconColor = KeyBoardXSecondaryText,
                                    unselectedTextColor = KeyBoardXSecondaryText,
                                    indicatorColor = KeyBoardXCard
                                )
                            )
                        }
                    },
                    containerColor = KeyBoardXBackground
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .background(KeyBoardXBackground)
                    ) {
                        when (currentScreenIndex) {
                            0 -> HomeSetupScreen(
                                settings = settings,
                                onNavigate = { currentScreenIndex = it }
                            )

                            1 -> ThemeSettingsScreen(
                                currentSettings = settings,
                                onSaveSettings = { updated ->
                                    coroutineScope.launch {
                                        dataStoreManager.updateSettings(updated)
                                    }
                                }
                            )

                            2 -> TypingSettingsScreen(
                                currentSettings = settings,
                                onSaveSettings = { updated ->
                                    coroutineScope.launch {
                                        dataStoreManager.updateSettings(updated)
                                    }
                                }
                            )

                            3 -> TermuxSettingsScreen(
                                currentSettings = settings,
                                onSaveSettings = { updated ->
                                    coroutineScope.launch {
                                        dataStoreManager.updateSettings(updated)
                                    }
                                }
                            )

                            4 -> QuickTextsSettingsScreen(
                                database = database
                            )

                            5 -> TranslatorSettingsScreen(
                                translatorManager = translatorManager
                            )

                            6 -> AISettingsScreen(
                                aiProvider = aiProvider
                            )

                            7 -> PrivacySettingsScreen(
                                currentSettings = settings,
                                onSaveSettings = { updated ->
                                    coroutineScope.launch {
                                        dataStoreManager.updateSettings(updated)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
