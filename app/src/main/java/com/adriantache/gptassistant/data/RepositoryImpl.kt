package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.data.model.ChatMessage.Companion.toChatMessages
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData

class RepositoryImpl(
    private val service: OpenAiApi,
    private val firebaseDatabase: FirebaseDatabaseImpl, // todo hide behind interface
) : Repository {
    override suspend fun getReply(conversation: ConversationData): Result<Message> {
        val response = try {
            service.getCompletions(OpenAiRequest(messages = conversation.toChatMessages()))
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (response.isSuccessful) {
            val answer = response.body()?.choices?.lastOrNull()?.message?.content.orEmpty()
            Result.success(Message.GptMessage(answer))
        } else {
            Result.failure(IllegalArgumentException(response.errorBody()?.string()))
        }
    }

    override suspend fun saveConversation(conversation: ConversationData) {
        firebaseDatabase.saveConversation(conversation = conversation)
    }

    override suspend fun getConversations(): List<ConversationData> {
        return firebaseDatabase.getConversations()
    }

    override suspend fun deleteConversation(conversationId: String) {
        firebaseDatabase.deleteConversation(conversationId)
    }
}
