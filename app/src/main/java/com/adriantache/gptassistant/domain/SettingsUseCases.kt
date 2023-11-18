package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.SettingsDataSource
import com.adriantache.gptassistant.domain.model.ui.SettingsUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsUseCases(
    private val storage: SettingsDataSource,
) {
    private val _settings = MutableStateFlow(getSettings())
    val settings: StateFlow<SettingsUi> = _settings

    private fun getSettings(): SettingsUi {
        return SettingsUi(
            isConversationMode = storage.getConversationMode(),
            setConversationMode = ::setConversationMode,
        )
    }

    private fun setConversationMode(active: Boolean) {
        storage.setConversationMode(active)

        _settings.value = getSettings()
    }
}
