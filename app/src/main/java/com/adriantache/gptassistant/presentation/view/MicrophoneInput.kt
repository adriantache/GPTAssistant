package com.adriantache.gptassistant.presentation.view

import android.Manifest
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.get

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneInput(
    isEnabled: Boolean,
    isSpeaking: Boolean,
    stopTTS: () -> Unit,
    recognizer: AudioRecognizer = get(),
    onResult: (String) -> Unit,
) {
    var isListening by remember { mutableStateOf(false) }
    var amplitudePercent by remember { mutableStateOf(0f) }

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val recognizerState by recognizer.state.collectAsState()

    LaunchedEffect(recognizerState) {
        when (val state = recognizerState) {
            is AudioRecognizer.RecognizerState.Recognizing -> {
                isListening = true
                amplitudePercent = state.amplitudePercent
            }

            is AudioRecognizer.RecognizerState.Success -> {
                isListening = false
                onResult(state.result)
            }

            AudioRecognizer.RecognizerState.Failure -> isListening = false

            AudioRecognizer.RecognizerState.Ready -> Unit
        }
    }

    DisposableEffect(Unit) {
        if (!microphonePermission.status.isGranted) {
            microphonePermission.launchPermissionRequest()
        }

        onDispose {
            recognizer.stopListening()
        }
    }

    Surface(
        modifier = Modifier.size(50.dp)
    ) {
        when {
            isSpeaking -> IconButton(onClick = stopTTS) {
                Icon(
                    painterResource(id = R.drawable.baseline_voice_over_off_24),
                    contentDescription = "Stop TTS"
                )
            }

            !isListening -> IconButton(
                onClick = {
                    stopTTS()
                    recognizer.startListening()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Speak input",
                    tint = if (isEnabled) Color.Unspecified else LocalContentColor.current.copy(alpha = 0.38f)
                )
            }

            else -> MicrophoneInputDisplay(
                modifier = Modifier.clickable {
                    recognizer.stopListening()
                    isListening = false
                },
                amplitudePercent = amplitudePercent,
            )
        }
    }
}
