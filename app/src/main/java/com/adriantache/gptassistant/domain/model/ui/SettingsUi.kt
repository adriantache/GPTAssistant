package com.adriantache.gptassistant.domain.model.ui

data class SettingsUi(
    val isConversationMode: Boolean,
    val setConversationMode: (Boolean) -> Unit,
)
