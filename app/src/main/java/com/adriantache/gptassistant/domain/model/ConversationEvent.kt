package com.adriantache.gptassistant.domain.model

sealed interface ConversationEvent {
    data class SpeakReply(val output: String) : ConversationEvent
    data class Error(val message: String?) : ConversationEvent

    data class ShowImage(val url: String) : ConversationEvent
}
