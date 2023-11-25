package com.adriantache.gptassistant.domain.model

sealed interface AssistantState {
    data class Init(val onInit: () -> Unit) : AssistantState
    data class ApiKeyInput(val onSubmit: (String) -> Unit) : AssistantState

    data class SaveConversationHistory(
        val onSaveGoogle: () -> Unit,
        val onRejectSaving: () -> Unit,
    ) : AssistantState

    data object Conversation : AssistantState

    data class Settings(val replaceThis: Boolean) : AssistantState
    data class History(val replaceThis: Boolean) : AssistantState
}
