package com.game.chase.domain.game

import com.game.chase.core.constants.Direction
import com.game.chase.core.constants.GRID_HEIGHT
import com.game.chase.core.constants.GRID_WIDTH
import com.game.chase.data.db.GameRepository
import com.game.chase.data.db.JokeRepository
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.data.entity.Enemy
import com.game.chase.data.entity.Joke
import com.game.chase.data.entity.Score
import com.game.chase.domain.game.util.PositionGenerator
import kotlinx.coroutines.CoroutineScope
import java.util.Random
import javax.inject.Inject
import kotlin.math.sign
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.collections.*
import kotlin.math.abs
import kotlin.math.ceil

class GameInteractor @Inject constructor(
    private val positionGenerator: PositionGenerator,
    private val gameRepository: GameRepository,
    private val jokeRepository: JokeRepository
) {

    private val updateScope = CoroutineScope(Dispatchers.Default)
     fun destroy() {
         updateScope.cancel()
     }

    fun movePlayer(gameState: GameState, direction: Direction): GameState {
        val oldPlayer = gameState.player
        val newPosition = when (direction) {
            Direction.UP -> Position(oldPlayer.position.x, (oldPlayer.position.y - 1))
            Direction.DOWN -> Position(oldPlayer.position.x, (oldPlayer.position.y + 1))
            Direction.LEFT -> Position((oldPlayer.position.x - 1), oldPlayer.position.y)
            Direction.RIGHT -> Position((oldPlayer.position.x + 1), oldPlayer.position.y)
        }

        if (gameState.collisionSquares.contains(newPosition)) {
            // Add error response message
            return gameState
        } else if (newPosition.x < 0 || newPosition.x >= GRID_WIDTH || newPosition.y < 0 || newPosition.y >= GRID_HEIGHT) {
            // Add error response message
            return gameState
        } else if (oldPlayer.lives <= 0) {
            // Add error response message
            return gameState
        } else {
            val newPlayer = oldPlayer.copy(position = newPosition)
            return gameState.copy(player = newPlayer)
        }
    }

    fun teleportPlayer(gameState: GameState): GameState {
        val oldPlayer = gameState.player
        if (oldPlayer.teleportUses > 0) {
            var newPosition: Position
            do {
                // TBD - may need to add an alternate algorithm to find a new position once there are too many occupied squares (or switch to tracking game state via each square knowing its state
                newPosition = positionGenerator.getRandomPosition()
            } while (isPositionOccupied(newPosition, gameState))
            val newPlayer = oldPlayer.copy(position = newPosition, teleportUses = oldPlayer.teleportUses - 1)
            return gameState.copy(player = newPlayer)
        }
        return gameState
    }

    private fun isPositionOccupied(position: Position, gameState: GameState): Boolean {
        return gameState.player.position == position ||
                gameState.enemies.any { it.position == position } ||
                gameState.collisionSquares.contains(position)
    }

    fun useBomb(gameState: GameState): GameState {
        val oldPlayer = gameState.player
        if (oldPlayer.bombUses > 0) {
            gameState.player.bombUses--

            var updatedScore = gameState.score
            for (i in gameState.enemies.indices.reversed()) {
                val enemy = gameState.enemies[i]
                if (isWithinRadius(1, gameState.player.position, enemy.position)) {
                    gameState.collisionSquares.add(enemy.position)
                    gameState.enemies.removeAt(i)
                    updatedScore++
                }
            }
            return gameState.copy(score = updatedScore)
        }
        return gameState
    }

    fun updateEnemies(gameState: GameState): GameState {
        val oldEnemies = gameState.enemies
        val playerPosition = gameState.player.position
        val newEnemies = mutableListOf<Enemy>()

        // for simple diagonal movement:
        for (enemy in oldEnemies) {
            val dx = playerPosition.x - enemy.position.x
            val dy = playerPosition.y - enemy.position.y

            val newPosition = Position(enemy.position.x + dx.sign, enemy.position.y + dy.sign)
            newEnemies.add(enemy.copy(position = newPosition))
        }

        return gameState.copy(enemies = newEnemies)
    }

     fun detectCollisions(gameState: GameState): GameState {
         val playerPosition = gameState.player.position
         val enemies = gameState.enemies.toMutableList()
         val collisionSquares = gameState.collisionSquares.toMutableList()
         var score = gameState.score
         val scoredEnemies = mutableSetOf<Enemy>() // Set to keep track of scored enemies

         for (i in enemies.indices.reversed()) {
             val enemy = enemies[i]
             if (enemy.position == playerPosition) {
                 // Collision detected with player
                 collisionSquares.add(enemy.position) // Add enemy's position to collisionSquares
                 scoredEnemies.add(enemy) // Add enemy to scoredEnemies set
                 enemies.removeAt(i) // Remove enemy from list
             } else if (collisionSquares.contains(enemy.position)) {
                 // Collision detected with existing collision square
                 if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                 if (!scoredEnemies.contains(enemy)) {
                     score += 1 // Add 1 point for every Enemy that collides with collision square
                     scoredEnemies.add(enemy) // Add enemy to scoredEnemies set
                 }
                 enemies.removeAt(i) // Remove enemy from list
             } else if (gameState.enemies.count { it.position == enemy.position } > 1) {
                 // Collision detected with other enemies
                 if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                 enemies.removeAt(i) // Remove enemy from list
                 val pileSize = gameState.enemies.count { it.position == enemy.position }
                 score += when (pileSize) {
                     2 -> 3 // Add 3 points for two Enemies colliding and creating a pile
                     3 -> 5 // Add 5 points for three Enemies colliding and creating a pile
                     else -> 0
                 }
                 scoredEnemies.add(enemy) // Add enemy to scoredEnemies set
             }
         }
         return gameState.copy(enemies = enemies, collisionSquares = collisionSquares, score = score)
     }

     fun getPlayerStartPosition(): Position {
         return Position(ceil((GRID_WIDTH / 2).toDouble()).toInt(), ceil((GRID_HEIGHT / 2).toDouble()).toInt())
     }

    fun nextLevel(gameState: GameState): GameState {
        val newLevel = gameState.level + 1
        val newScore = gameState.score + (3 * gameState.level)
        val newPlayerPosition = getPlayerStartPosition()
        return GameState(
            player = gameState.player.copy(position = newPlayerPosition),
            enemies = generateEnemies(newLevel, newPlayerPosition).toMutableList(),
            collisionSquares = mutableListOf(),
            score = newScore,
            level = newLevel
        )
    }

    fun startNewGame(): GameState {
        return GameState(
            player = Player(position = getPlayerStartPosition()),
            enemies = generateEnemies(1, getPlayerStartPosition()).toMutableList(),
            collisionSquares = mutableListOf(),
            score = 0,
            level = 1
        )
    }

    fun generateEnemies(level: Int, playerPosition: Position): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val random = Random()

        repeat(level + 2) {
            var position: Position
            do {
                position = Position(random.nextInt(GRID_WIDTH), random.nextInt(GRID_HEIGHT))
            } while (isWithinRadius(2, position, playerPosition)) // Ensure enemies are not too close to the player
            enemies.add(Enemy(position))
        }
        return enemies
    }

     fun isWithinRadius(radius: Int, pos1: Position, pos2: Position): Boolean {
        return abs(pos1.x - pos2.x) <= radius && abs(pos1.y - pos2.y) <= radius
    }

    suspend fun fetchJoke(): Joke {
        return jokeRepository.getRandomJoke()
    }

    suspend fun insertScore(points: Int) {
        return gameRepository.insertScore(Score(points = points))
    }

    suspend fun getTopScores(limit: Int): List<Score> {
        return gameRepository.getTopScores(limit)
    }

    suspend fun getLatestScore(): Score? {
        return gameRepository.getLatestScore()
    }
}