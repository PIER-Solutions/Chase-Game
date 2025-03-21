package com.game.chase.data.joke.impl

import android.util.Log
import com.game.chase.data.joke.api.JokeApi
import com.game.chase.data.joke.db.JokeDao
import com.game.chase.data.joke.JokeRepository
import com.game.chase.data.joke.Joke
import com.game.chase.data.joke.util.mapper.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class DefaultJokeRepository @Inject constructor(
    private val jokeDao: JokeDao,
    private val jokeApi: JokeApi
) : JokeRepository {

    override suspend fun getRandomJoke(): Joke {
        return try {
            // Try to fetch a joke from the API
            val apiJoke = withContext(Dispatchers.IO) {
                jokeApi.getRandomJoke()
            }
            // If successful, convert to Joke entity and insert into the database
            val joke = apiJoke.toJoke()
            jokeDao.insertJoke(joke.toDbJoke())
            // Return the new joke
            joke
        } catch (e: IOException) {
            Log.e("DefaultJokeRepository", "Network error: ${e.message}")
            jokeDao.getRandomJoke()?.toJoke() ?: Joke(type = "Unknown", setup = "Why aren't networking errors funny?", punchline = "[No Response]")
        } catch (e: Exception) {
            // Network error or API error, fall back to database
            // Log the error for debugging (optional)
            e.printStackTrace()
            // Get a random joke from the database or return a default joke
            jokeDao.getRandomJoke()?.toJoke() ?: Joke(type = "Unknown", setup = "What do you never want, but should always catch?", punchline = "An Exception")
        }
    }
}


