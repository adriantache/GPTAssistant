package com.adriantache.gptassistant.domain.data

import com.adriantache.gptassistant.domain.model.Conversation
import com.adriantache.gptassistant.domain.model.Message

fun interface Repository {
    suspend fun getReply(conversation: Conversation): Result<Message>
}
