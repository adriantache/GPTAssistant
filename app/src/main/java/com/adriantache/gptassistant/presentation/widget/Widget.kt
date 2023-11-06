package com.adriantache.gptassistant.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject

// TODO: improve error states, UI in general
// TODO: continue with https://developer.android.com/jetpack/compose/glance/user-interaction
class Widget : GlanceAppWidget(), KoinComponent {
    private val useCases: ConversationUseCases by inject()
    private val audioRecognizer: AudioRecognizer by inject()

    // TTS needs to be initialized before we ask it to speak, and that can take a bit of time...
    private val tts: TtsHelper = get()

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
//            val state = WidgetStateHelper.getState(currentState())

            var output by remember { mutableStateOf("GPT") }

            val recognizerState by audioRecognizer.state.collectAsState()
            LaunchedEffect(recognizerState) {
                when (val recognizerStateValue = recognizerState) {
                    is Success -> {
                        recognizerStateValue.result.value?.let {
                            output = "Wait"
                            useCases.onInput(it, fromWidget = true)
                            useCases.onSubmit(true)
                        }
                    }

                    Failure -> output = "Fail"

                    else -> Unit
                }
            }

            val events by useCases.events.collectAsState()
            LaunchedEffect(events) {
                when (val event = events?.value) {
                    is ConversationEvent.SpeakReply -> {
                        output = "Listen"
                        tts.speak(event.output).collect {
                            if (!it) {
                                output = "Done"

                                delay(1000)

                                output = "GPT"
                            }
                        }
                    }

                    else -> Unit
                }
            }

            Box(
                modifier = GlanceModifier.fillMaxSize()
//                    .background(ImageProvider(R.drawable.baseline_filter_alt_24))
                    .cornerRadius(16.dp)
                    .appWidgetBackground(),
                contentAlignment = Alignment.Center,
            ) {
                MyContent(
                    output = output,
                    onClick = {
                        output = "Speak"
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
