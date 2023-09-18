package com.adriantache.gptassistant.domain.model.data

import com.adriantache.gptassistant.domain.model.Message
import kotlinx.serialization.Serializable

@Serializable
sealed class MessageData {
    abstract val content: String

    @Serializable
    data class UserMessage(override val content: String) : MessageData()

    @Serializable
    data class GptMessage(override val content: String) : MessageData()

    @Serializable
    data class AdminMessage(override val content: String) : MessageData()

    companion object {
        fun Message.toData(): MessageData {
            return when (this) {
                is Message.AdminMessage -> AdminMessage(content)
                is Message.GptMessage -> GptMessage(content)
                is Message.UserMessage -> UserMessage(content)
            }
        }
    }
}
