package com.adriantache.gptassistant.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenAiRequest(
    val messages: List<ChatMessage>,
    val model: String = "gpt-3.5-turbo",
    val temperature: Double = 0.5,
    val max_tokens: Int = 4096 - messages.getChatLength(),
)

private fun List<ChatMessage>.getChatLength(): Int {
    return this.map { it.content }.getLength()
}

private fun List<String>.getLength(): Int {
    return this.foldRight(0) { message, acc ->
        acc + message.length
    }
}
