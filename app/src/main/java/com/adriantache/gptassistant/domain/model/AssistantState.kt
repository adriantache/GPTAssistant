package com.adriantache.gptassistant.domain.model

sealed interface AssistantState {
    data class Init(val onInit: () -> Unit) : AssistantState
    data class ApiKeyInput(val onSubmit: (String) -> Unit) : AssistantState

    data object Conversation : AssistantState
}
