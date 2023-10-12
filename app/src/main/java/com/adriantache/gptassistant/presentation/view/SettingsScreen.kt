package com.adriantache.gptassistant.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.adriantache.gptassistant.domain.model.ui.SettingsUi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    settings: SettingsUi,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismiss,
    ) {
        Column(
            Modifier
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
            )

            SettingsRow(
                text = "Show input on the bottom of the screen.",
                checked = settings.isInputOnBottom,
                onChecked = settings.setInputOnBottom,
            )

            SettingsRow(
                text = "Hide text input and immediately activate speech recognition after TTS stops speaking.",
                checked = settings.isConversationMode,
                onChecked = settings.setConversationMode,
            )
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    SettingsScreen(
        settings = SettingsUi(
            isInputOnBottom = true,
            isConversationMode = false,
            setConversationMode = {},
            setInputOnBottom = {},
        )
    ) {
    }
}