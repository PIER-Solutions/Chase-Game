package com.game.chase.data.db.impl

import android.util.Log
import com.game.chase.data.db.JokeApi
import com.game.chase.data.db.JokeRepository
import com.game.chase.data.entity.Joke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class DefaultJokeRepository @Inject constructor(
    private val jokeApi: JokeApi
) : JokeRepository {

    override suspend fun getRandomJoke(): Joke {
        return withContext(Dispatchers.IO) {
            try {
                val response = jokeApi.getRandomJoke()
                response
            } catch (e: IOException) {
                Log.e("DefaultJokeRepository", "Network error: ${e.message}")
                Joke(type = "Unknown", setup = "Why aren't networking errors funny?", punchline = "[No Response]")
            } catch (e: Exception) {
                Log.e("DefaultJokeRepository", "Unexpected error: ${e.message}")
                Joke(type = "Unknown", setup = "What do you never want, but should always catch?", punchline = "An Exception")
            }
        }
    }
}


