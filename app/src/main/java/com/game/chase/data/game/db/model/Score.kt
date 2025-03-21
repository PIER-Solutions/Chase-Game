package com.game.chase.data.game.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val points: Int,
    val date: Long = System.currentTimeMillis()
)
