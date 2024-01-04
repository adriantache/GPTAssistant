package com.adriantache.gptassistant.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R

@Composable
fun ConversationInput(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    isTtsSpeaking: Boolean,
    input: String,
    onInput: (input: String) -> Unit,
    onSubmit: (fromSpeech: Boolean) -> Unit,
    stopTTS: () -> Unit,
    isConversationMode: Boolean,
    isKeyboardExpanded: Boolean,
    onExpandKeyboard: () -> Unit,
) {
    BackHandler(isKeyboardExpanded) {
        onExpandKeyboard()
    }

    val audioInputOffset = remember { Animatable(0f) }

    LaunchedEffect(isKeyboardExpanded) {
        val targetValue = if (isKeyboardExpanded) -80f else 0f
        val delay = if (!isKeyboardExpanded) 200 else 0
        audioInputOffset.animateTo(targetValue, animationSpec = tween(durationMillis = 500, delayMillis = delay))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Max),
        contentAlignment = Alignment.BottomCenter,
    ) {
        KeyboardPopup(
            isExpanded = isKeyboardExpanded,
            onExpand = onExpandKeyboard,
        ) {
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(it),
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

        // TODO: move this to a separate component and extract all the garbage from it
        AudioInput(
            modifier = Modifier
                .requiredHeight(120.dp)
                .requiredWidth(100.dp)
                .padding(bottom = 20.dp)
                .offset(x = 0.dp, y = audioInputOffset.value.dp),
            isEnabled = !isLoading,
            isTtsSpeaking = isTtsSpeaking,
            isConversationMode = isConversationMode,
            stopTts = stopTTS,
            onInput = onInput,
            onSubmit = onSubmit,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xfff)
@Composable
fun ConversationInputPreview() {
    ConversationInput(
        isLoading = false,
        isTtsSpeaking = false,
        input = "",
        onInput = {},
        onSubmit = {},
        stopTTS = {},
        isConversationMode = false,
        isKeyboardExpanded = true,
        onExpandKeyboard = {},
    )
}
