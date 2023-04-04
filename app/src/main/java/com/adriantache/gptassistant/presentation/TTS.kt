package com.adriantache.gptassistant.presentation

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.*
import android.util.Log
import java.util.*

class TTS(context: Context) {
    private var tts = getTTS(context)

    private fun getTTS(context: Context): TextToSpeech {
        return TextToSpeech(context) { status ->
            if (status != SUCCESS) {
                Log.e("TTS", "Initialization Failed!")
                return@TextToSpeech
            }

            configureTTS()
        }
    }

    private fun configureTTS() {
        tts.setPitch(1f)
        tts.setSpeechRate(1f)
        tts.setLanguage(Locale.US).apply {
            if (this in listOf(LANG_MISSING_DATA, LANG_NOT_SUPPORTED)) {
                Log.e("TTS", "This Language is not supported")
            }
        }
    }

    fun speak(text: String) {
        tts.speak(text, QUEUE_FLUSH, null, null)
    }

    fun destroy() {
        tts.shutdown()
    }

    fun stop() {
        tts.stop()
    }
}
