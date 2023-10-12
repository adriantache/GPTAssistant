package com.adriantache.gptassistant.presentation.view

import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneInput(
    isEnabled: Boolean,
    isSpeaking: Boolean,
    isConversationMode: Boolean,
    stopTTS: () -> Unit,
    recognizer: AudioRecognizer = get(),
    onResult: (String) -> Unit,
) {
    var isListening by remember { mutableStateOf(false) }
    var amplitudePercent by remember { mutableFloatStateOf(0f) }

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val recognizerState by recognizer.state.collectAsState()

    fun startRecognizing() {
        stopTTS()
        recognizer.startListening()
    }

    LaunchedEffect(recognizerState) {
        when (val state = recognizerState) {
            is AudioRecognizer.RecognizerState.Recognizing -> {
                isListening = true
                amplitudePercent = state.amplitudePercent
            }

            is AudioRecognizer.RecognizerState.Success -> {
                isListening = false

                state.result.value?.let(onResult)
            }

            AudioRecognizer.RecognizerState.Failure -> isListening = false

            AudioRecognizer.RecognizerState.Ready -> Unit
        }
    }

    var isWaitingForTtsSpeaking by remember { mutableStateOf(false) }

    LaunchedEffect(isConversationMode, isSpeaking) {
        if (!isConversationMode) return@LaunchedEffect

        if (isSpeaking) {
            isWaitingForTtsSpeaking = true
        } else if (isWaitingForTtsSpeaking) {
            isWaitingForTtsSpeaking = false

            delay(500)

            startRecognizing()
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

    AnimatedVisibility(
        visible = isSpeaking,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        IconButton(onClick = stopTTS) {
            Icon(
                painterResource(id = R.drawable.baseline_voice_over_off_24),
                contentDescription = "Stop TTS"
            )
        }
    }

    AnimatedVisibility(
        visible = !isSpeaking && !isListening,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        IconButton(
            onClick = { startRecognizing() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.baseline_mic_24),
                contentDescription = "Speak input",
                tint = if (isEnabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
            )
        }
    }

    AnimatedVisibility(
        visible = isListening,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        MicrophoneInputDisplay(
            modifier = Modifier.clickable {
                recognizer.stopListening()
                isListening = false
            },
            amplitudePercent = amplitudePercent,
        )
    }
}
