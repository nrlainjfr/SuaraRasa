package com.example.SuaraRasa_A209008.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlin.also
import kotlin.jvm.java

@Database(entities = [LetterEntity::class], version = 1, exportSchema = false)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun letterDao(): LetterDao

    companion object {
        @Volatile
        private var INSTANCE: DiaryDatabase? = null

        fun getInstance(context: Context): DiaryDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    DiaryDatabase::class.java,
                    "diary_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}