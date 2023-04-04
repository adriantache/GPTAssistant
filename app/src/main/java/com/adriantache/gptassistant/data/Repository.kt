package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.model.ChatMessage
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import java.net.SocketTimeoutException

class Repository {
    private val service = getRetrofit().create(OpenAiApi::class.java)

    suspend fun getReply(input: String): String {
        val response = try {
            service.getCompletions(OpenAiRequest(messages = listOf(ChatMessage(input))))
        } catch (e: Exception) {
            if (e is SocketTimeoutException) return "Timeout."

            return e.toString()
        }

        return if (response.isSuccessful) {
            response.body()?.choices?.get(0)?.message?.content.orEmpty()
        } else {
            "ERROR ${response.errorBody()?.string()}"
        }
    }
}
