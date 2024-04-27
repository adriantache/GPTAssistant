package com.adriantache.gptassistant.data.model

import kotlinx.serialization.Serializable

@Serializable
class StabilityAiRequest {
    @Suppress("PrivatePropertyName")
    private val text_prompts: List<Prompt>

    @Suppress("unused")
    private val width: Int = 512

    @Suppress("unused")
    private val height: Int = 896

    constructor(prompt: String) {
        this.text_prompts = listOf(Prompt(prompt))
    }
}

@Serializable
private data class Prompt(
    val text: String,
)
