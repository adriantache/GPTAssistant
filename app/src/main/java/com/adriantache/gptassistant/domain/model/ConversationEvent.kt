package com.adriantache.gptassistant.domain.model

import com.adriantache.gptassistant.data.model.GeneratedImage

sealed interface ConversationEvent {
    data class SpeakReply(val output: String) : ConversationEvent
    data class Error(val message: String?) : ConversationEvent

    data class ShowImage(val image: GeneratedImage) : ConversationEvent
}
