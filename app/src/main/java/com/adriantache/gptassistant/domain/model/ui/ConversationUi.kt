package com.adriantache.gptassistant.domain.model.ui

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.Message

data class ConversationUi(
    val latestInput: String,
    val conversation: List<Message>,
    val isLoading: Boolean,
    val canResetConversation: Boolean,
    val areMessagesVisible: Boolean,
    val canSubmit: Boolean,
) {
    companion object {
        fun Conversation.toUi(
            isLoading: Boolean,
        ): ConversationUi {
            return ConversationUi(
                latestInput = latestInput,
                conversation = messages,
                isLoading = isLoading,
                canResetConversation = canResetConversation,
                areMessagesVisible = areMessagesVisible,
                canSubmit = canSubmit,
            )
        }
    }
}
