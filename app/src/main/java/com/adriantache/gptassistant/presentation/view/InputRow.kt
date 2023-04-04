package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputRow(
    isLoading: Boolean,
    stopTTS: () -> Unit,
    onInput: (String, fromSpeech: Boolean) -> Unit
) {
    var input by remember { mutableStateOf("") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MicrophoneInput(stopTTS) {
            input = it

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
        ) {
            Spacer(Modifier.weight(1f))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = { onInput(input, false) }) {
                    Text("Submit")
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}
