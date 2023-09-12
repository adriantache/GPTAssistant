package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.model.ChatMessage.Companion.toChatMessages
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.Message

// TODO: add functionality: save conversation
class RepositoryImpl(
    private val service: OpenAiApi = getRetrofit().create(OpenAiApi::class.java),
) : Repository {
    override suspend fun getReply(conversation: Conversation): Result<Message> {
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
}
