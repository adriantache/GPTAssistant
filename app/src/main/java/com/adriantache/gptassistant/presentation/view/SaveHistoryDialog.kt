package com.adriantache.gptassistant.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.adriantache.gptassistant.presentation.auth.FirebaseAuthButton

@Composable
fun SaveHistoryDialog(
    onSaveGoogle: (String) -> Unit,
    onSaveLocally: () -> Unit,
    onRejectSaving: () -> Unit,
) {
    Dialog(
        onDismissRequest = { /*ignored since it's a mandatory input*/ },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Choose login type",
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "This app can automatically save your conversations. Please note that you will lose conversation history after uninstalling the app unless you sign in with Google.",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic,
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                FirebaseAuthButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(48.dp),
                    onGotId = onSaveGoogle,
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(48.dp),
                    onClick = { onSaveLocally() }) {
                    Text(text = "Only save conversations locally")
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(48.dp),
                    onClick = { onRejectSaving() }) {
                    Text(text = "Don't save conversations")
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview()
@Composable
private fun SaveHistoryDialogPreview() {
    SaveHistoryDialog({}, {}, {})
}
