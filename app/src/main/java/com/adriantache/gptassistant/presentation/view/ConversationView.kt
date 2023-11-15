package com.adriantache.gptassistant.presentation.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.ui.ConversationUi
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ConversationView(
    conversation: ConversationUi,
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

    val topBarAnimation = remember { Animatable(-200f) }

    BackHandler(canResetConversation) {
        onResetConversation()
    }

    LaunchedEffect(conversation) {
        lazyListState.animateScrollToItem((conversation.messages.size - 2).coerceAtLeast(0))
    }

    LaunchedEffect(conversation.title) {
        launch {
            topBarAnimation.animateTo(-200f)
        }
        launch {
            topBarAnimation.animateTo(0f)
        }
    }

    Column(Modifier.fillMaxWidth()) {
        if (canResetConversation) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max)
                    .heightIn(min = 80.dp)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(start = 16.dp, bottom = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .offset(x = 0.dp, y = topBarAnimation.value.dp),
                    text = conversation.title ?: "New conversation",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .requiredWidth(48.dp)
                        .fillMaxHeight()
                        .clickable { onResetConversation() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        modifier = Modifier,
                        painter = painterResource(id = R.drawable.baseline_close_24),
                        contentDescription = "Close conversation",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
            }
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
                if (conversation.messages.isEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Button(
                                modifier = Modifier
                                    .weight(1f)
                                    .requiredHeight(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                                onClick = onLoadPreviousConversations,
                            ) {
                                Text(
                                    text = "Load previous conversations",
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            }

                            Spacer(Modifier.width(8.dp))

                            Button(
                                modifier = Modifier.requiredHeight(48.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                                onClick = onShowSettings,
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.baseline_settings_24),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                } else {
                    items(conversation.messages, key = { it.id }) { message ->
                        MessageView(
                            modifier = Modifier.animateItemPlacement(),
                            message = message,
                        )
                    }
                }
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
}
