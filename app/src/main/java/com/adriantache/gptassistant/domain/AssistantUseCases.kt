package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.data.firebase.FirebaseDatabaseImpl
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.model.AssistantState
import com.adriantache.gptassistant.domain.model.AssistantState.ApiKeyInput
import com.adriantache.gptassistant.domain.model.AssistantState.Conversation
import com.adriantache.gptassistant.domain.model.AssistantState.Init
import com.adriantache.gptassistant.domain.model.AssistantState.SaveConversationHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: migrate all main functionality here
// TODO: move this class behind an interface
// TODO: clean up sharedprefs usage in the entire project
class AssistantUseCases(
    private val apiKeyDataSource: ApiKeyDataSource,
    private val settingsUseCases: SettingsUseCases,
    private val firebaseDatabaseImpl: FirebaseDatabaseImpl,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    private val _states: MutableStateFlow<AssistantState> = MutableStateFlow(Init(::onInit))
    val states: StateFlow<AssistantState> = _states

    private fun onInit() {
        val noApiKey = apiKeyDataSource.openAiApiKey.isNullOrBlank()
        val shouldShowConversationSaveOptions = settingsUseCases.settings.value.areConversationsSaved == null ||
                (settingsUseCases.settings.value.areConversationsSaved == true && !firebaseDatabaseImpl.hasId())

        when {
            noApiKey -> _states.value = ApiKeyInput(::onApiKeyInput)

            shouldShowConversationSaveOptions -> _states.value = SaveConversationHistory(
                onSaveGoogle = ::onSaveGoogle,
                onRejectSaving = ::onRejectSaving,
            )

            else -> _states.value = Conversation
        }
    }

    private fun onSaveGoogle() {
        settingsUseCases.setAreConversationsSaved(true)

        onInit()
    }

    private fun onRejectSaving() {
        settingsUseCases.setAreConversationsSaved(false)

        onInit()
    }

    // TODO: also save api key to storage
    private fun onApiKeyInput(key: String) {
        if (key.isBlank()) return

        apiKeyDataSource.openAiApiKey = key
        onInit()
    }
}
