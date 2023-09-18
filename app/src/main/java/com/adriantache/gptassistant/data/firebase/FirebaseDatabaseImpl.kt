package com.adriantache.gptassistant.data.firebase

import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val DATABASE_URL = "https://chatgpt-44830-default-rtdb.europe-west1.firebasedatabase.app/"

// TODO: implement UI to view existing conversations and restore them
class FirebaseDatabaseImpl {
    private val database = Firebase.database(DATABASE_URL)
    private val myRef = database.getReference("message")

    private suspend fun getDatabaseContents(): List<ConversationData> {
        val json = myRef.get().await().getValue<String>() ?: return emptyList()

        return Json.decodeFromString<List<ConversationData>>(json)
    }

    suspend fun saveConversation(conversation: ConversationData) {
        val currentDatabase = getDatabaseContents()
        val updatedContents = currentDatabase + conversation

        val json = Json.encodeToString(updatedContents.distinct())

        myRef.setValue(json)
    }

    suspend fun getConversations(): List<ConversationData> {
        return getDatabaseContents()
    }
}
