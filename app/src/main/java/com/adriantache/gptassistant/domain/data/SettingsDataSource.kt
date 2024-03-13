package com.adriantache.gptassistant.domain.data

interface SettingsDataSource {
    fun setConversationMode(active: Boolean)

    fun getConversationMode(): Boolean
    fun setAreConversationsSaved(active: Boolean)
    fun getAreConversationsSaved(): Boolean?

    fun setUseStabilityAi(active: Boolean)
    fun getUseStabilityAi(): Boolean
}
