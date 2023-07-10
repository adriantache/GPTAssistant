package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun InputRow(
    isLoading: Boolean,
    stopTts: () -> Unit,
    isTtsSpeaking: StateFlow<Boolean>,
    onInput: (String, fromSpeech: Boolean) -> Unit,
) {
    var input by remember { mutableStateOf("") }
    val isSpeaking by isTtsSpeaking.collectAsState()

    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MicrophoneInput(stopTts) {
            input = it

            keyboard?.hide()

            if (!isLoading) {
                onInput(input, true)
            }
        }

        Spacer(Modifier.width(8.dp))

        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = input,
            onValueChange = { input = it }
        )

        Spacer(Modifier.width(8.dp))

        Row(
            modifier = Modifier.width(100.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer(Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator()
            } else if (isSpeaking) {
                Button(onClick = { stopTts() }) {
                    Text("Stop TTS")
                }
            } else {
                Button(onClick = { onInput(input, false) }) {
                    Text("Submit")
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
