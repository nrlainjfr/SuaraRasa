package com.example.SuaraRasa_A209008.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "playlists")
private val PLAYLIST_KEY = stringPreferencesKey("saved_playlists")

class PlaylistDataStore(private val context: Context) {
    private val gson = Gson()

    val playlistsFlow: Flow<List<MusicCategory>> = context.dataStore.data
        .map { preferences ->
            val json = preferences[PLAYLIST_KEY] ?: return@map emptyList()
            try {
                val type = object : TypeToken<List<MusicCategory>>() {}.type
                gson.fromJson<List<MusicCategory>>(json, type)
                    ?.map { MusicCategory.fromSerialized(it) }
                    ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun savePlaylists(playlists: List<MusicCategory>) {
        try {
            val serialized = playlists.map { it.toSerializable() }
            val json = gson.toJson(serialized)
            context.dataStore.edit { preferences ->
                preferences[PLAYLIST_KEY] = json
            }
        } catch (e: Exception) {
            // Handle error
        }
    }
}