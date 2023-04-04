package com.adriantache.gptassistant.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Choice(
    val message: ChatMessage,
    val text: String?,
    val index: Int?,
    val logprobs: List<Double>?
)
