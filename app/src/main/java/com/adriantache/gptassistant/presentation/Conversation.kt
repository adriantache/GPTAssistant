package com.adriantache.gptassistant.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.ui.unit.IntOffset
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.SettingsUseCases
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.presentation.tts.TtsHelper
import com.adriantache.gptassistant.presentation.view.ConversationView
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

            null -> Unit
        }
    }

    @Suppress("NAME_SHADOWING")
    AnimatedContent(
        targetState = screenState,
        label = "",
        transitionSpec = {
            slideIn(animationSpec = spring(), initialOffset = { IntOffset(it.width, 0) })
                .togetherWith(slideOut(animationSpec = spring(), targetOffset = { IntOffset(-it.width, -it.height) }))
        }
    ) { screenState ->
        ConversationView(
            conversation = screenState,
            isLoading = screenState.isLoading,
            isTtsSpeaking = isTtsSpeaking,
            input = screenState.latestInput,
            onInput = useCases::onInput,
            onSubmit = { fromSpeech ->
                keyboard?.hide()

                useCases.onSubmit(fromSpeech = fromSpeech)
            },
            stopTTS = { tts.stop() },
            canResetConversation = screenState.canResetConversation,
            onResetConversation = { useCases.onResetConversation() },
            onLoadPreviousConversations = { showPreviousConversationsDialog = true },
            onShowSettings = { showSettings = true },
            isInputOnBottom = settings.isInputOnBottom,
            isConversationMode = settings.isConversationMode,
        )
    }

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
}
