package com.example.prac4.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class UserPreferencesRepository(private val context: Context) {

    companion object {
        private val HIGHLIGHT_DONE = booleanPreferencesKey("highlight_done_tasks")
    }

    val highlightDoneTasks: Flow<Boolean> = context.dataStore.data
        .catch { if (it is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw it }
        .map { preferences -> preferences[HIGHLIGHT_DONE] ?: false }

    suspend fun setHighlightDoneTasks(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[HIGHLIGHT_DONE] = enabled
        }
    }
}