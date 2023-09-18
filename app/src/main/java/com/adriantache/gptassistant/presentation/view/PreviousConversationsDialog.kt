package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontStyle.Companion.Normal
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.domain.model.Conversation

private var newConversation = Conversation(title = "New conversation")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PreviousConversationsDialog(
    getConversationHistory: suspend () -> List<Conversation>,
    onLoadConversation: (Conversation) -> Unit,
    onDismiss: () -> Unit,
) {
    var conversations by remember {
        mutableStateOf(emptyList<Conversation>())
    }

    LaunchedEffect(Unit) {
        conversations = listOf(newConversation) + getConversationHistory()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Text("Pick a saved conversation.")

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(conversations) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(min = 48.dp)
                        .clickable {
                            if (it == newConversation) {
                                onDismiss()
                            } else {
                                onLoadConversation(it)
                                onDismiss()
                            }
                        }
                        .background(MaterialTheme.colorScheme.secondaryContainer)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = it.title ?: it.messages.first().content.take(50),
                        fontStyle = if (it == newConversation) Italic else Normal,
                    )
                }
            }
        }
    }
}
