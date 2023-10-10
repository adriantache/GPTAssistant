package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.SettingsDataSource
import com.adriantache.gptassistant.domain.model.ui.SettingsUi
import kotlinx.coroutines.flow.MutableStateFlow

class SettingsUseCases(
    private val storage: SettingsDataSource,
) {
    val settings = MutableStateFlow(getSettings())

    private fun getSettings(): SettingsUi {
        return SettingsUi(
            isInputOnBottom = storage.getInputOnBottom(),
            setInputOnBottom = ::setInputOnBottom,
        )
    }

    private fun setInputOnBottom(isOnBottom: Boolean) {
        storage.setInputOnBottom(isOnBottom)

        settings.value = getSettings()
    }
}
