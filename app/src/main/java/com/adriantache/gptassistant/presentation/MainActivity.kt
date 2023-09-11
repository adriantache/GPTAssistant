package com.adriantache.gptassistant.presentation

import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.data.Repository
import com.adriantache.gptassistant.data.Repository.Companion.toError
import com.adriantache.gptassistant.data.model.ChatMessage
import com.adriantache.gptassistant.presentation.view.ClearConversationDialog
import com.adriantache.gptassistant.presentation.view.ConversationView
import com.adriantache.gptassistant.presentation.view.InputRow
import com.adriantache.gptassistant.ui.theme.GPTAssistantTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository by lazy { Repository() }

    private lateinit var tts: TTS
    private lateinit var audioManager: AudioManager
    private val audioFocusRequest = AudioFocusRequest.Builder(AUDIOFOCUS_GAIN_TRANSIENT).build()

    override fun onResume() {
        super.onResume()

        tts = TTS(this)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
    }

    override fun onDestroy() {
        tts.stop()
        tts.destroy()

        super.onDestroy()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            val scrollState = rememberLazyListState()
            val keyboard = LocalSoftwareKeyboardController.current

            var input by rememberSaveable { mutableStateOf("") }
            var output: List<ChatMessage> by rememberSaveable { mutableStateOf(emptyList()) }
            var isLoading: Boolean by remember { mutableStateOf(false) }
            var showResetConfirmation: Boolean by remember { mutableStateOf(false) }

            val isTtsSpeaking by tts.isTtsSpeaking.collectAsState()

            KeepScreenOn(isLoading)

            LaunchedEffect(isTtsSpeaking) {
                if (!isTtsSpeaking) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                }
            }

            GPTAssistantTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 16.dp),
                    ) {
                        InputRow(
                            isLoading = isLoading,
                            stopTts = ::stopTts,
                            isTtsSpeaking = tts.isTtsSpeaking,
                            input = input,
                            onInput = { input = it },
                        ) { input, fromSpeech ->
                            coroutineScope.launch {
                                if (input.isEmpty()) {
                                    output = "Please enter something!".toError()
                                    return@launch
                                }

                                keyboard?.hide()
                                isLoading = true

                                output += ChatMessage(input)
                                output = repository.getReply(input)
                                isLoading = false

                                scrollState.animateScrollToItem((output.size - 2).coerceAtLeast(0))

                                if (fromSpeech) {
                                    audioManager.requestAudioFocus(audioFocusRequest)
                                    tts.speak(output.lastOrNull()?.content.orEmpty())
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            state = scrollState,
                        ) {
                            items(output) {
                                ConversationView(it)
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            onClick = {
                                if (output.isEmpty()) return@Button

                                showResetConfirmation = true
                            }
                        ) {
                            Text("Reset conversation")
                        }
                    }
                }

                if (showResetConfirmation) {
                    ClearConversationDialog(
                        onConfirm = {
                            input = ""
                            output = emptyList()
                            repository.resetConversation()
                            showResetConfirmation = false
                        },
                        onDismiss = { showResetConfirmation = false },
                    )
                }
            }
        }
    }

    private fun stopTts() {
        tts.stop()
    }
}
