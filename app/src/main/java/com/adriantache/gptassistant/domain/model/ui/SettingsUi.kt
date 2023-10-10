package com.adriantache.gptassistant.domain.model.ui

data class SettingsUi(
    val isInputOnBottom: Boolean,
    val setInputOnBottom: (Boolean) -> Unit,
)
