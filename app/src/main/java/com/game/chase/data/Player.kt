package com.game.chase.data

import com.game.chase.data.Position

data class Player(
    var position: Position,
    var lives: Int = 3,
    var teleportUses: Int = 3,
    var bombUses: Int = 2
)
