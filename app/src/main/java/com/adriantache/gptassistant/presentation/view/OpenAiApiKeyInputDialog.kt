package com.adriantache.gptassistant.presentation.view

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private const val OPENAI_API_KEY_PAGE = "https://platform.openai.com/account/api-keys"

@Composable
fun OpenAiApiKeyInputDialog(
    onSubmit: (String) -> Unit,
) {
    val uriHandler = LocalUriHandler.current

    var input by remember { mutableStateOf("") }

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
                text = "Please enter your OpenAI API key to connect to ChatGPT:",
                style = MaterialTheme.typography.headlineSmall,
            )

            Spacer(Modifier.height(16.dp))

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "Please note that you will need to set up billing in your OpenAI account to access the ChatGPT API.",
                style = MaterialTheme.typography.labelSmall,
                fontStyle = FontStyle.Italic,
            )

            Spacer(Modifier.height(8.dp))

            TextField(
                modifier = Modifier.fillMaxWidth(),
                value = input,
                enabled = true,
                onValueChange = { input = it },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit(input) }),
            )

            Spacer(Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        uriHandler.openUri(OPENAI_API_KEY_PAGE)
                    }
                    .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                    .padding(8.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Click here to open the OpenAI website to get a key.",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onSubmit(input) }) {
                    Text("Submit")
                }
            }
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview
@Composable
private fun ApiKeyInputPreview() {
    OpenAiApiKeyInputDialog(
        onSubmit = {},
    )
}
