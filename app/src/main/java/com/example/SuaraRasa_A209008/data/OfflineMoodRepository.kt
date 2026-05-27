package com.example.SuaraRasa_A209008.data

import kotlinx.coroutines.flow.Flow

class OfflineMoodRepository(private val moodEntryDao: MoodEntryDao) : MoodRepository {

    override fun getAllMoodEntriesStream(): Flow<List<MoodEntry>> =
        moodEntryDao.getAllMoodEntries()

    override fun getMoodEntryStream(id: Int): Flow<MoodEntry?> =
        moodEntryDao.getMoodEntryById(id)

    override suspend fun insertMoodEntry(entry: MoodEntry) =
        moodEntryDao.insertMoodEntry(entry)

    override suspend fun deleteMoodEntry(entry: MoodEntry) =
        moodEntryDao.deleteMoodEntry(entry)

    override suspend fun updateMoodEntry(entry: MoodEntry) =
        moodEntryDao.updateMoodEntry(entry)
}
