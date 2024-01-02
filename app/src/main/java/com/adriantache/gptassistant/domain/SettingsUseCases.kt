package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.SettingsDataSource
import com.adriantache.gptassistant.domain.model.ui.SettingsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: add setting for hiding keyboard after input
class SettingsUseCases(
    private val storage: SettingsDataSource,
) {
    private val _settings = MutableStateFlow(getSettings())
    val settings: StateFlow<SettingsUi> = _settings

    private fun getSettings(): SettingsUi {
        return SettingsUi(
            areConversationsSaved = storage.getAreConversationsSaved(),
            setAreConversationsSaved = ::setAreConversationsSaved,
            isConversationMode = storage.getConversationMode(),
            setConversationMode = ::setConversationMode,
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

    private fun refreshSettings() {
        _settings.value = getSettings()
    }
}
