package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.model.AssistantState
import com.adriantache.gptassistant.domain.model.AssistantState.ApiKeyInput
import com.adriantache.gptassistant.domain.model.AssistantState.Conversation
import com.adriantache.gptassistant.domain.model.AssistantState.Init
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: migrate all main functionality here
// TODO: move this class behind an interface
class AssistantUseCases(
    private val apiKeyDataSource: ApiKeyDataSource,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    private val _states: MutableStateFlow<AssistantState> = MutableStateFlow(Init(::onInit))
    val states: StateFlow<AssistantState> = _states

    private fun onInit() {
        val noApiKey = apiKeyDataSource.apiKey.isNullOrBlank()

        if (noApiKey) {
            _states.value = ApiKeyInput(::onApiKeyInput)
        } else {
            _states.value = Conversation
        }
    }

    private fun onApiKeyInput(key: String) {
        if (key.isBlank()) return

        apiKeyDataSource.apiKey = key
        onInit()
    }
}
