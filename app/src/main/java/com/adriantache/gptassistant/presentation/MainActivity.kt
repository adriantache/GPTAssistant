package com.adriantache.gptassistant.presentation

import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.data.Repository
import com.adriantache.gptassistant.presentation.view.InputRow
import com.adriantache.gptassistant.ui.theme.GPTAssistantTheme
import kotlinx.coroutines.launch


// TODO: handle requesting microphone permission

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
            val scrollState = rememberScrollState()
            val keyboard = LocalSoftwareKeyboardController.current

            var output: String? by remember { mutableStateOf(null) }
            var isLoading: Boolean by remember { mutableStateOf(false) }

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
                    color = MaterialTheme.colorScheme.background
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp)
                            .scrollable(scrollState, Orientation.Vertical),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        item {
                            InputRow(
                                isLoading = isLoading,
                                stopTts = ::stopTts,
                                isTtsSpeaking = tts.isTtsSpeaking,
                            ) { input, fromSpeech ->
                                coroutineScope.launch {
                                    if (input.isEmpty()) {
                                        output = "Please enter something!"
                                        return@launch
                                    }

                                    output = null
                                    keyboard?.hide()
                                    isLoading = true
                                    output = repository.getReply(input)
                                    isLoading = false

                                    if (fromSpeech) {
                                        audioManager.requestAudioFocus(audioFocusRequest)
                                        tts.speak(output.orEmpty())
                                    }
                                }
                            }
                        }

                        item {
                            if (output != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.primary, OutlinedTextFieldDefaults.shape)
                                        .padding(8.dp),
                                ) {
                                    Text(
                                        modifier = Modifier.fillMaxWidth(),
                                        text = output.orEmpty(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun stopTts() {
        tts.stop()
    }
}
