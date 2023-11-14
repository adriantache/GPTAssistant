package com.adriantache.gptassistant.data.firebase

import android.content.SharedPreferences
import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val DATABASE_URL = "https://chatgpt-44830-default-rtdb.europe-west1.firebasedatabase.app/"

//private const val ID_KEY = "ID_KEY" todo migrate old conversations to new ID and delete this key
const val ID_KEY = "HISTORY_ID_KEY" // TODO: improve this mechanism

// TODO: implement this properly
class FirebaseDatabaseImpl(
    private val preferences: SharedPreferences,
) {
    private val database = Firebase.database(DATABASE_URL)
    private var myRef: DatabaseReference? = getDatabase()
        get() = field ?: getDatabase()

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
        val json = myRef?.get()?.await()?.getValue<String>() ?: return emptyList()

        return Json.decodeFromString<List<ConversationData>>(json)
    }

    private fun updateDatabase(updatedContents: List<ConversationData>) {
        val json = Json.encodeToString(updatedContents)

        myRef?.setValue(json)
    }

    private fun getDatabase(): DatabaseReference? {
        // If id is missing, we assume user chose not to save history.
        val id = preferences.getString(ID_KEY, null)

        return id?.let { database.getReference("message-$it") }
    }
}
