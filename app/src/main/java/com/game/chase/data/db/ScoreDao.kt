package com.game.chase.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.game.chase.data.entity.Score

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(score: Score)

    @Query("SELECT * FROM scores ORDER BY points DESC")
    fun getAllScores(): LiveData<List<Score>>
}

