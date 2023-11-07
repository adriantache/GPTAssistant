package com.adriantache.gptassistant.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.appWidgetBackground
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import com.adriantache.gptassistant.domain.ConversationUseCases
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Failure
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Success
import com.adriantache.gptassistant.presentation.tts.TtsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

// TODO: update this after implementing proper states
// TODO: improve error states, UI in general
class Widget : GlanceAppWidget(), KoinComponent {
    private val useCases: ConversationUseCases by inject()
    private val audioRecognizer: AudioRecognizer by inject()

    // TTS needs to be initialized before we ask it to speak, and that can take a bit of time...
    private val tts: TtsHelper = get()

    private var output = "GPT"

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            val scope = rememberCoroutineScope()

            fun updateOutput(newOutput: String) {
                output = newOutput

                scope.launch {
                    update(context, id)
                }
            }

            val recognizerState by audioRecognizer.state.collectAsState()
            LaunchedEffect(recognizerState) {
                when (val recognizerStateValue = recognizerState) {
                    is Success -> {
                        recognizerStateValue.result.value?.let {
                            updateOutput("Wait")
                            useCases.onInput(it, fromWidget = true)
                            useCases.onSubmit(true)
                        }
                    }

                    Failure -> updateOutput("Fail")

                    else -> Unit
                }
            }

            val events by useCases.events.collectAsState()
            LaunchedEffect(events) {
                when (val event = events?.value) {
                    is ConversationEvent.SpeakReply -> {
                        updateOutput("Listen")

                        tts.speak(event.output).collect {
                            if (!it) {
                                updateOutput("Done")

                                delay(1000)

                                updateOutput("GPT")
                            }
                        }
                    }

                    else -> Unit
                }
            }

            Box(
                modifier = GlanceModifier.fillMaxSize()
                    .cornerRadius(16.dp)
                    .appWidgetBackground(),
                contentAlignment = Alignment.Center,
            ) {
                MyContent(
                    output = output,
                    onClick = {
                        updateOutput("Speak")
                        audioRecognizer.startListening()
                    }
                )
            }
        }
    }

    @Composable
    private fun MyContent(
        output: String,
        onClick: () -> Unit,
    ) {
        Button(
            modifier = GlanceModifier.fillMaxWidth().then(GlanceModifier.height(48.dp)),
            text = output,
            onClick = onClick,
        )
    }
}
