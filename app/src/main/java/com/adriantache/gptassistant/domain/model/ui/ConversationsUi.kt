package com.adriantache.gptassistant.domain.model.ui

data class ConversationsUi(
    val conversations: List<ConversationUi>,
    val onDeleteConversation: suspend (String) -> Unit,
)
