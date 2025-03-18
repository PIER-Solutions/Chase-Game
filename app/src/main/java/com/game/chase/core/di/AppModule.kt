package com.game.chase.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.game.chase.data.db.GameDatabase
import com.game.chase.data.db.GameRepository
import com.game.chase.data.db.impl.DefaultGameRepository
import com.game.chase.data.db.ScoreDao
import com.game.chase.domain.game.util.PositionGenerator
import com.game.chase.domain.game.util.impl.RandomPositionGenerator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(gameRepository: DefaultGameRepository): GameRepository

    companion object {
        @Provides
        @Singleton
        fun provideContext(@ApplicationContext context: Context): Context {
            return context
        }

        @Provides
        @Singleton
        fun provideDatabase(app: Application): GameDatabase =
            Room.databaseBuilder(app, GameDatabase::class.java, "game_database").build()

        @Provides
        @Singleton
        fun provideScoreDao(db: GameDatabase): ScoreDao = db.scoreDao()

        @Provides
        @Singleton
        fun provideGameRepository(scoreDao: ScoreDao): DefaultGameRepository = DefaultGameRepository(scoreDao)

        @Provides
        @Singleton
        fun providePositionGenerator(): PositionGenerator {
            return RandomPositionGenerator()
        }
    }
}