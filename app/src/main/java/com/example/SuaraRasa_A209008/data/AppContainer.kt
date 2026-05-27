package com.example.SuaraRasa_A209008.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val moodRepository: MoodRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflineMoodRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {

    override val moodRepository: MoodRepository by lazy {
        OfflineMoodRepository(MoodDatabase.getDatabase(context).moodEntryDao())
    }
}
