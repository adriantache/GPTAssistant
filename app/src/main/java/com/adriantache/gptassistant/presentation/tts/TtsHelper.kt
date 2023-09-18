package com.adriantache.gptassistant.presentation.tts

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TtsHelper(context: Context) {
    private val tts = getTts(context)
    private val audioManager = context.getSystemService(ComponentActivity.AUDIO_SERVICE) as AudioManager
    private val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT).build()

    fun speak(text: String): StateFlow<Boolean> {
        audioManager.requestAudioFocus(audioFocusRequest)
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)

        return getStatusFlow()
    }

    fun stop() {
        tts.stop()
    }

    // TODO: see if necessary
    fun onDestroy() {
        tts.shutdown()
    }

    private fun getTts(context: Context): TextToSpeech {
        return TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                Log.e("TTS", "Initialization Failed!")
                return@TextToSpeech
            }
        }.apply {
            setPitch(1f)
            setSpeechRate(1f)
            setLanguage(Locale.US).apply {
                if (this in listOf(TextToSpeech.LANG_MISSING_DATA, TextToSpeech.LANG_NOT_SUPPORTED)) {
                    Log.e("TTS", "This Language is not supported")
                }
            }
        }
    }

    // Used because the status listener isn't working.
    private fun getStatusFlow(): StateFlow<Boolean> {
        val statusFlow = MutableStateFlow(true)

        CoroutineScope(Dispatchers.Default).launch {
            while (tts.isSpeaking) {
                delay(100)

                if (!tts.isSpeaking) {
                    audioManager.abandonAudioFocusRequest(audioFocusRequest)
                }

                statusFlow.value = tts.isSpeaking
            }
        }

        return statusFlow
    }
}
