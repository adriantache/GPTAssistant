package com.adriantache.gptassistant.domain.model

import java.util.UUID

sealed interface Message {
    val content: String
    val id: String

    data class UserMessage(
        override val content: String,
        override val id: String = UUID.randomUUID().toString() + content.hashCode(),
    ) : Message

    data class GptMessage(
        override val content: String,
        override val id: String = UUID.randomUUID().toString() + content.hashCode(),
    ) : Message

    data class AdminMessage(
        override val content: String,
        override val id: String = UUID.randomUUID().toString() + content.hashCode(),
    ) : Message

    data class Loading(
        override val content: String = "",
        override val id: String = UUID.randomUUID().toString() + content.hashCode(),
    ) : Message
}
