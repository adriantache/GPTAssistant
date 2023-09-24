package com.adriantache.gptassistant.domain.model.data

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.data.MessageData.Companion.toData
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ConversationData(
    val id: String? = null,
    val startedAt: Long? = null,
    val title: String? = null,
    val messages: List<MessageData> = emptyList(),
) {
    fun toConversation(): Conversation {
        return Conversation(
            id = id ?: UUID.randomUUID().toString(),
            startedAt = startedAt ?: 0L,
            title = title,
            messages = messages.map { it.toMessage() },
            latestInput = "",
            errorMessage = null,
        )
    }

    companion object {
        fun Conversation.toData(): ConversationData {
            return ConversationData(
                id = id,
                startedAt = startedAt,
                title = title,
                messages = messages.mapNotNull { it.toData() },
            )
        }
    }
}
