package com.example.SuaraRasa_A209008.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LetterDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(letter: LetterEntity)

    @Query("SELECT * FROM letters WHERE dateToOpen <= :today ORDER BY dateToOpen DESC")
    fun getPastLetters(today: Long): Flow<List<LetterEntity>>
}