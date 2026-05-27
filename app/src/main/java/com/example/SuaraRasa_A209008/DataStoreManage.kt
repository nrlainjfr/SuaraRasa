package com.example.SuaraRasa_A209008

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DATASTORE_NAME = "favorites_store"

val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

class DataStoreManager(private val context: Context) {
    companion object {
        val FAVORITES_KEY = stringSetPreferencesKey("favorites_quotes")
    }

    val favoritesFlow: Flow<Set<String>> = context.dataStore.data
        .map { preferences ->
            preferences[FAVORITES_KEY] ?: emptySet()
        }

    suspend fun saveFavorites(quotes: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[FAVORITES_KEY] = quotes
        }
    }
}
