package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.Message.AdminMessage
import com.adriantache.gptassistant.domain.model.Message.GptMessage
import com.adriantache.gptassistant.domain.model.Message.Loading
import com.adriantache.gptassistant.domain.model.Message.UserMessage
import com.adriantache.gptassistant.presentation.util.Markdown
import com.adriantache.gptassistant.presentation.util.openSearch

@Composable
fun MessageView(
    modifier: Modifier = Modifier,
    message: Message,
    onEditMessage: (Message) -> Unit,
    onDuplicate: () -> Unit,
) {
    val bgColor = when (message) {
        is UserMessage -> MaterialTheme.colorScheme.primary
        is Loading -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.secondary
    }
    val textColor = if (message is UserMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val contentAlignment = if (message is UserMessage) Arrangement.End else Arrangement.Start
    val finalModifier = when (message) {
        is UserMessage,
        is GptMessage,
        is AdminMessage,
        -> modifier.fillMaxWidth(0.8f)

        is Loading -> modifier
            .fillMaxWidth()
            .requiredHeight(200.dp)
    }
    val fontStyle = if (message is UserMessage) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = contentAlignment,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (message is UserMessage) {
            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = onDuplicate,
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_call_split_24), contentDescription = "Duplicate")
            }

            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = {
                    context.openSearch(message.content)
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_search_24), contentDescription = "Search")
            }

            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = {
                    onEditMessage(message)
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_edit_24), contentDescription = "Edit message")
            }

            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = finalModifier
                .background(bgColor, OutlinedTextFieldDefaults.shape)
                .padding(12.dp),
        ) {
            Markdown(
                modifier = Modifier.fillMaxWidth(),
                text = message.content,
                style = fontStyle,
                color = textColor,
            )

            if (message is Loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    val progress = remember { Animatable(initialValue = 0f) }

                    LaunchedEffect(Unit) {
                        progress.animateTo(
                            1f,
                            animationSpec = tween(
                                durationMillis = 90000,
                                easing = LinearEasing
                            ),
                        )
                    }

                    WavesLoadingIndicator(
                        modifier = Modifier
                            .requiredSize(160.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clip(CircleShape),
                        progress = 0.7f,
                    )
                }
            }
        }

        if (message is GptMessage || message is AdminMessage) {
            Spacer(Modifier.width(8.dp))

            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = {
                    clipboardManager.setText(AnnotatedString(message.content))
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_content_copy_24), contentDescription = "Copy message")
            }
        }
    }
}
