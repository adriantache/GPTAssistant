package com.adriantache.gptassistant.data.dataSource

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource

private const val PREFERENCES_FILE = "preferences"
private const val API_KEY_TAG = "API_KEY_TAG"
private const val STABILITY_API_KEY_TAG = "STABILITY_API_KEY_TAG"

class ApiKeyDataSourceImpl(
    context: Context,
) : ApiKeyDataSource {
    private val preferences: SharedPreferences = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE)

    override var openAiApiKey: String? = null
        get() {
            return field ?: preferences.getString(API_KEY_TAG, null)
        }
        set(value) {
            preferences.edit { putString(API_KEY_TAG, value) }
            field = value
        }

    override var stabilityAiApiKey: String? = null
        get() {
            return field ?: preferences.getString(STABILITY_API_KEY_TAG, null)
        }
        set(value) {
            preferences.edit { putString(STABILITY_API_KEY_TAG, value) }
            field = value
        }
}
