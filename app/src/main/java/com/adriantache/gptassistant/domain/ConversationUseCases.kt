package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.data.RepositoryImpl
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.domain.model.ui.ConversationUi
import com.adriantache.gptassistant.domain.model.ui.ConversationUi.Companion.toUi
import com.adriantache.gptassistant.domain.util.Event
import com.adriantache.gptassistant.domain.util.Event.Companion.asEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

// TODO: implement conversation saving
@Suppress("kotlin:S6305")
class ConversationUseCases(
    private val repository: Repository = RepositoryImpl(),
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    @set:Synchronized
    @get:Synchronized
    private var conversation = Conversation()

    val state: MutableStateFlow<ConversationUi> = MutableStateFlow(conversation.toUi(false))
    val events: MutableStateFlow<Event<ConversationEvent>?> = MutableStateFlow(null)

    fun onInput(input: String) {
        conversation = conversation.onInput(input)
        updateState()
    }

    fun onSubmit(fromSpeech: Boolean) {
        updateState(isLoading = true)
        conversation = conversation.onSubmit()

        scope.launch {
            repository.getReply(conversation)
                .onSuccess { reply ->
                    conversation = conversation.onReply(reply)
                    updateState()

                    if (fromSpeech) {
                        events.value = ConversationEvent.SpeakReply(reply.content).asEvent()
                    }
                }
                .onFailure {
                    updateState()
                    events.value = ConversationEvent.Error(it.message).asEvent()
                }
        }
    }

    fun onResetConversation() {
        conversation = Conversation()
        updateState()
    }

    private fun updateState(isLoading: Boolean = false) {
        state.value = conversation.toUi(isLoading = isLoading)
    }
}
