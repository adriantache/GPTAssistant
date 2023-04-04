package com.adriantache.gptassistant.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenAiResponse(
    val id: String,
    val choices: List<Choice>,
    val model: String?,
    val engine: String?,
    val prompt: String?,
    val temperature: Double?
)

