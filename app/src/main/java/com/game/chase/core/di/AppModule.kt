package com.game.chase.core.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.game.chase.data.game.db.GameDatabase
import com.game.chase.data.game.GameRepository
import com.game.chase.data.joke.api.JokeApi
import com.game.chase.data.joke.db.JokeDao
import com.game.chase.data.joke.db.JokeDatabase
import com.game.chase.data.joke.JokeRepository
import com.game.chase.data.game.impl.DefaultGameRepository
import com.game.chase.data.game.db.ScoreDao
import com.game.chase.data.joke.impl.DefaultJokeRepository
import com.game.chase.domain.game.util.PositionGenerator
import com.game.chase.domain.game.util.impl.RandomPositionGenerator
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    /*
        @Binds vs @Provides example:

        @Binds (Interface to Implementation): If JokeRepository is an interface and DefaultJokeRepository
        is its concrete implementation, and you want to use DefaultJokeRepository in all cases, then the
        @Binds method is likely the correct approach.

        @Provides (Custom Creation): If you need to create JokeRepository with specific dependencies
        (JokeDao, JokeApi) and potentially perform some custom logic during creation, then the @Provides
        method is the correct choice.
     */

    @Binds
    @Singleton
    abstract fun bindGameRepository(gameRepository: DefaultGameRepository): GameRepository

    @Binds
    @Singleton
    abstract fun bindJokeRepository(jokeRepository: DefaultJokeRepository): JokeRepository

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
        fun provideJokeDatabase(app: Application): JokeDatabase {
            return Room.databaseBuilder(app, JokeDatabase::class.java, "joke_db")
                .createFromAsset("joke_db")
                .build()
        }

        @Provides
        @Singleton
        fun provideJokeDao(db: JokeDatabase): JokeDao {
            return db.jokeDao()
        }

        @Provides
        @Singleton
        fun providePositionGenerator(): PositionGenerator {
            return RandomPositionGenerator()
        }

        @Provides
        @Singleton
        fun provideJokeApi(okHttpClient: OkHttpClient): JokeApi {
            return Retrofit.Builder()
                .baseUrl("https://official-joke-api.appspot.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
                .create(JokeApi::class.java)
        }

        @Provides
        @Singleton
        fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }

        @Provides
        @Singleton
        fun provideLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

    }
}