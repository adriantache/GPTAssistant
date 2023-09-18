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

    private suspend fun getDatabaseContents(): List<ConversationData> {
        val json = myRef.get().await().getValue<String>() ?: return emptyList()

        return Json.decodeFromString<List<ConversationData>>(json)
    }

    suspend fun saveConversation(conversation: ConversationData) {
        val currentDatabase = getDatabaseContents()
        val updatedContents = (currentDatabase + conversation).distinct()

        if (currentDatabase == updatedContents) return

        val json = Json.encodeToString(updatedContents)

        myRef.setValue(json)
    }

    suspend fun getConversations(): List<ConversationData> {
        return getDatabaseContents()
    }
}
