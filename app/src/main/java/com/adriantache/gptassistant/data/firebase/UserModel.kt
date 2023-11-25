package com.adriantache.gptassistant.data.firebase

import com.adriantache.gptassistant.domain.model.data.ConversationData
import kotlinx.serialization.Serializable

@Serializable
data class UserModel(
    val conversations: List<ConversationData> = emptyList(),
    val categories: List<Category> = emptyList(),
)

@Serializable
data class Category(
    val name: String,
    val conversationIds: List<String>,
)
