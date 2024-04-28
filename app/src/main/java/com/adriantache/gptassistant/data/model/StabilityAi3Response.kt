package com.adriantache.gptassistant.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StabilityAi3Response(
    val image: String,
    @SerialName("finish-reason")
    val finishReason: StabilityAiFinishReason?,
    val seed: Long?,
)
