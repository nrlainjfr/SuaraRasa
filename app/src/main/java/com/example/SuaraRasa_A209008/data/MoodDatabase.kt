package com.example.SuaraRasa_A209008.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MoodEntry::class], version = 1, exportSchema = false)
abstract class MoodDatabase : RoomDatabase() {

    abstract fun moodEntryDao(): MoodEntryDao

    companion object {
        @Volatile
        private var Instance: MoodDatabase? = null

        fun getDatabase(context: Context): MoodDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    MoodDatabase::class.java,
                    "mood_database"
                ).build().also { Instance = it }
            }
        }
    }
}
