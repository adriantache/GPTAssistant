package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClearConversationDialog(
    onConfirm: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    var shouldSaveConversation by remember { mutableStateOf(true) }

    AlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Reset conversation",
                style = MaterialTheme.typography.titleSmall,
            )

            Text("Are you sure you want to clear this conversation and start a new one?")

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = shouldSaveConversation, onCheckedChange = { shouldSaveConversation = it })

                Text("Save conversation")
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }

                TextButton(onClick = { onConfirm(shouldSaveConversation) }) {
                    Text("Ok")
                }
            }
        }
    }
}
