package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

@Suppress("unused")
private const val MODEL_GPT_3 = "gpt-3.5-turbo-0613"
private const val MODEL_GPT_4 = "gpt-4-0613"

@Serializable
data class OpenAiRequest(
    val messages: List<ChatMessage>,
    val model: String = MODEL_GPT_4,
)
