package com.example.SuaraRasa_A209008.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMoodEntry(entry: MoodEntry)

    @Update
    suspend fun updateMoodEntry(entry: MoodEntry)

    @Delete
    suspend fun deleteMoodEntry(entry: MoodEntry)

    @Query("SELECT * FROM mood_entries ORDER BY date DESC, time DESC")
    fun getAllMoodEntries(): Flow<List<MoodEntry>>

    @Query("SELECT * FROM mood_entries WHERE id = :id")
    fun getMoodEntryById(id: Int): Flow<MoodEntry?>
}
