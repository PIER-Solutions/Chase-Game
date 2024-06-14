package com.game.chase.domain.game.util.impl

import com.game.chase.data.Position
import com.game.chase.domain.game.util.PositionGenerator
import java.util.LinkedList
import java.util.Queue

class SpecificPositionGenerator(private val positions: List<Position>) : PositionGenerator {
    private val queue: Queue<Position> = LinkedList(positions)

    override fun getRandomPosition(): Position {
        if (queue.isEmpty()) {
            throw RuntimeException("No more positions in queue")
        }
        return queue.remove()
    }
}