package com.adriantache.gptassistant.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.adriantache.gptassistant.data.Repository
import com.adriantache.gptassistant.presentation.view.InputRow
import com.adriantache.gptassistant.ui.theme.GPTAssistantTheme
import kotlinx.coroutines.launch


// TODO: handle requesting microphone permission

class MainActivity : ComponentActivity() {
    private val repository by lazy { Repository() }

    private lateinit var tts: TTS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val coroutineScope = rememberCoroutineScope()
            val context = LocalContext.current
            val scrollState = rememberScrollState()

            var output by remember { mutableStateOf("") }
            var isLoading by remember { mutableStateOf(false) }

            KeepScreenOn(isLoading)

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
                            InputRow(isLoading, ::stopTTS) { input, fromSpeech ->
                                coroutineScope.launch {
                                    if (input.isEmpty()) {
                                        output = "Please enter something!"
                                        return@launch
                                    }

                                    output = ""
                                    isLoading = true
                                    output = repository.getReply(input)
                                    isLoading = false

                                    if (fromSpeech) {
                                        tts.speak(output)
                                    }
                                }
                            }
                        }

                        item {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = output,
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        tts = TTS(this)
    }

    override fun onDestroy() {
        tts.destroy()

        super.onDestroy()
    }

    private fun stopTTS() {
        tts.stop()
    }
}
