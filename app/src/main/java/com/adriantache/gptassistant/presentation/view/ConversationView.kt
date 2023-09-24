package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.domain.model.Message

@OptIn(ExperimentalFoundationApi::class)
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
                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .requiredHeight(48.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                            onClick = onLoadPreviousConversations,
                        ) {
                            Text(
                                "Load previous conversations",
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            } else {
                items(conversation, key = { it.id }) { message ->
                    MessageView(
                        modifier = Modifier.animateItemPlacement(),
                        message = message,
                    )
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
