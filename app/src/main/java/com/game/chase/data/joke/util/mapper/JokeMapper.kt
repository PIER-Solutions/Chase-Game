package com.game.chase.data.joke.util.mapper

import com.game.chase.data.joke.Joke
import com.game.chase.data.joke.api.model.ApiJoke
import com.game.chase.data.joke.db.model.DbJoke

fun Joke.toApiJoke(): ApiJoke {
    return ApiJoke(type = type, setup = setup, punchline = punchline)
}

fun Joke.toDbJoke(): DbJoke {
    return DbJoke(type = type, setup = setup, punchline = punchline)
}

fun DbJoke.toApiJoke(): ApiJoke {
    return ApiJoke(type = type, setup = setup, punchline = punchline)
}

fun DbJoke.toJoke(): Joke {
    return Joke(type = type, setup = setup, punchline = punchline)
}

fun ApiJoke.toDbJoke(): DbJoke {
    return DbJoke(type = type, setup = setup, punchline = punchline)
}

fun ApiJoke.toJoke(): Joke {
    return Joke(type = type, setup = setup, punchline = punchline)
}