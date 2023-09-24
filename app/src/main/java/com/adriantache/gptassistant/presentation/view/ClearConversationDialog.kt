package com.adriantache.gptassistant.presentation.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ClearConversationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Reset conversation")
        },
        text = {
            Text("This conversation has been automatically saved. Are you sure you want to clear this conversation and start a new one?")
        },
        confirmButton = {
            Button(onClick = { onConfirm() }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}
