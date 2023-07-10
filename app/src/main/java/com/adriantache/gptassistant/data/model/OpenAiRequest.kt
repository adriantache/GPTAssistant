package com.adriantache.gptassistant.data.model

import com.squareup.moshi.JsonClass

@Suppress("unused")
private const val MODEL_GPT_3 = "gpt-3.5-turbo"
private const val MODEL_GPT_4 = "gpt-4"

@JsonClass(generateAdapter = true)
data class OpenAiRequest(
    val messages: List<ChatMessage>,
    val model: String = MODEL_GPT_4,
)
