package com.adriantache.gptassistant.data.dataSource

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.data.SettingsDataSource

private const val PREFERENCES_FILE = "preferences"
private const val CONVERSATION_MODE = "CONVERSATION_MODE"
private const val ARE_CONVERSATIONS_SAVED = "ARE_CONVERSATIONS_SAVED"
private const val USE_STABILITY_AI = "USE_STABILITY_AI"

class SettingsDataSourceImpl(
    context: Context,
) : SettingsDataSource {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)

    init {
        deleteOldKeys()
    }

    override fun setAreConversationsSaved(active: Boolean) {
        preferences.edit {
            putBoolean(ARE_CONVERSATIONS_SAVED, active)
        }
    }

    override fun getAreConversationsSaved(): Boolean? {
        if (!preferences.contains(ARE_CONVERSATIONS_SAVED)) return null

        return preferences.getBoolean(ARE_CONVERSATIONS_SAVED, false)
    }

    override fun setUseStabilityAi(active: Boolean) {
        preferences.edit {
            putBoolean(USE_STABILITY_AI, active)
        }
    }

    override fun getUseStabilityAi(): Boolean {
        if (!preferences.contains(USE_STABILITY_AI)) return false

        return preferences.getBoolean(USE_STABILITY_AI, false)
    }

    override fun setConversationMode(active: Boolean) {
        preferences.edit {
            putBoolean(CONVERSATION_MODE, active)
        }
    }

    override fun getConversationMode(): Boolean {
        return preferences.getBoolean(CONVERSATION_MODE, false)
    }

    private fun deleteOldKeys() {
        preferences.edit {
            remove("INPUT_ON_BOTTOM")
        }
    }
}
