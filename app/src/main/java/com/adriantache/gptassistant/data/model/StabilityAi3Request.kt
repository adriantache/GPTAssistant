@file:Suppress("unused")

package com.adriantache.gptassistant.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private const val MODE_TEXT_GEN = "text-to-image"
private const val MODE_IMAGE_GEN = "image-to-image"

private const val MODEL_SD3 = "sd3"
private const val MODEL_SD3_TURBO = "sd3-turbo" // Cheaper.

private const val FORMAT_JPG = "jpeg"
private const val FORMAT_PNG = "png"

@Serializable
data class StabilityAi3Request(
    val prompt: String,
    val aspectRatio: String? = null,
    val mode: String = MODE_TEXT_GEN,
    @SerialName("negative_prompt")
    val negativePrompt: String? = null,
    val model: String = MODEL_SD3,
    val seed: String? = null,
    @SerialName("output_format")
    val outputFormat: String = FORMAT_PNG,
)
