package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OpenAiImageResponse(
    val created: Long,
    val data: List<ImageData>,
) {
    val url = data.first().url
}

@Serializable
data class ImageData(
    val url: String,
)

