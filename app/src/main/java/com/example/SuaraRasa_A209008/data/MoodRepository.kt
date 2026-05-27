package com.example.SuaraRasa_A209008.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, update, delete, and retrieve of [MoodEntry] from a given data source.
 */
interface MoodRepository {

    /**
     * Retrieve all mood entries from the data source.
     */
    fun getAllMoodEntriesStream(): Flow<List<MoodEntry>>

    /**
     * Retrieve a single mood entry by its ID.
     */
    fun getMoodEntryStream(id: Int): Flow<MoodEntry?>

    /**
     * Insert a mood entry into the data source.
     */
    suspend fun insertMoodEntry(entry: MoodEntry)

    /**
     * Delete a mood entry from the data source.
     */
    suspend fun deleteMoodEntry(entry: MoodEntry)

    /**
     * Update an existing mood entry in the data source.
     */
    suspend fun updateMoodEntry(entry: MoodEntry)
}
