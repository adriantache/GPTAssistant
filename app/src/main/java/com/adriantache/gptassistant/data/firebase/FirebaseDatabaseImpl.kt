package com.adriantache.gptassistant.data.firebase

import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.data.util.getString
import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val DATABASE_URL = "https://chatgpt-44830-default-rtdb.europe-west1.firebasedatabase.app/"

// TODO: remove this key once all users are on new version
private const val OLD_ID_KEY = "ID_KEY"
const val ID_KEY = "HISTORY_ID_KEY" // TODO: improve this mechanism

// TODO: implement this properly
class FirebaseDatabaseImpl(
    private val preferences: SharedPreferences,
) {
    private val database = Firebase.database(DATABASE_URL)
    private var myRef: DatabaseReference? = getDatabase()
        get() = field ?: getDatabase()

    // TODO: remove this method once all users are on new version
    private suspend fun migrateConversations() {
        if (!preferences.contains(OLD_ID_KEY)) return

        val oldDatabase = getDatabase(OLD_ID_KEY)
        val oldContents = getDatabaseContents(oldDatabase)

        val newContents = getDatabaseContents()

        val allConversations = oldContents + newContents

        updateDatabase(allConversations.distinct())

        preferences.edit {
            remove(OLD_ID_KEY)
        }
    }

    suspend fun saveConversation(conversation: ConversationData) {
        migrateConversations()

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
        migrateConversations()

        return getDatabaseContents()
    }

    suspend fun deleteConversation(conversationId: String) {
        val conversations = getDatabaseContents()
        val updatedState = conversations.filterNot { it.id == conversationId || it.id == null }

        updateDatabase(updatedState)
    }

    private suspend fun getDatabaseContents(
        ref: DatabaseReference? = myRef,
    ): List<ConversationData> {
        val json = ref?.get()?.await()?.getValue<String>() ?: return emptyList()

        return Json.decodeFromString<List<ConversationData>>(json)
    }

    private fun updateDatabase(updatedContents: List<ConversationData>) {
        val json = Json.encodeToString(updatedContents)

        myRef?.setValue(json)
    }

    private fun getDatabase(id: String? = preferences.getString(ID_KEY)): DatabaseReference? {
        // If id is missing, we assume user chose not to save history.
        return id?.let { database.getReference("message-$it") }
    }
}
