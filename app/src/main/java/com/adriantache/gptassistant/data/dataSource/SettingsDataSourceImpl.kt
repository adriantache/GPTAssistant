package com.adriantache.gptassistant.data.dataSource

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.data.SettingsDataSource

private const val PREFERENCES_FILE = "preferences"
private const val INPUT_ON_BOTTOM = "INPUT_ON_BOTTOM"
private const val CONVERSATION_MODE = "CONVERSATION_MODE"

class SettingsDataSourceImpl(
    context: Context,
) : SettingsDataSource {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE)

    override fun setInputOnBottom(isOnBottom: Boolean) {
        preferences.edit {
            putBoolean(INPUT_ON_BOTTOM, isOnBottom)
        }
    }

    override fun getInputOnBottom(): Boolean {
        return preferences.getBoolean(INPUT_ON_BOTTOM, false)
    }

    override fun setConversationMode(active: Boolean) {
        preferences.edit {
            putBoolean(CONVERSATION_MODE, active)
        }
    }

    override fun getConversationMode(): Boolean {
        return preferences.getBoolean(CONVERSATION_MODE, false)
    }
}
