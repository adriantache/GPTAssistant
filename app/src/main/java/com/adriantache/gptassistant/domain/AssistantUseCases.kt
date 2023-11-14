package com.adriantache.gptassistant.domain

import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.data.firebase.ID_KEY
import com.adriantache.gptassistant.domain.data.ApiKeyDataSource
import com.adriantache.gptassistant.domain.model.AssistantState
import com.adriantache.gptassistant.domain.model.AssistantState.ApiKeyInput
import com.adriantache.gptassistant.domain.model.AssistantState.Conversation
import com.adriantache.gptassistant.domain.model.AssistantState.Init
import com.adriantache.gptassistant.domain.model.AssistantState.SaveConversationHistory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

// TODO: migrate all main functionality here
// TODO: move this class behind an interface
// TODO: clean up sharedprefs usage in the entire project
class AssistantUseCases(
    private val apiKeyDataSource: ApiKeyDataSource,
    private val sharedPreferences: SharedPreferences,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    private val _states: MutableStateFlow<AssistantState> = MutableStateFlow(Init(::onInit))
    val states: StateFlow<AssistantState> = _states

    private fun onInit() {
        val noApiKey = apiKeyDataSource.apiKey.isNullOrBlank()

        when {
            noApiKey -> _states.value = ApiKeyInput(::onApiKeyInput)
            !sharedPreferences.contains(ID_KEY) -> _states.value = SaveConversationHistory(
                onSaveGoogle = ::onSaveGoogle,
                onSaveLocally = ::onSaveLocally,
                onRejectSaving = ::onRejectSaving,
            )

            else -> _states.value = Conversation
        }
    }

    private fun onSaveGoogle(key: String) {
        sharedPreferences.edit(commit = true) {
            putString(ID_KEY, key)
        }
        onInit()
    }

    private fun onSaveLocally() {
        val key = UUID.randomUUID().toString()
        sharedPreferences.edit {
            putString(ID_KEY, key)
        }
        onInit()
    }

    private fun onRejectSaving() {
        sharedPreferences.edit {
            remove(ID_KEY)
        }
        onInit()
    }

    private fun onApiKeyInput(key: String) {
        if (key.isBlank()) return

        apiKeyDataSource.apiKey = key
        onInit()
    }
}
