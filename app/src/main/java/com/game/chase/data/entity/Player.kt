package com.game.chase.data.entity

data class Player(
    var position: Position,
    var lives: Int = 3,
    var teleportUses: Int = 3,
    var bombUses: Int = 2
)
