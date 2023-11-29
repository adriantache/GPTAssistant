package com.adriantache.gptassistant.domain.model

import java.util.UUID

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val startedAt: Long = System.currentTimeMillis(),
    val title: String? = null,
    val messages: List<Message> = emptyList(),
    val latestInput: String = "",
    val errorMessage: String? = null,
) {
    val canResetConversation = messages.isNotEmpty()
    val canSubmit = latestInput.isNotBlank()

    fun onInput(input: String): Conversation {
        return this.copy(
            messages = messages,
            latestInput = input,
        )
    }

    fun onSubmit(): Conversation {
        if (!canSubmit) return this

        val userInput = Message.UserMessage(latestInput)

        return this.copy(
            messages = messages + userInput + Message.Loading(),
            latestInput = "",
        )
    }

    fun onReply(reply: Message): Conversation {
        return this.copy(messages = messages.filterNot { it is Message.Loading } + reply)
    }

    fun editLastUserMessage(message: Message): Conversation {
        val messageIndex = messages.indexOf(message).takeIf { it != -1 } ?: return this
        val newMessages = messages.subList(0, messageIndex)

        return this.copy(
            messages = newMessages,
            latestInput = message.content,
        )
    }

    fun duplicate(): Conversation {
        return this.copy(id = UUID.randomUUID().toString(), title = null)
    }
}
