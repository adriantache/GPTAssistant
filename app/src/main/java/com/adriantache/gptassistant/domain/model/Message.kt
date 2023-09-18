package com.adriantache.gptassistant.domain.model

sealed interface Message {
    val content: String

    data class UserMessage(override val content: String) : Message
    data class GptMessage(override val content: String) : Message
    data class AdminMessage(override val content: String) : Message

    data object Loading : Message {
        override val content: String = ""
    }
}
