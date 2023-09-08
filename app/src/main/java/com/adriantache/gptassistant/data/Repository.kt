package com.adriantache.gptassistant.data

import com.adriantache.gptassistant.data.model.ChatMessage
import com.adriantache.gptassistant.data.model.ChatRole
import com.adriantache.gptassistant.data.model.OpenAiRequest
import com.adriantache.gptassistant.data.retrofit.OpenAiApi
import com.adriantache.gptassistant.data.retrofit.getRetrofit
import java.net.SocketTimeoutException

// TODO: add functionality: save conversation
class Repository {
    private val service = getRetrofit().create(OpenAiApi::class.java)

    private var conversation = emptyList<ChatMessage>()

    suspend fun getReply(input: String): String {
        conversation += ChatMessage(input)

        val response = try {
            service.getCompletions(OpenAiRequest(messages = conversation))
        } catch (e: Exception) {
            if (e is SocketTimeoutException) return "Timeout."

            return e.toString()
        }

        return if (response.isSuccessful) {
            val answer = response.body()?.choices?.lastOrNull()?.message?.content.orEmpty()
            conversation += ChatMessage(answer, ChatRole.assistant)
            answer
        } else {
            "ERROR ${response.errorBody()?.string()}"
        }
    }

    fun resetConversation() {
        conversation = emptyList()
    }
}
