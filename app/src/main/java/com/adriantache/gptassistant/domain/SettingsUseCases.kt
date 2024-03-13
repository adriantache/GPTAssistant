package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.data.SettingsDataSource
import com.adriantache.gptassistant.domain.model.ui.SettingsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: add setting for hiding keyboard after input
class SettingsUseCases(
    private val storage: SettingsDataSource,
    private val apiKeyDataSource: ApiKeyDataSource,
) {
    private val _settings = MutableStateFlow(getSettings())
    val settings: StateFlow<SettingsUi> = _settings

    private fun getSettings(): SettingsUi {
        return SettingsUi(
            areConversationsSaved = storage.getAreConversationsSaved(),
            setAreConversationsSaved = ::setAreConversationsSaved,
            isConversationMode = storage.getConversationMode(),
            setConversationMode = ::setConversationMode,
            isStabilityAi = storage.getUseStabilityAi(),
            hasStabilityAiApiKey = apiKeyDataSource.stabilityAiApiKey != null,
            onInputStabilityApiKey = {
                apiKeyDataSource.stabilityAiApiKey = it
                refreshSettings()
            },
            setUseStabilityAi = ::setUseStabilityAi,
        )
    }

    internal fun setAreConversationsSaved(active: Boolean) {
        storage.setAreConversationsSaved(active)

        refreshSettings()
    }

    private fun setConversationMode(active: Boolean) {
        storage.setConversationMode(active)

        refreshSettings()
    }

    private fun setUseStabilityAi(active: Boolean) {
        storage.setUseStabilityAi(active)

        refreshSettings()
    }

    private fun refreshSettings() {
        _settings.value = getSettings()
    }
}
