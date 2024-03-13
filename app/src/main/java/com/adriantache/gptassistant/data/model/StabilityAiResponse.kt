package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StabilityAiResponse(
    val artifacts: List<StabilityAiImage>,
)

@Serializable
data class StabilityAiImage(
    val base64: String,
    val finishReason: StabilityAiFinishReason,
    val seed: Long,
)

@Suppress("unused")
enum class StabilityAiFinishReason {
    CONTENT_FILTERED, ERROR, SUCCESS
}
