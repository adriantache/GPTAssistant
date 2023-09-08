package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import kotlinx.coroutines.flow.MutableStateFlow
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

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = input,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
        ),
        onValueChange = { input = it },
        leadingIcon = {
            Row {
                Spacer(Modifier.width(2.dp))

                MicrophoneInput(stopTts) {
                    input = it

                    keyboard?.hide()

                    if (!isLoading) {
                        onInput(input, true)
                    }
                }
            }
        },
        trailingIcon = {
            Row(
                modifier = Modifier.requiredSize(24.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else if (isSpeaking) {
                    IconButton(onClick = stopTts) {
                        Icon(
                            painterResource(id = R.drawable.baseline_voice_over_off_24),
                            contentDescription = "Stop TTS"
                        )
                    }
                } else {
                    IconButton(onClick = { onInput(input, false) }) {
                        Icon(
                            painterResource(id = R.drawable.baseline_east_24),
                            contentDescription = "Submit"
                        )
                    }
                }
            }
        },
    )
}

@Preview
@Composable
private fun InputRowPreview() {
    InputRow(
        isLoading = false,
        stopTts = { },
        isTtsSpeaking = MutableStateFlow(false),
        onInput = { _, _ -> }
    )
}
