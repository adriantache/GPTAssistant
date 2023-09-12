package com.adriantache.gptassistant.data.model

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.Message
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val content: String,
    val role: ChatRole = ChatRole.user,
) {
    companion object {
        fun Conversation.toChatMessages(): List<ChatMessage> {
            return this.messages.map {
                val role = when (it) {
                    is Message.AdminMessage -> ChatRole.system
                    is Message.GptMessage -> ChatRole.assistant
                    is Message.UserMessage -> ChatRole.user
                }

                ChatMessage(content = it.content, role = role)
            }
        }
    }
}

@Suppress("EnumEntryName", "Unused")
enum class ChatRole {
    system, assistant, user
}
