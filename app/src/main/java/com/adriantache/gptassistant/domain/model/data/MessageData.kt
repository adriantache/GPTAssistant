package com.adriantache.gptassistant.domain.model.data

import com.adriantache.gptassistant.domain.model.Message
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed class MessageData {
    abstract val content: String
    abstract val id: String?

    @Serializable
    data class UserMessage(
        override val content: String,
        override val id: String? = null,
    ) : MessageData()

    @Serializable
    data class GptMessage(
        override val content: String,
        override val id: String? = null,
    ) : MessageData()

    @Serializable
    data class AdminMessage(
        override val content: String,
        override val id: String? = null,
    ) : MessageData()

    fun toMessage(): Message {
        return when (this) {
            is AdminMessage -> Message.AdminMessage(content, id ?: UUID.randomUUID().toString())
            is GptMessage -> Message.GptMessage(content, id ?: UUID.randomUUID().toString())
            is UserMessage -> Message.UserMessage(content, id ?: UUID.randomUUID().toString())
        }
    }

    companion object {
        fun Message.toData(): MessageData? {
            return when (this) {
                is Message.AdminMessage -> AdminMessage(content, id)
                is Message.GptMessage -> GptMessage(content, id)
                is Message.UserMessage -> UserMessage(content, id)
                is Message.Loading -> null
            }
        }
    }
}
