package com.adriantache.gptassistant.presentation

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_MISSING_DATA
import android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.speech.tts.TextToSpeech.SUCCESS
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

class TTS(context: Context) {
    private var tts = getTTS(context)

    val isTtsSpeaking = MutableStateFlow(false)

    private fun getTTS(context: Context): TextToSpeech {
        return TextToSpeech(context) { status ->
            if (status != SUCCESS) {
                Log.e("TTS", "Initialization Failed!")
                return@TextToSpeech
            }

            tts.configure()
        }
    }

    private fun TextToSpeech.configure() {
        setPitch(1f)
        setSpeechRate(1f)
        setLanguage(Locale.US).apply {
            if (this in listOf(LANG_MISSING_DATA, LANG_NOT_SUPPORTED)) {
                Log.e("TTS", "This Language is not supported")
            }
        }
    }

    fun speak(text: String) {
        tts.speak(text, QUEUE_FLUSH, null, null)

        broadcastStatus()
    }

    // Used because the status listener isn't working.
    private fun broadcastStatus() {
        isTtsSpeaking.value = true

        CoroutineScope(Dispatchers.Default).launch {
            while (isTtsSpeaking.value) {
                delay(100)

                isTtsSpeaking.value = tts.isSpeaking
            }
        }
    }

    fun destroy() {
        tts.shutdown()
    }

    fun stop() {
        tts.stop()
    }
}
