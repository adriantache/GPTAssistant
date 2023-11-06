package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

private const val MODEL_GPT_3 = "gpt-3.5-turbo-1106"
private const val MODEL_GPT_4 = "gpt-4-0613"
private const val MODEL_GPT_4_TURBO = "gpt-4-1106-preview"

@Serializable
data class OpenAiRequest(
    val messages: List<ChatMessage>,
    val model: String = MODEL_GPT_4_TURBO,
)
