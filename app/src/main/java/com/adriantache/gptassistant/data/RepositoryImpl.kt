package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.data.model.ChatMessage.Companion.toChatMessages
import com.adriantache.gptassistant.data.model.OpenAiImageRequest
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData

private const val AUTH_HEADER_PREFIX = "Bearer "
private const val HISTORY_ID_KEY = "HISTORY_ID_KEY"

class RepositoryImpl(
    private val service: OpenAiApi,
    private val apiKeyDataSource: ApiKeyDataSource,
    private val firebaseDatabase: FirebaseDatabaseImpl, // todo hide behind interface
) : Repository {
    override suspend fun getReply(conversation: ConversationData): Result<Message> {
        val authHeader = apiKeyDataSource.apiKey?.let { AUTH_HEADER_PREFIX + it }

        val response = try {
            service.getCompletions(
                authHeader = authHeader ?: return Result.failure(IllegalArgumentException("No API key!")),
                request = OpenAiRequest(messages = conversation.toChatMessages()),
            )
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

    override suspend fun getImage(prompt: String): Result<String> {
        val authHeader = apiKeyDataSource.apiKey?.let { AUTH_HEADER_PREFIX + it }

        val response = try {
            service.getImageGeneration(
                authHeader = authHeader ?: return Result.failure(IllegalArgumentException("No API key!")),
                request = OpenAiImageRequest(prompt),
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (response.isSuccessful) {
            val answer = response.body()?.url ?: return Result.failure(IllegalArgumentException("No body: ${response.body()}"))

            Result.success(answer)
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
