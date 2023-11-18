package com.adriantache.gptassistant.domain.data

interface SettingsDataSource {
    fun setConversationMode(active: Boolean)

    fun getConversationMode(): Boolean
}
