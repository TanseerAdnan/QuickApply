package com.t.quickapply.data.local

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.cvDataStore by preferencesDataStore(name = "cv_store")
class CvRepository(private val context: Context) {

    private val KEY = stringPreferencesKey("cv_list")

    val cvList: Flow<List<CvEntry>> = context.cvDataStore.data.map { prefs ->
        val raw = prefs[KEY] ?: return@map emptyList()
        try {
            Json.decodeFromString<List<CvEntry>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun addCv(entry: CvEntry) {
        context.cvDataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<CvEntry>>(prefs[KEY] ?: "[]")
            } catch (e: Exception) { emptyList() }
            val updated = current + entry
            prefs[KEY] = Json.encodeToString(updated)
        }
    }

    suspend fun deleteCv(id: String) {
        context.cvDataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<CvEntry>>(prefs[KEY] ?: "[]")
            } catch (e: Exception) { emptyList() }
            val updated = current.filter { it.id != id }
            prefs[KEY] = Json.encodeToString(updated)
        }
    }

    suspend fun updateCv(updated: CvEntry) {
        context.cvDataStore.edit { prefs ->
            val current = try {
                Json.decodeFromString<List<CvEntry>>(prefs[KEY] ?: "[]")
            } catch (e: Exception) { emptyList() }
            val newList = current.map { if (it.id == updated.id) updated else it }
            prefs[KEY] = Json.encodeToString(newList)
        }
    }
}