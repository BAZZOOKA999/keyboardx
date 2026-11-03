package com.example.ime.view

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.KeyboardThemeColors
import com.example.voice.VoiceInputManager
import com.example.voice.VoiceState

@Composable
fun VoiceInputOverlay(
    voiceManager: VoiceInputManager,
    colors: KeyboardThemeColors,
    defaultLanguage: String,
    onTextRecognized: (String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedLanguage by remember {
        mutableStateOf(
            when (defaultLanguage) {
                "en_US" -> "en-US"
                "es_ES" -> "es-ES"
                else -> "pt-BR"
            }
        )
    }

    val voiceState by voiceManager.voiceState.collectAsState()
    val soundLevel by voiceManager.soundLevel.collectAsState()
    var transcribedText by remember { mutableStateOf("") }

    // Pulsing circle animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    LaunchedEffect(Unit) {
        voiceManager.startListening(
            language = selectedLanguage,
            preferOffline = true,
            onFinalText = { text ->
                transcribedText = text
            }
        )
    }

    LaunchedEffect(voiceState) {
        when (val state = voiceState) {
            is VoiceState.PartialResult -> transcribedText = state.text
            is VoiceState.FinalResult -> transcribedText = state.text
            else -> {}
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            voiceManager.cancelListening()
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(colors.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top row: Language toggles & Cancel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf(
                    Pair("pt-BR", "Português"),
                    Pair("en-US", "English"),
                    Pair("es-ES", "Español")
                ).forEach { (code, label) ->
                    val isSelected = selectedLanguage == code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.accent else colors.keyBackground)
                            .clickable {
                                selectedLanguage = code
                                voiceManager.startListening(
                                    language = code,
                                    preferOffline = true,
                                    onFinalText = { transcribedText = it }
                                )
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            IconButton(
                onClick = {
                    voiceManager.cancelListening()
                    onClose()
                },
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colors.keyBackground)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fechar",
                    tint = colors.keyText,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Live transcription view
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.keyBackground)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            when (voiceState) {
                is VoiceState.Listening -> {
                    if (transcribedText.isBlank()) {
                        Text(
                            text = "Fale agora... Ouvindo em $selectedLanguage",
                            color = colors.secondaryText,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Text(
                            text = transcribedText,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is VoiceState.Error -> {
                    Text(
                        text = (voiceState as VoiceState.Error).message,
                        color = colors.accent,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Text(
                        text = transcribedText.ifBlank { "Toque no microfone para gravar." },
                        color = Color.White,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Center Animated Mic & Control Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cancel Button
            OutlinedButton(
                onClick = {
                    voiceManager.cancelListening()
                    onClose()
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Cancelar", fontSize = 12.sp, color = colors.keyText)
            }

            // Big Glowing Mic Button
            val isListening = voiceState is VoiceState.Listening
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .scale(if (isListening) pulseScale else 1.0f)
                    .clip(CircleShape)
                    .background(if (isListening) colors.accent else colors.keyBackground)
                    .clickable {
                        if (isListening) {
                            voiceManager.stopListening()
                        } else {
                            voiceManager.startListening(
                                language = selectedLanguage,
                                preferOffline = true,
                                onFinalText = { transcribedText = it }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                    contentDescription = if (isListening) "Parar" else "Gravar",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            // Commit / Insert Button
            Button(
                onClick = {
                    if (transcribedText.isNotBlank()) {
                        onTextRecognized(transcribedText)
                    }
                    voiceManager.stopListening()
                    onClose()
                },
                enabled = transcribedText.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Done,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Inserir", fontSize = 12.sp, color = Color.White)
            }
        }
    }
}
