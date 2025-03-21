package com.game.chase.data.joke.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jokes")
data class DbJoke(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "setup")
    val setup: String,

    @ColumnInfo(name = "punchline")
    val punchline: String
)
