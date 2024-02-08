@file:Suppress("unused")

package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

// Models without version always point to latest version.
private const val MODEL_GPT_3 = "gpt-3.5-turbo"
private const val MODEL_GPT_4 = "gpt-4"
private const val MODEL_GPT_4_TURBO = "gpt-4-turbo-preview"

@Serializable
data class OpenAiRequest(
    val messages: List<ChatMessage>,
    val model: String = MODEL_GPT_4_TURBO,
)
