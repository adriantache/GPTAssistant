package com.adriantache.gptassistant.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.adriantache.gptassistant.domain.AssistantUseCases
import com.adriantache.gptassistant.domain.model.AssistantState
import com.adriantache.gptassistant.presentation.view.OpenAiApiKeyInputDialog
import com.adriantache.gptassistant.presentation.view.SaveHistoryDialog
import org.koin.androidx.compose.get


@Composable
fun AssistantStateMachine(
    stateUseCases: AssistantUseCases = get(),
) {
    val state by stateUseCases.states.collectAsState()

    LaunchedEffect(state) {
        when (val localState = state) {
            is AssistantState.ApiKeyInput -> Unit
            AssistantState.Conversation -> Unit
            is AssistantState.Init -> localState.onInit()
            else -> Unit
        }
    }

    when (val localState = state) {
        is AssistantState.Init -> Unit
        is AssistantState.ApiKeyInput -> OpenAiApiKeyInputDialog(onSubmit = localState.onSubmit)
        is AssistantState.SaveConversationHistory -> SaveHistoryDialog(
            onSaveGoogle = localState.onSaveGoogle,
            onRejectSaving = localState.onRejectSaving,
        )

        AssistantState.Conversation -> Conversation()
        else -> Unit
    }
}
