package com.game.chase.data.joke.api.model

import com.google.gson.annotations.SerializedName

data class ApiJoke(
    @SerializedName("type")
    val type: String,

    @SerializedName("setup")
    val setup: String,

    @SerializedName("punchline")
    val punchline: String
)
