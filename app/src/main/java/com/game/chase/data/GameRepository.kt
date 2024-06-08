package com.game.chase.data

import androidx.lifecycle.LiveData
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class GameRepository @Inject constructor(private val scoreDao: ScoreDao) {
    val allScores: LiveData<List<Score>> = scoreDao.getAllScores()

    suspend fun insertScore(score: Score) {
        scoreDao.insert(score)
    }

    fun generateEnemies(level: Int, playerPosition: Position): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val gridSize = 10 // Assuming a 10x10 grid
        val random = java.util.Random()

        repeat(level + 2) {
            var position: Position
            do {
                position = Position(random.nextInt(gridSize), random.nextInt(gridSize))
            } while (isTooClose(position, playerPosition, minDistance = 2))
            enemies.add(Enemy(position))
        }
        return enemies
    }

    private fun isTooClose(pos1: Position, pos2: Position, minDistance: Int): Boolean {
        return Math.abs(pos1.x - pos2.x) <= minDistance && Math.abs(pos1.y - pos2.y) <= minDistance
    }
}


