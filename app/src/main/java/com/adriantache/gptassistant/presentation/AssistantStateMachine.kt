package com.adriantache.gptassistant.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.adriantache.gptassistant.domain.AssistantUseCases
import com.adriantache.gptassistant.domain.model.AssistantState
import com.adriantache.gptassistant.presentation.view.ApiKeyInputDialog
import org.koin.androidx.compose.get

@Composable
fun AssistantStateMachine(
    stateUseCases: AssistantUseCases = get(),
) {
    val state by stateUseCases.states.collectAsStateWithLifecycle(minActiveState = RESUMED)

    LaunchedEffect(state) {
        when (val localState = state) {
            is AssistantState.ApiKeyInput -> Unit
            AssistantState.Conversation -> Unit
            is AssistantState.Init -> localState.onInit()
        }
    }

    AnimatedContent(targetState = state, label = "contentAnimation") {
        when (val localState = it) {
            is AssistantState.Init -> Unit
            is AssistantState.ApiKeyInput -> ApiKeyInputDialog(onSubmit = localState.onSubmit)
            AssistantState.Conversation -> Conversation()
        }
    }
}
