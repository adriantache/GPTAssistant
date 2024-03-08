package com.adriantache.gptassistant.domain.data

import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData

interface Repository {
    suspend fun getReply(conversation: ConversationData): Result<Message>

    suspend fun getImage(prompt: String): Result<String>

    suspend fun saveConversation(conversation: ConversationData)

    suspend fun getConversations(): List<ConversationData>

    suspend fun deleteConversation(conversationId: String)
}
