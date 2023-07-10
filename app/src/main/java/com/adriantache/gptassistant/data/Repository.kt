package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.model.ChatMessage
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.model.OpenAiResponse
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import java.net.SocketTimeoutException

class Repository {
    private val service = getRetrofit().create(OpenAiApi::class.java)

    private var responses = emptyList<OpenAiResponse>()

    suspend fun getReply(input: String): String {
        val response = try {
            service.getCompletions(
                OpenAiRequest(messages = getResponses() + ChatMessage(input))
            )
        } catch (e: Exception) {
            if (e is SocketTimeoutException) return "Timeout."

            return e.toString()
        }

        return if (response.isSuccessful) {
            response.body()?.choices?.lastOrNull()?.message?.content.orEmpty()
        } else {
            "ERROR ${response.errorBody()?.string()}"
        }
    }

    private fun getResponses(): List<ChatMessage> {
        return responses.flatMap { response ->
            response.choices.map { it.message }
        }
    }
}
