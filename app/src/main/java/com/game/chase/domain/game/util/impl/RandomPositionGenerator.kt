package com.game.chase.domain.game.util.impl

import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.data.Position
import com.game.chase.domain.game.util.PositionGenerator
import java.util.Random

class RandomPositionGenerator : PositionGenerator {
    override fun getRandomPosition() = Position(Random().nextInt(GRID_SIZE - 1), Random().nextInt(GRID_SIZE - 1))
}