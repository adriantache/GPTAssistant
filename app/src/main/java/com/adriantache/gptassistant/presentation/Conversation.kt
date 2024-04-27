package com.adriantache.gptassistant.presentation

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.adriantache.gptassistant.data.model.GeneratedImage
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.SettingsUseCases
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.presentation.tts.TtsHelper
import com.adriantache.gptassistant.presentation.view.ConversationView
import com.adriantache.gptassistant.presentation.view.ImageGenerationView
import com.adriantache.gptassistant.presentation.view.PreviousConversationsDialog
import com.adriantache.gptassistant.presentation.view.SettingsScreen
import org.koin.androidx.compose.get

@Composable
fun Conversation(
    useCases: ConversationUseCases = get(),
    settingsUseCases: SettingsUseCases = get(),
    tts: TtsHelper = get(),
) {
    val keyboard = LocalSoftwareKeyboardController.current

    var showPreviousConversationsDialog: Boolean by remember { mutableStateOf(false) }
    var showSettings: Boolean by remember { mutableStateOf(false) }
    var showErrorMessage: String? by remember { mutableStateOf(null) }
    var showImage: GeneratedImage? by remember { mutableStateOf(null) }

    var isTtsSpeaking by remember { mutableStateOf(false) }

    val screenState by useCases.state.collectAsState()
    val events by useCases.events.collectAsState()
    val settings by settingsUseCases.settings.collectAsState()

    KeepScreenOn(screenState.isLoading || isTtsSpeaking)

    // TODO: move this logic to use case, interacting with platform layer directly
    LaunchedEffect(events) {
        when (val event = events?.value) {
            is ConversationEvent.SpeakReply -> tts.speak(event.output)
                .collect { isTtsSpeaking = it }

            is ConversationEvent.Error -> showErrorMessage = event.message ?: "An error has occurred."

            is ConversationEvent.ShowImage -> showImage = event.image

            null -> Unit
        }
    }

    ConversationView(
        conversation = screenState,
        isLoading = screenState.isLoading,
        isTtsSpeaking = isTtsSpeaking,
        input = screenState.latestInput,
        onInput = useCases::onInput,
        onSubmit = { fromSpeech, isImageGeneration ->
            keyboard?.hide()

            if (isImageGeneration) {
                useCases.onRequestImage()
            } else {
                useCases.onSubmit(fromSpeech = fromSpeech)
            }
        },
        stopTTS = { tts.stop() },
        canResetConversation = screenState.canResetConversation,
        onResetConversation = { useCases.onResetConversation() },
        onLoadPreviousConversations = { showPreviousConversationsDialog = true },
        onShowSettings = { showSettings = true },
        isConversationMode = settings.isConversationMode,
        onEditMessage = { useCases.onEditMessage(it) },
        onDuplicate = { useCases.onDuplicate() },
    )

    if (showPreviousConversationsDialog) {
        PreviousConversationsDialog(
            getConversationHistory = useCases::getConversations,
            onLoadConversation = useCases::onLoadConversation,
            onDismiss = { showPreviousConversationsDialog = false }
        )
    }

    if (showErrorMessage != null) {
        AlertDialog(
            text = { Text(showErrorMessage.orEmpty()) },
            confirmButton = {
                Button(onClick = { showErrorMessage = null }) {
                    Text("Ok")
                }
            },
            onDismissRequest = { showErrorMessage = null },
        )
    }

    if (showSettings) {
        SettingsScreen(
            settings = settings,
            onDismiss = { showSettings = false },
        )
    }

    showImage?.let {
        ImageGenerationView(image = it) {
            showImage = null
        }
    }
}
