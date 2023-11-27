package com.adriantache.gptassistant.data.firebase

import android.content.SharedPreferences
import androidx.core.content.edit
import com.adriantache.gptassistant.domain.model.data.ConversationData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO: remove this key once all users are on new version
private const val OLD_ID_KEY = "ID_KEY"

// TODO: remove this key once all users are on new version
private const val ID_KEY = "HISTORY_ID_KEY"

suspend fun oldIdMigration(
    preferences: SharedPreferences,
    database: FirebaseDatabase,
) {
    if (!preferences.contains(OLD_ID_KEY)) return

    val oldKey = preferences.getString(OLD_ID_KEY, null)
    val oldContents = getDatabaseContents(getDatabase(oldKey, database))

    val newKey = preferences.getString(ID_KEY, null)
    val newDatabaseRef = getDatabase(newKey, database)
    val newContents = getDatabaseContents(newDatabaseRef)

    val allConversations = oldContents + newContents

    updateDatabase(allConversations.distinct(), newDatabaseRef)

    preferences.edit {
        remove(OLD_ID_KEY)
    }
}

suspend fun databaseStructureMigration(
    preferences: SharedPreferences,
    database: FirebaseDatabase,
) {
    // Only perform migration if cloud storage is enabled.
    if (!preferences.getBoolean("ARE_CONVERSATIONS_SAVED", false)) return

    val newKey = preferences.getString(ID_KEY, null)
    val currentDatabaseRef = getDatabase(newKey, database)
    val currentContents = getDatabaseContents(currentDatabaseRef)

    if (currentContents.isEmpty()) return // Assume migration was already performed.

    val userId = FirebaseAuth.getInstance().uid ?: return
    val newRef = database.getReference("users/$userId")
    val newContents = getNewDatabaseContents(newRef)

    val updatedContents = UserModel(conversations = (currentContents + newContents).distinct())
    val json = Json.encodeToString(updatedContents)

    newRef.setValue(json)

    currentDatabaseRef?.removeValue()?.await()
    preferences.edit {
        remove(OLD_ID_KEY)
    }
}

private fun getDatabase(id: String?, database: FirebaseDatabase): DatabaseReference? {
    // If id is missing, we assume user chose not to save history.
    return id?.let { database.getReference("message-$it") }
}

private suspend fun getDatabaseContents(
    ref: DatabaseReference?,
): List<ConversationData> {
    val json = ref?.get()?.await()?.getValue<String>() ?: return emptyList()

    return Json.decodeFromString<List<ConversationData>>(json)
}

private suspend fun getNewDatabaseContents(
    ref: DatabaseReference?,
): List<ConversationData> {
    val json = ref?.get()?.await()?.getValue<String>()?.takeUnless { it.isEmpty() } ?: return emptyList()

    return Json.decodeFromString<UserModel>(json).conversations
}

private fun updateDatabase(
    updatedContents: List<ConversationData>,
    databaseRef: DatabaseReference?,
) {
    val json = Json.encodeToString(updatedContents)

    databaseRef?.setValue(json)
}
