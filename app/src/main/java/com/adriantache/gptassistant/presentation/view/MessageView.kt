package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.R
import com.adriantache.gptassistant.domain.model.Message

// TODO: support admin messages
@Composable
fun MessageView(
    chatMessage: Message,
) {
    val isUserMessage = chatMessage is Message.UserMessage
    val bgColor = if (isUserMessage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor = if (isUserMessage) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val contentAlignment = if (isUserMessage) Arrangement.End else Arrangement.Start

    val clipboardManager = LocalClipboardManager.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = contentAlignment,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.75f)
                .background(bgColor, OutlinedTextFieldDefaults.shape)
                .padding(8.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = chatMessage.content,
                style = MaterialTheme.typography.bodyMedium,
                color = textColor,
            )
        }

        if (!isUserMessage) {
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
