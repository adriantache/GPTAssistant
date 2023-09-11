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
    input: String,
    onInput: (String) -> Unit,
    onSubmit: (String, fromSpeech: Boolean) -> Unit,
) {
    val isSpeaking by isTtsSpeaking.collectAsState()

    val keyboard = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = input,
        onValueChange = onInput,
        leadingIcon = {
            Row {
                Spacer(Modifier.width(2.dp))

                MicrophoneInput(isSpeaking, stopTts) {
                    onInput(it)

                    keyboard?.hide()

                    if (!isLoading) {
                        onSubmit(it, true)
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
                    IconButton(onClick = { onSubmit(input, false) }) {
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
                onSubmit(input, false)
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
        input = "test",
        onInput = {},
        onSubmit = { _, _ -> },
    )
}
