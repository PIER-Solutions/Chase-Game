package com.game.chase.domain.game

import com.game.chase.data.Enemy
import com.game.chase.data.Player
import com.game.chase.data.Position

data class GameState(
    val player: Player,
    val enemies: MutableList<Enemy>,
    val collisionSquares: MutableList<Position>,
    var score: Int = 0,
    var level: Int = 1,
)
