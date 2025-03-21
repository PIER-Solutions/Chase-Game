package com.game.chase.data.joke.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.game.chase.data.joke.db.model.DbJoke

@Database(entities = [DbJoke::class], version = 1, exportSchema = false)
abstract class JokeDatabase : RoomDatabase() {
    abstract fun jokeDao(): JokeDao
}