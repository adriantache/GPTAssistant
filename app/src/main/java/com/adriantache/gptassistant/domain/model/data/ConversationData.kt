package com.adriantache.gptassistant.domain.model.data

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.data.MessageData.Companion.toData
import kotlinx.serialization.Serializable

@Serializable
data class ConversationData(
    val title: String? = null,
    val messages: List<MessageData> = emptyList(),
) {
    fun toConversation(): Conversation {
        return Conversation(
            title = title,
            messages = messages.map { it.toMessage() },
            latestInput = "",
            errorMessage = null,
        )
    }

    companion object {
        fun Conversation.toData(title: String? = null): ConversationData {
            return ConversationData(
                title = title,
                messages = messages.mapNotNull { it.toData() },
            )
        }
    }
}
