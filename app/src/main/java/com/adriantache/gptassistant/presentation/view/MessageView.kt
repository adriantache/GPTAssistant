package com.adriantache.gptassistant.presentation.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import com.adriantache.gptassistant.presentation.util.openSearch

@Composable
fun MessageView(
    chatMessage: Message,
) {
    val bgColor = when (chatMessage) {
        is UserMessage -> MaterialTheme.colorScheme.primary
        Loading -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.secondary
    }
    val textColor = if (chatMessage is UserMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val contentAlignment = if (chatMessage is UserMessage) Arrangement.End else Arrangement.Start
    val modifier = when (chatMessage) {
        is UserMessage,
        is GptMessage,
        is AdminMessage,
        -> Modifier.fillMaxWidth(0.75f)

        Loading -> Modifier
            .fillMaxWidth()
            .requiredHeight(200.dp)
    }

    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = contentAlignment,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (chatMessage is UserMessage) {
            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = {
                    context.openSearch(chatMessage.content)
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_search_24), contentDescription = "Copy message")
            }

            Spacer(Modifier.width(8.dp))
        }

        Box(
            modifier = modifier
                .background(bgColor, OutlinedTextFieldDefaults.shape)
                .padding(8.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = chatMessage.content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )

            if (chatMessage is Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
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
                            .fillMaxHeight()
                            .fillMaxWidth(0.5f)
                            .offset(x = 0.dp, y = (100 * progress.value).dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                            .clip(CircleShape),
                        progress = 0.7f,
                    )
                }
            }
        }

        if (chatMessage is GptMessage || chatMessage is AdminMessage) {
            Spacer(Modifier.width(8.dp))

            IconButton(
                modifier = Modifier.requiredSize(48.dp),
                onClick = {
                    clipboardManager.setText(AnnotatedString(chatMessage.content))
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_content_copy_24), contentDescription = "Copy message")
            }
        }
    }
}
