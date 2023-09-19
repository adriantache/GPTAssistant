package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.domain.model.Message

@Composable
fun ConversationView(
    conversation: List<Message>,
    isLoading: Boolean,
    isTtsSpeaking: Boolean,
    canResetConversation: Boolean,
    input: String,
    onInput: (input: String) -> Unit,
    onSubmit: (fromSpeech: Boolean) -> Unit,
    stopTTS: () -> Unit,
    onResetConversation: () -> Unit,
    onLoadPreviousConversations: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    LaunchedEffect(conversation) {
        scrollState.animateScrollToItem((conversation.size - 2).coerceAtLeast(0))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
    ) {
        InputRow(
            isLoading = isLoading,
            stopTts = stopTTS,
            isTtsSpeaking = isTtsSpeaking,
            input = input,
            onInput = onInput,
            onSubmit = onSubmit,
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = scrollState,
        ) {
            if (conversation.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(onClick = onLoadPreviousConversations) {
                            Text("Load previous conversations")
                        }
                    }
                }
            } else {
                itemsIndexed(conversation, key = { index, message ->
                    // TODO: add index to messages to avoid this weird key
                    message::class.java.simpleName + message.content + index
                }) { _, message ->
                    var visible by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        visible = true
                    }

                    val offsetModifier = if (message is Message.UserMessage) 1 else -1

                    AnimatedVisibility(
                        visible = visible,
                        enter = slideIn { fullSize ->
                            IntOffset(fullSize.width * offsetModifier, 0)
                        },
                    ) {
                        MessageView(message)
                    }
                }
            }
        }

        if (canResetConversation) {
            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                onClick = onResetConversation
            ) {
                Text("Reset conversation")
            }
        }
    }
}
