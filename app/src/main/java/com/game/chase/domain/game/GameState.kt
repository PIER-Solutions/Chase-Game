package com.game.chase.domain.game

import com.game.chase.data.entity.Enemy
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position

data class GameState(
    val player: Player,
    val enemies: MutableList<Enemy>,
    val collisionSquares: MutableList<Position>,
    var score: Int = 0,
    var level: Int = 1,
    var shouldShowEndOfGameDialog: Boolean = false
)
