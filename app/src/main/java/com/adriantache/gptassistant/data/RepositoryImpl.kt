package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.data.model.ChatMessage.Companion.toChatMessages
import com.adriantache.gptassistant.data.model.GeneratedImage
import com.adriantache.gptassistant.data.model.OpenAiImageRequest
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.model.StabilityAiFinishReason
import com.adriantache.gptassistant.data.model.StabilityAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.StabilityAiApi
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData

private const val AUTH_HEADER_PREFIX = "Bearer "
private const val NO_API_KEY_ERROR = "No API key!"

class RepositoryImpl(
    private val openAiService: OpenAiApi,
    private val stabilityAiService: StabilityAiApi,
    private val apiKeyDataSource: ApiKeyDataSource,
    private val firebaseDatabase: FirebaseDatabaseImpl, // todo hide behind interface
) : Repository {
    override suspend fun getReply(conversation: ConversationData): Result<Message> {
        val authHeader = apiKeyDataSource.openAiApiKey?.let { AUTH_HEADER_PREFIX + it }

        val response = try {
            openAiService.getCompletions(
                authHeader = authHeader ?: return Result.failure(IllegalArgumentException(NO_API_KEY_ERROR)),
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

    override suspend fun getImage(
        prompt: String,
        useStabilityAi: Boolean,
    ): Result<GeneratedImage> {
        return if (useStabilityAi) {
            getImageWithStabilityAi(prompt)
        } else {
            getImageWithOpenAi(prompt)
        }
    }

    private suspend fun getImageWithOpenAi(prompt: String): Result<GeneratedImage> {
        val authHeader = apiKeyDataSource.openAiApiKey?.let { AUTH_HEADER_PREFIX + it }

        val response = try {
            openAiService.getImageGeneration(
                authHeader = authHeader ?: return Result.failure(IllegalArgumentException(NO_API_KEY_ERROR)),
                request = OpenAiImageRequest(prompt),
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (response.isSuccessful) {
            val answer = response.body()?.url ?: return Result.failure(IllegalArgumentException("No body: ${response.body()}"))

            Result.success(GeneratedImage(url = answer))
        } else {
            Result.failure(IllegalArgumentException(response.errorBody()?.string()))
        }
    }

    private suspend fun getImageWithStabilityAi(prompt: String): Result<GeneratedImage> {
        val authHeader = apiKeyDataSource.stabilityAiApiKey?.let { AUTH_HEADER_PREFIX + it }

        val response = try {
            stabilityAiService.getImageGeneration(
                authHeader = authHeader ?: return Result.failure(IllegalArgumentException(NO_API_KEY_ERROR)),
                request = StabilityAiRequest(prompt),
            )
        } catch (e: Exception) {
            return Result.failure(e)
        }

        return if (response.isSuccessful) {
            val image = response.body()?.artifacts?.firstOrNull()
                ?: return Result.failure(IllegalArgumentException("No body: ${response.body()}"))

            if (image.finishReason != StabilityAiFinishReason.SUCCESS) {
                return Result.failure(IllegalStateException("Couldn't generate image: ${image.finishReason}"))
            }

            Result.success(GeneratedImage(base64 = image.base64))
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
