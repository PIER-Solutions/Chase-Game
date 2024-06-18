package com.game.chase.domain.game.util

import com.game.chase.data.entity.Position

interface PositionGenerator {
    fun getRandomPosition(): Position
}