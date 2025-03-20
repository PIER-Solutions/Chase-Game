package com.game.chase.data.db

import com.game.chase.data.entity.Joke

interface JokeRepository {
    suspend fun getRandomJoke(): Joke
}