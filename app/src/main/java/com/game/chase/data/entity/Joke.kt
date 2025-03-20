package com.game.chase.data.entity

import com.google.gson.annotations.SerializedName

data class Joke(
    @SerializedName("type")
    val type: String,

    @SerializedName("setup")
    val setup: String,

    @SerializedName("punchline")
    val punchline: String
)
