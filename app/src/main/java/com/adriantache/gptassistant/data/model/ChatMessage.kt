package com.adriantache.gptassistant.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatMessage(
    val content: String,
    val role: ChatRole = ChatRole.user,
)

@Suppress("EnumEntryName", "Unused")
enum class ChatRole {
    system, assistant, user
}
