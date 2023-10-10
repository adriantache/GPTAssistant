package com.adriantache.gptassistant.domain.data

interface SettingsDataSource {
    fun setInputOnBottom(isOnBottom: Boolean)

    fun getInputOnBottom(): Boolean
}
