package com.game.chase.data.db

import com.game.chase.data.entity.Joke
import retrofit2.http.GET

interface JokeApi {
    @GET("random_joke")
    suspend fun getRandomJoke(): Joke
}