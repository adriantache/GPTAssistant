package com.adriantache.gptassistant.data.firebase

import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

private const val DATABASE_URL = "https://chatgpt-44830-default-rtdb.europe-west1.firebasedatabase.app/"
private const val ID_KEY = "ID_KEY"

// TODO: implement this properly
class FirebaseDatabaseImpl(
    preferences: SharedPreferences,
) {
    private val database = Firebase.database(DATABASE_URL)
    private val myRef: DatabaseReference

    init {
        var id = preferences.getString(ID_KEY, null)

        if (id == null) {
            id = UUID.randomUUID().toString()

            preferences.edit { putString(ID_KEY, id) }
        }

        myRef = database.getReference("message-$id")
    }

    suspend fun saveConversation(conversation: ConversationData) {
        val currentDatabase = getDatabaseContents()
        val updatedContents = getDatabaseContents().map {
            if (it.id != conversation.id) return@map it

            conversation
        }.toMutableList()

        if (!updatedContents.contains(conversation)) {
            updatedContents += conversation
        }

        if (currentDatabase == updatedContents) return

        updateDatabase(updatedContents)
    }

    suspend fun getConversations(): List<ConversationData> {
        return getDatabaseContents()
    }

    suspend fun deleteConversation(conversationId: String) {
        val conversations = getDatabaseContents()
        val updatedState = conversations.filterNot { it.id == conversationId || it.id == null }

        updateDatabase(updatedState)
    }

    private suspend fun getDatabaseContents(): List<ConversationData> {
        val json = myRef.get().await().getValue<String>() ?: return emptyList()

        return Json.decodeFromString<List<ConversationData>>(json)
    }

    private fun updateDatabase(updatedContents: List<ConversationData>) {
        val json = Json.encodeToString(updatedContents)

        myRef.setValue(json)
    }
}
