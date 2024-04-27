package com.adriantache.gptassistant.domain.model.ui

data class SettingsUi(
    val areConversationsSaved: Boolean?,
    val setAreConversationsSaved: (Boolean) -> Unit,
    val isConversationMode: Boolean,
    val setConversationMode: (Boolean) -> Unit,
    val isStabilityAi: Boolean,
    val setUseStabilityAi: (Boolean) -> Unit,
    val onInputStabilityApiKey: (String) -> Unit,
    val hasStabilityAiApiKey: Boolean,
)
