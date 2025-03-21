package com.game.chase.data.joke

interface JokeRepository {
    suspend fun getRandomJoke(): Joke
}