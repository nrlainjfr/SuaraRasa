package com.example.SuaraRasa_A209008.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "letters")
data class LetterEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val mood: String,
    val dateToOpen: Long
)