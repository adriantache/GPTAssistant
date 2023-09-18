package com.adriantache.gptassistant.data.model

import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.adriantache.gptassistant.domain.model.data.MessageData
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val content: String,
    val role: ChatRole = ChatRole.user,
) {
    companion object {
        fun ConversationData.toChatMessages(): List<ChatMessage> {
            return this.messages.map {
                val role = when (it) {
                    is MessageData.AdminMessage -> ChatRole.system
                    is MessageData.GptMessage -> ChatRole.assistant
                    is MessageData.UserMessage -> ChatRole.user
                }

                ChatMessage(content = it.content, role = role)
            }
        }
    }
}

@Suppress("EnumEntryName")
enum class ChatRole {
    system, assistant, user
}
