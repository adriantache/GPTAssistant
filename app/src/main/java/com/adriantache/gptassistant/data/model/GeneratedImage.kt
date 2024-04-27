package com.adriantache.gptassistant.data.model

data class GeneratedImage(
    val url: String? = null,
    val base64: String? = null,
) {
    val isUrl = url != null
}
