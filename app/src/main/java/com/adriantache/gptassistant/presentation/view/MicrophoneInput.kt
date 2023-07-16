package com.adriantache.gptassistant.presentation.view

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MicrophoneInput(
    stopTTS: () -> Unit,
    onResult: (String) -> Unit,
) {
    var isListening by remember { mutableStateOf(false) }
    var amplitudePercent by remember { mutableStateOf(0f) }

    val microphonePermission = rememberPermissionState(Manifest.permission.RECORD_AUDIO)

    val context = LocalContext.current
    val recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                isListening = true
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) {
                amplitudePercent = (rmsdB * 10).toInt() / 100f
            }

            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                isListening = false
            }

            override fun onError(error: Int) = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val output = matches?.get(0) ?: ""
                onResult(output)
            }
        })
    }
    val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    DisposableEffect(Unit) {
        if (!microphonePermission.status.isGranted) {
            microphonePermission.launchPermissionRequest()
        }

        onDispose {
            recognizer?.stopListening()
        }
    }

    Surface(
        modifier = Modifier.size(50.dp)
    ) {
        if (!isListening) {
            IconButton(onClick = {
                stopTTS()
                recognizer.startListening(recognizerIntent)
            }) {
                Icon(painter = painterResource(id = R.drawable.baseline_mic_24), contentDescription = null)
            }
        } else {
            MicrophoneInputDisplay(
                modifier = Modifier.clickable {
                    recognizer.stopListening()
                    isListening = false
                },
                amplitudePercent = amplitudePercent,
            )
        }
    }
}
