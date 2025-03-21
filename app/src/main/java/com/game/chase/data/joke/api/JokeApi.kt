package com.game.chase.data.joke.api

import com.game.chase.data.joke.api.model.ApiJoke
import retrofit2.http.GET

interface JokeApi {
    @GET("random_joke")
    suspend fun getRandomJoke(): ApiJoke
}