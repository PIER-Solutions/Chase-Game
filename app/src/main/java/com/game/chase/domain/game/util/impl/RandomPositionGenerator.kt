package com.game.chase.domain.game.util.impl

import com.game.chase.core.constants.GRID_HEIGHT
import com.game.chase.core.constants.GRID_WIDTH
import com.game.chase.data.entity.Position
import com.game.chase.domain.game.util.PositionGenerator
import java.util.Random

class RandomPositionGenerator : PositionGenerator {
    override fun getRandomPosition() = Position(Random().nextInt(GRID_WIDTH - 1), Random().nextInt(GRID_HEIGHT - 1))
}