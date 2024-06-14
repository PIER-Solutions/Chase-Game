package com.game.chase.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.game.chase.data.GameDatabase
import com.game.chase.data.GameRepository
import com.game.chase.data.ScoreDao
import com.game.chase.domain.game.util.PositionGenerator
import com.game.chase.domain.game.util.impl.RandomPositionGenerator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

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
    fun provideGameRepository(scoreDao: ScoreDao): GameRepository = GameRepository(scoreDao)

    @Provides
    @Singleton
    fun providePositionGenerator(): PositionGenerator {
        return RandomPositionGenerator()
    }
}
