package com.adriantache.gptassistant.presentation.view

import android.view.ViewTreeObserver
import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.Message
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
    onSubmit: (fromSpeech: Boolean, isImageGeneration: Boolean) -> Unit,
    onEditMessage: (Message) -> Unit,
    stopTTS: () -> Unit,
    onResetConversation: () -> Unit,
    onLoadPreviousConversations: () -> Unit,
    onShowSettings: () -> Unit,
    isConversationMode: Boolean,
    onDuplicate: () -> Unit,
) {
    val lazyListState = rememberLazyListState()

    val topBarAnimation = remember { Animatable(-200f) }

    var isImageGeneration by remember { mutableStateOf(false) }

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

    val view = LocalView.current
    val viewTreeObserver = view.viewTreeObserver
    var isKeyboardExpanded by remember { mutableStateOf(false) }

    DisposableEffect(viewTreeObserver) {
        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            isKeyboardExpanded = ViewCompat.getRootWindowInsets(view)?.isVisible(WindowInsetsCompat.Type.ime()) ?: true
        }

        viewTreeObserver.addOnGlobalLayoutListener(listener)

        onDispose {
            viewTreeObserver.removeOnGlobalLayoutListener(listener)
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
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
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
                .fillMaxWidth(),
        ) {
            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                state = lazyListState,
            ) {
                if (conversation.messages.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth()) {
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            painterResource(id = R.drawable.baseline_history_24),
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onSecondary,
                                        )

                                        Spacer(modifier = Modifier.width(8.dp))

                                        Text(
                                            text = "Conversation History",
                                            color = MaterialTheme.colorScheme.onSecondary,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                    }
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

                        Spacer(modifier = Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(8.dp))
                                .padding(16.dp),
                        ) {
                            Text(
                                text = "Welcome to GPT Assistant. Use the voice input or keyboard buttons on the bottom to input your query.\n\n" +
                                        "Use the buttons above to search through previous conversations or update settings.",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))


                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(8.dp))
                                .padding(16.dp),
                        ) {
                            Text(
                                text = "It is now also possible to generate images, but please be aware the cost for that is much higher than conversations.",
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                style = MaterialTheme.typography.titleSmall,
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    modifier = Modifier.clickable { isImageGeneration = false },
                                    text = "Conversation",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )

                                Switch(checked = isImageGeneration, onCheckedChange = { isImageGeneration = it })

                                Text(
                                    modifier = Modifier.clickable { isImageGeneration = true },
                                    text = "Image Generation",
                                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                                )
                            }
                        }
                    }
                } else {
                    items(conversation.messages, key = { it.id }) { message ->
                        MessageView(
                            modifier = Modifier.animateItemPlacement(),
                            message = message,
                            onEditMessage = {
                                onEditMessage(it)
                                isKeyboardExpanded = true
                            },
                            onDuplicate = onDuplicate,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            ConversationInput(
                isLoading = isLoading,
                stopTTS = stopTTS,
                isTtsSpeaking = isTtsSpeaking,
                input = input,
                onInput = onInput,
                onSubmit = { fromSpeech ->
                    isKeyboardExpanded = false
                    onSubmit(fromSpeech, isImageGeneration)
                },
                isConversationMode = isConversationMode,
                isKeyboardExpanded = isKeyboardExpanded,
                onExpandKeyboard = { isKeyboardExpanded = !isKeyboardExpanded },
            )
        }
    }
}
