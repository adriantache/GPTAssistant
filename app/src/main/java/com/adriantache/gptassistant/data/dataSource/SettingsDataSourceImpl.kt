package com.adriantache.gptassistant.data.dataSource

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.data.SettingsDataSource

private const val PREFERENCES_FILE = "preferences"
private const val INPUT_ON_BOTTOM = "INPUT_ON_BOTTOM"

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
}
