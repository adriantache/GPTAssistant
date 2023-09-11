package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

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
        onValueChange = { input = it },
        leadingIcon = {
            Row {
                Spacer(Modifier.width(2.dp))

                MicrophoneInput(isSpeaking, stopTts) {
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
        keyboardOptions = KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                onInput(input, false)
            }
        ),
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
