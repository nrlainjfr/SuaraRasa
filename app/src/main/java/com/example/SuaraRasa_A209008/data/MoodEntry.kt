package com.example.SuaraRasa_A209008.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mood_entries")
data class MoodEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val emotion: String,
    val date: String,        // Format: yyyy-MM-dd
    val time: String,        // Format: HH:mm
    val activity: String,
    val note: String
)

