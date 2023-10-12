package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.presentation.util.ListScrollEntries

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
    onShowSettings: () -> Unit,
    isInputOnBottom: Boolean,
    isConversationMode: Boolean,
) {
    val lazyListState = rememberLazyListState()

    val entries = remember { ListScrollEntries() }
    val isFabVisible by remember {
        derivedStateOf {
            entries.addItem(lazyListState.firstVisibleItemIndex)
            !entries.isScrollingDown || lazyListState.firstVisibleItemIndex == 0
        }
    }

    LaunchedEffect(conversation) {
        lazyListState.animateScrollToItem((conversation.size - 2).coerceAtLeast(0))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 16.dp),
    ) {
        AnimatedVisibility(!isInputOnBottom) {
            InputRow(
                isLoading = isLoading,
                stopTts = stopTTS,
                isTtsSpeaking = isTtsSpeaking,
                input = input,
                onInput = onInput,
                onSubmit = onSubmit,
                isConversationMode = isConversationMode,
            )
        }

        AnimatedVisibility(!isInputOnBottom) {
            Spacer(Modifier.height(16.dp))
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            state = lazyListState,
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
                            onClick = onShowSettings,
                        ) {
                            Text(
                                "Settings",
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
            Spacer(Modifier.height(8.dp))

            AnimatedVisibility(visible = isFabVisible) {
                FloatingActionButton(
                    onClick = onResetConversation,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp),
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_delete_forever_24),
                            contentDescription = "Reset conversation",
                            tint = LocalContentColor.current
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text("Reset conversation")

                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(isInputOnBottom) {
            Spacer(Modifier.height(8.dp))
        }

        AnimatedVisibility(isInputOnBottom) {
            InputRow(
                isLoading = isLoading,
                stopTts = stopTTS,
                isTtsSpeaking = isTtsSpeaking,
                input = input,
                onInput = onInput,
                onSubmit = onSubmit,
                isConversationMode = isConversationMode,
            )
        }
    }
}
