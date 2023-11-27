package com.adriantache.gptassistant.data.firebase

import android.content.SharedPreferences
import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private const val DATABASE_URL = "https://chatgpt-44830-default-rtdb.europe-west1.firebasedatabase.app/"

// TODO: implement this properly
class FirebaseDatabaseImpl(
    private val preferences: SharedPreferences,
) {
    private val database = Firebase.database(DATABASE_URL)
    private val myRef: DatabaseReference? = getDatabase()

    // TODO: remove this method once all users are on new version
    private suspend fun migrateConversations() {
        oldIdMigration(
            preferences = preferences,
            database = database,
        )
        databaseStructureMigration(
            preferences = preferences,
            database = database,
        )
    }

    fun hasId(): Boolean {
        return FirebaseAuth.getInstance().uid != null
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
        val json = ref?.get()?.await()?.getValue<String>()?.takeUnless { it.isEmpty() } ?: return emptyList()

        return Json.decodeFromString<UserModel>(json).conversations
    }

    private fun updateDatabase(updatedContents: List<ConversationData>) {
        val userModel = UserModel(conversations = updatedContents)
        val json = Json.encodeToString(userModel)

        myRef?.setValue(json)
    }

    private fun getDatabase(): DatabaseReference? {
        val userId = FirebaseAuth.getInstance().uid ?: return null
        return database.getReference("users/$userId")
    }
}
