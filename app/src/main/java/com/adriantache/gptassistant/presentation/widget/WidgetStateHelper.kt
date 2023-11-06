package com.adriantache.gptassistant.presentation.widget

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.adriantache.gptassistant.domain.model.ui.ConversationsUi
import kotlinx.serialization.json.Json

object WidgetStateHelper {
    fun getState(prefs: Preferences): ConversationsUi? {
        val conversationJson = prefs[stringPreferencesKey(conversationKey)]

        return conversationJson?.let { Json.decodeFromString<ConversationsUi>(it) }
    }

    private const val conversationKey = "conversationKey"
}
