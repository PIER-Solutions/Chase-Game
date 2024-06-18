package com.game.chase.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.game.chase.data.entity.Score

@Database(entities = [Score::class], version = 1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun scoreDao(): ScoreDao

    companion object {
        @Volatile private var instance: GameDatabase? = null

        fun getDatabase(context: Context): GameDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    GameDatabase::class.java, "game_database"
                ).build().also { instance = it }
            }
    }
}
