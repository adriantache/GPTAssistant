package com.adriantache.gptassistant.presentation.tts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Failure
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Ready
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Recognizing
import com.adriantache.gptassistant.presentation.tts.AudioRecognizer.RecognizerState.Success
import kotlinx.coroutines.flow.MutableStateFlow

class AudioRecognizer(context: Context) {
    @Suppress("kotlin:S6305")
    val state: MutableStateFlow<RecognizerState> = MutableStateFlow(Ready)

    private val recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                state.value = Recognizing(0f)
            }

            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) {
                if (state.value is Recognizing) {
                    state.value = Recognizing((rmsdB * 10).toInt() / 100f)
                }
            }

            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() {
                state.value = Ready
            }

            override fun onError(error: Int) {
                state.value = Failure
            }

            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onResults(results: Bundle) {
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val output = matches?.get(0) ?: ""

                state.value = Success(output)
            }
        })
    }
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
    }

    fun startListening() {
        state.value = Ready

        recognizer.startListening(recognizerIntent)
    }

    fun stopListening() {
        recognizer.stopListening()
    }

    sealed interface RecognizerState {
        data object Ready : RecognizerState
        data class Recognizing(val amplitudePercent: Float) : RecognizerState
        data class Success(val result: String) : RecognizerState
        data object Failure : RecognizerState
    }
}
