package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
    isConversationMode: Boolean,
) {
    val keyboard = LocalSoftwareKeyboardController.current

    val buttonHeight = if (isConversationMode) 100.dp else 56.dp
    val buttonWidth = if (isConversationMode) 100.dp else 48.dp
    val buttonShape = if (isConversationMode) CircleShape else RoundedCornerShape(8.dp)

    Row(
        modifier = Modifier
            .height(IntrinsicSize.Max)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        val buttonColors = if (isTtsSpeaking) {
            ButtonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondary.copy(0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onSecondary.copy(0.7f),
            )
        } else {
            ButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer.copy(0.7f),
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(0.7f),
            )
        }

        Button(
            modifier = Modifier
                .requiredHeight(buttonHeight)
                .requiredWidth(buttonWidth),
            shape = buttonShape,
            onClick = {},
            colors = buttonColors,
            contentPadding = PaddingValues(0.dp)
        ) {
            MicrophoneInput(
                isEnabled = !isLoading,
                isSpeaking = isTtsSpeaking,
                isConversationMode = isConversationMode,
                stopTTS = stopTts,
            ) {
                keyboard?.hide()

                onInput(it)
                onSubmit(true)
            }
        }

        if (!isConversationMode) {
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
        isConversationMode = false,
    )
}
