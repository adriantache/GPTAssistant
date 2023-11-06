package com.adriantache.gptassistant.domain

import com.adriantache.gptassistant.domain.data.Repository
import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.ConversationEvent
import com.adriantache.gptassistant.domain.model.Message
import com.adriantache.gptassistant.domain.model.data.ConversationData.Companion.toData
import com.adriantache.gptassistant.domain.model.ui.ConversationUi
import com.adriantache.gptassistant.domain.model.ui.ConversationUi.Companion.toUi
import com.adriantache.gptassistant.domain.model.ui.ConversationsUi
import com.adriantache.gptassistant.domain.util.Event
import com.adriantache.gptassistant.domain.util.Event.Companion.asEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

private const val ONE_DAY_MS = 1000 * 60 * 60 * 24
private const val THREE_MINUTES_MS = 3 * 60 * 1000
private val titleQuery = Message.UserMessage("Suggest a title for this conversation.")

class ConversationUseCases(
    private val repository: Repository,
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
) {
    private val scope = CoroutineScope(dispatcher)

    @set:Synchronized
    @get:Synchronized
    private var conversation = Conversation()

    private var lastRequestTime = 0L

    private val _state: MutableStateFlow<ConversationUi> = MutableStateFlow(conversation.toUi(false))
    val state: StateFlow<ConversationUi> = _state
    private val _events: MutableStateFlow<Event<ConversationEvent>?> = MutableStateFlow(null)
    val events: StateFlow<Event<ConversationEvent>?> = _events

    fun onInput(
        input: String,
        fromWidget: Boolean = false,
    ) {
        // Automatically reset the conversation if we haven't interacted with the Widget in a while.
        val isWidgetExpired = System.currentTimeMillis() - lastRequestTime > THREE_MINUTES_MS
        if (fromWidget && isWidgetExpired) {
            conversation = Conversation()
        }

        conversation = conversation.onInput(input)
        updateState()
    }

    fun onSubmit(fromSpeech: Boolean) {
        // TODO: remove this hack
        if (System.currentTimeMillis() - lastRequestTime < 500) {
            return
        }
        lastRequestTime = System.currentTimeMillis()

        conversation = conversation.onSubmit()
        updateState(isLoading = true)

        scope.launch {
            repository.getReply(conversation.toData())
                .onSuccess { reply ->
                    conversation = conversation.onReply(reply)
                    saveConversation()
                    updateState()

                    if (fromSpeech) {
                        _events.value = ConversationEvent.SpeakReply(reply.content).asEvent()
                    }
                }
                .onFailure {
                    updateState()
                    _events.value = ConversationEvent.Error(it.message).asEvent()
                }
        }
    }

    fun onResetConversation() {
        updateState(isLoading = true)

        conversation = Conversation()
        updateState()
    }

    private suspend fun saveConversation() {
        if (conversation.title == null) {
            val title = repository.getReply(
                conversation.copy(messages = conversation.messages + titleQuery).toData()
            ).getOrNull()?.content
                ?: UUID.randomUUID().toString()

            conversation = conversation.copy(title = title)
        }

        repository.saveConversation(conversation.toData())
    }

    fun onLoadConversation(conversation: ConversationUi) {
        this.conversation = conversation.toEntity()
        updateState()
    }

    suspend fun getConversations(
        dateRangeStart: Long?,
        dateRangeEnd: Long?,
        searchText: String?,
    ): ConversationsUi {
        val conversations = repository.getConversations().map { it.toConversation() }

        return ConversationsUi(
            conversations = conversations.filter {
                val isAfterStartDate = dateRangeStart == null || it.startedAt >= dateRangeStart
                val isBeforeEndDate = dateRangeEnd == null || it.startedAt <= (dateRangeEnd + ONE_DAY_MS)
                val containsSearch =
                    searchText == null || searchText.length < 2 || it.messages.any { message -> message.content.contains(searchText) }

                isAfterStartDate && isBeforeEndDate && containsSearch
            }.map { it.toUi(false) },
            onDeleteConversation = ::deleteConversation,
        )
    }

    private suspend fun deleteConversation(conversationId: String) {
        repository.deleteConversation(conversationId)
    }

    private fun updateState(isLoading: Boolean = false) {
        _state.value = conversation.toUi(isLoading = isLoading)
    }
}
