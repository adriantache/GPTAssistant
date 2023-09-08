package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.data.model.ChatMessage
import com.adriantache.gptassistant.data.model.ChatRole

@Composable
fun ConversationView(
    chatMessage: ChatMessage,
) {
    val bgColor = if (chatMessage.role == ChatRole.user) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
    val textColor = if (chatMessage.role == ChatRole.user) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondary
    val contentAlignment = if (chatMessage.role == ChatRole.user) Arrangement.End else Arrangement.Start

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
    }
}
