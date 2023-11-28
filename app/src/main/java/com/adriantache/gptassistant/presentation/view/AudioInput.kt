package com.adriantache.gptassistant.presentation.view

import android.Manifest
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer
import com.adriantache.gptassistant.presentation.view.State.LISTENING
import com.adriantache.gptassistant.presentation.view.State.LOADING
import com.adriantache.gptassistant.presentation.view.State.READY
import com.adriantache.gptassistant.presentation.view.State.SPEAKING
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.delay
import org.koin.androidx.compose.get

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AudioInput(
    modifier: Modifier = Modifier,
    isTtsSpeaking: Boolean = false,
    isEnabled: Boolean = false,
    isConversationMode: Boolean = false,
    stopTts: () -> Unit,
    onInput: (input: String) -> Unit,
    onSubmit: (fromSpeech: Boolean) -> Unit,
    recognizer: AudioRecognizer = get(),
) {
    val buttonColorsNormal = ButtonColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.7f),
        disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
    )
    val buttonColorsTts = ButtonColors(
        containerColor = MaterialTheme.colorScheme.secondary,
        contentColor = MaterialTheme.colorScheme.onSecondary,
        disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(0.7f),
        disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(0.7f),
    )

    var buttonColors by remember { mutableStateOf(buttonColorsNormal) }
    var amplitudePercent by remember { mutableFloatStateOf(0f) }

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val recognizerState by recognizer.state.collectAsState()

    var state by remember { mutableStateOf(READY) }

    fun startRecognizing() {
        stopTts()
        recognizer.startListening()
    }

    fun onClick() {
        when (state) {
            READY -> {
                state = LOADING
                startRecognizing()
            }

            LISTENING -> {
                recognizer.stopListening()
                state = READY
            }

            SPEAKING -> {
                stopTts()
                state = READY
                buttonColors = buttonColorsNormal
            }

            LOADING -> Unit
        }
    }

    LaunchedEffect(recognizerState) {
        when (val localState = recognizerState) {
            is AudioRecognizer.RecognizerState.Recognizing -> {
                state = LISTENING
                amplitudePercent = localState.amplitudePercent
            }

            is AudioRecognizer.RecognizerState.Success -> {
                state = LOADING

                localState.result.value?.let {
                    onInput(it)
                    onSubmit(true)
                }
            }

            AudioRecognizer.RecognizerState.Failure -> state = READY

            AudioRecognizer.RecognizerState.Ready -> Unit
        }
    }

    var isWaitingForTtsToFinish by remember { mutableStateOf(false) }

    // TODO: remove this behaviour here and move it to use case; disable this for widget input?
    LaunchedEffect(isConversationMode, isTtsSpeaking) {
        if (!isConversationMode) return@LaunchedEffect

        if (isTtsSpeaking) {
            isWaitingForTtsToFinish = true
        } else if (isWaitingForTtsToFinish) {
            isWaitingForTtsToFinish = false

            delay(500)

            startRecognizing()
        }
    }

    LaunchedEffect(isTtsSpeaking) {
        if (isTtsSpeaking) {
            state = SPEAKING
            buttonColors = buttonColorsTts
        } else if (state == SPEAKING) {
            // TODO: test this with conversation mode
            state = READY
            buttonColors = buttonColorsNormal
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

    Button(
        modifier = modifier,
        shape = CircleShape,
        onClick = { onClick() },
        colors = buttonColors,
        contentPadding = PaddingValues(0.dp)
    ) {
        AnimatedContent(targetState = state, label = "") {
            when (it) {
                READY -> Icon(
                    painter = painterResource(id = R.drawable.baseline_mic_24),
                    contentDescription = "Speak input",
                    tint = if (isEnabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.38f)
                )

                LISTENING -> MicrophoneInputDisplay(modifier = Modifier.padding(16.dp), amplitudePercent = amplitudePercent)

                LOADING -> CircularLoadingAnimation()

                SPEAKING -> Icon(
                    painterResource(id = R.drawable.baseline_voice_over_off_24),
                    contentDescription = "Stop TTS",
                )
            }
        }
    }
}

private enum class State {
    READY,
    LISTENING,
    LOADING,
    SPEAKING,
}
