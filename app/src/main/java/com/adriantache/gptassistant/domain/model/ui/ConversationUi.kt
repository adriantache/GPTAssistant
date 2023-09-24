package com.adriantache.gptassistant.domain.model.ui

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.Message

data class ConversationUi(
    val id: String,
    val title: String?,
    val startedAt: Long,
    val latestInput: String,
    val messages: List<Message>,
    val isLoading: Boolean,
    val errorMessage: String?,
    val canResetConversation: Boolean,
    val canSubmit: Boolean,
) {
    fun toEntity(): Conversation {
        return Conversation(
            id = id,
            startedAt = startedAt,
            title = title,
            messages = messages,
            latestInput = latestInput,
            errorMessage = errorMessage,
        )
    }

    companion object {
        fun Conversation.toUi(
            isLoading: Boolean,
        ): ConversationUi {
            return ConversationUi(
                id = id,
                title = title,
                startedAt = startedAt,
                latestInput = latestInput,
                messages = messages,
                isLoading = isLoading,
                errorMessage = errorMessage,
                canResetConversation = canResetConversation,
                canSubmit = canSubmit,
            )
        }
    }
}
