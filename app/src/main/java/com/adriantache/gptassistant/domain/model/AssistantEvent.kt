package com.adriantache.gptassistant.domain.model

sealed interface AssistantEvent {
    data class Error(val replaceThis: Boolean) : AssistantEvent
    data class UpdateWidget(val replaceThis: Boolean) : AssistantEvent
    data class SpeakResponse(val replaceThis: Boolean) : AssistantEvent
}
