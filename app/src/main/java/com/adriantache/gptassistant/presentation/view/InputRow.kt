package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R

@Composable
fun InputRow(
    isLoading: Boolean,
    stopTts: () -> Unit,
    isTtsSpeaking: Boolean,
    input: String,
    onInput: (input: String) -> Unit,
    onSubmit: (fromSpeech: Boolean) -> Unit,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Button(
            modifier = Modifier
                .requiredHeight(56.dp)
                .requiredWidth(48.dp),
            shape = RoundedCornerShape(8.dp),
            onClick = {},
            colors = ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            MicrophoneInput(
                isEnabled = !isLoading,
                isSpeaking = isTtsSpeaking,
                stopTTS = stopTts
            ) {
                keyboard?.hide()

                onInput(it)
                onSubmit(true)
            }
        }

        Spacer(Modifier.width(4.dp))

        TextField(
            modifier = Modifier.weight(1f),
            value = input,
            enabled = !isLoading,
            onValueChange = onInput,
            trailingIcon = {
                Row(
                    modifier = Modifier.requiredSize(24.dp),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconButton(
                        onClick = { onSubmit(false) },
                        enabled = !isLoading
                    ) {
                        Icon(
                            painterResource(id = R.drawable.baseline_east_24),
                            contentDescription = "Submit"
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit(false) }),
        )
    }
}

@Preview
@Composable
private fun InputRowPreview() {
    InputRow(
        isLoading = false,
        stopTts = { },
        isTtsSpeaking = false,
        input = "test",
        onInput = {},
        onSubmit = {},
    )
}
