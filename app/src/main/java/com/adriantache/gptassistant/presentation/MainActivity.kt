package com.adriantache.gptassistant.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.adriantache.gptassistant.di.koinSetup
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.presentation.tts.TtsHelper
import com.adriantache.gptassistant.presentation.view.ClearConversationDialog
import com.adriantache.gptassistant.presentation.view.ConversationView
import com.adriantache.gptassistant.presentation.view.PreviousConversationsDialog
import com.adriantache.gptassistant.ui.theme.GPTAssistantTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val useCases: ConversationUseCases by inject()
    private val tts: TtsHelper by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        koinSetup()

        setContent {
            val keyboard = LocalSoftwareKeyboardController.current

            var showResetConfirmation: Boolean by remember { mutableStateOf(false) }
            var showPreviousConversationsDialog: Boolean by remember { mutableStateOf(false) }
            var showErrorMessage: String? by remember { mutableStateOf(null) }

            var isTtsSpeaking by remember { mutableStateOf(false) }

            val screenState by useCases.state.collectAsState()
            val events by useCases.events.collectAsState()

            KeepScreenOn(screenState.isLoading)

            LaunchedEffect(events) {
                when (val event = events?.value) {
                    is ConversationEvent.SpeakReply -> tts.speak(event.output)
                        .collect { isTtsSpeaking = it }

                    is ConversationEvent.Error -> showErrorMessage = event.message ?: "An error has occurred."

                    null -> Unit
                }
            }

            GPTAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    ConversationView(
                        conversation = screenState.messages,
                        isLoading = screenState.isLoading,
                        isTtsSpeaking = isTtsSpeaking,
                        input = screenState.latestInput,
                        onInput = useCases::onInput,
                        onSubmit = { fromSpeech ->
                            keyboard?.hide()

                            useCases.onSubmit(fromSpeech = fromSpeech)
                        },
                        stopTTS = {
                            tts.stop()
                        },
                        canResetConversation = screenState.canResetConversation,
                        onResetConversation = { showResetConfirmation = true },
                        onLoadPreviousConversations = { showPreviousConversationsDialog = true },
                    )

                    if (showPreviousConversationsDialog) {
                        PreviousConversationsDialog(
                            getConversationHistory = useCases::getConversations,
                            onLoadConversation = useCases::onLoadConversation,
                            onDismiss = { showPreviousConversationsDialog = false }
                        )
                    }

                    if (showResetConfirmation) {
                        ClearConversationDialog(
                            onConfirm = {
                                useCases.onResetConversation()
                                showResetConfirmation = false
                            },
                            onDismiss = { showResetConfirmation = false },
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

//                    BezierAnimation()
//                    BezierAnimation()
//                    BezierAnimation()
//                    BezierAnimation()

//                    Box(
//                        modifier = Modifier.fillMaxSize(),
//                        contentAlignment = Alignment.Center,
//                    ) {
//                        Waves()
//                    }
                }
            }
        }
    }
}
