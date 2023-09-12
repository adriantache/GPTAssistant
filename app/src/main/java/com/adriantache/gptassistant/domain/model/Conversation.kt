package com.adriantache.gptassistant.domain.model

data class Conversation(
    val messages: List<Message> = emptyList(),
    val latestInput: String = "",
    val errorMessage: String? = null,
) {
    val areMessagesVisible = messages.isNotEmpty()
    val canResetConversation = messages.isNotEmpty()
    val canSubmit = latestInput.isNotBlank()

    fun onInput(input: String): Conversation {
        if (input.isBlank()) {
            return this.copy(errorMessage = "Please input something.")
        }

        return Conversation(
            messages = messages,
            latestInput = input,
        )
    }

    fun onSubmit(): Conversation {
        if (!canSubmit) return this

        val userInput = Message.UserMessage(latestInput)

        return Conversation(
            messages = messages + userInput,
            latestInput = "",
        )
    }

    fun onReply(reply: Message): Conversation {
        return Conversation(messages + reply)
    }
}
