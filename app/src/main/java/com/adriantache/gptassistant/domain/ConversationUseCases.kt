package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.data.RepositoryImpl
import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData.Companion.toData
import com.adriantache.gptassistant.domain.model.ui.ConversationUi
import com.adriantache.gptassistant.domain.model.ui.ConversationUi.Companion.toUi
import com.adriantache.gptassistant.domain.util.Event
import com.adriantache.gptassistant.domain.util.Event.Companion.asEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private val titleQuery = Message.UserMessage("Suggest a title for this conversation.")

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
            repository.getReply(conversation.toData())
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

    fun onResetConversation(saveConversation: Boolean) {
        updateState(isLoading = true)

        scope.launch {
            if (saveConversation) {
                val title = if (conversation.title != null) {
                    conversation.title
                } else {
                    repository.getReply(
                        conversation.copy(messages = conversation.messages + titleQuery).toData()
                    ).getOrNull()?.content
                        ?: UUID.randomUUID().toString()
                }

                repository.saveConversation(conversation.toData(title))
            }

            conversation = Conversation()
            updateState()
        }
    }

    fun onLoadConversation(conversation: Conversation) {
        this.conversation = conversation
        updateState()
    }

    suspend fun getConversations(): List<Conversation> {
        return repository.getConversations().map { it.toConversation() }
    }

    private fun updateState(isLoading: Boolean = false) {
        state.value = conversation.toUi(isLoading = isLoading)
    }
}
