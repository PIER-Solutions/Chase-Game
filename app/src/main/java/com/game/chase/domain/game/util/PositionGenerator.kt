package com.game.chase.domain.game.util

import com.game.chase.data.Position

interface PositionGenerator {
    fun getRandomPosition(): Position
}