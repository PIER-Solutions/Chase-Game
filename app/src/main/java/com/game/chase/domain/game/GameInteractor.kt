package com.game.chase.domain.game

import com.game.chase.core.constants.Direction
import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.data.GameRepository
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.data.Enemy
import com.game.chase.domain.game.util.PositionGenerator
import java.util.Random
import javax.inject.Inject
import kotlin.math.sign


class GameInteractor @Inject constructor(
    private val gameRepository: GameRepository,
    private val positionGenerator: PositionGenerator)
     {

    fun movePlayer(gameState: GameState, direction: Direction): GameState {
        val oldPlayer = gameState.player
        val newPosition = when (direction) {
            Direction.UP -> Position(oldPlayer.position.x, (oldPlayer.position.y - 1).coerceAtLeast(0))
            Direction.DOWN -> Position(oldPlayer.position.x, (oldPlayer.position.y + 1).coerceAtMost(GRID_SIZE - 1))
            Direction.LEFT -> Position((oldPlayer.position.x - 1).coerceAtLeast(0), oldPlayer.position.y)
            Direction.RIGHT -> Position((oldPlayer.position.x + 1).coerceAtMost(GRID_SIZE - 1), oldPlayer.position.y)
        }

        if (gameState.collisionSquares.contains(newPosition)) {
            // Add error response message
            return gameState
        } else if (newPosition.x < 0 || newPosition.x >= GRID_SIZE || newPosition.y < 0 || newPosition.y >= GRID_SIZE) {
            // Add error response message
            return gameState
        } else {
            val newPlayer = oldPlayer.copy(position = newPosition)
            return updateEnemies(gameState.copy(player = newPlayer))
        }
    }

    fun teleportPlayer(gameState: GameState): GameState {
        val oldPlayer = gameState.player
        if (oldPlayer.teleportUses > 0) {
            var newPosition: Position
            do {
                // TBD - may need to add an alternate algorithm to find a new position once there are too many occupied squares (or switch to tracking game state via each square knowing its state
                newPosition = positionGenerator.getRandomPosition()
            } while (isPositionOccupied(newPosition, gameState)) //TODO: needs to try to find a new position until an acceptable one is found
            val newPlayer = oldPlayer.copy(position = newPosition, teleportUses = oldPlayer.teleportUses - 1)
            return updateEnemies(gameState.copy(player = newPlayer))
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

            for (i in gameState.enemies.indices.reversed()) {
                val enemy = gameState.enemies[i]
                if (isWithinRadius(1, gameState.player.position, enemy.position, )) {
                    gameState.collisionSquares.add(enemy.position)
                    gameState.enemies.removeAt(i)
                    //TODO: Calculate points
                }
            }
            return updateEnemies(gameState)
        }
        return gameState
    }

    fun updateEnemies(gameState: GameState): GameState {
        val oldEnemies = gameState.enemies
        val playerPosition = gameState.player.position
        val newEnemies = mutableListOf<Enemy>()

        for (enemy in oldEnemies) {
            val dx = playerPosition.x - enemy.position.x
            val dy = playerPosition.y - enemy.position.y
            val newPosition = Position(enemy.position.x + dx.sign, enemy.position.y + dy.sign)
            newEnemies.add(enemy.copy(position = newPosition))
        }

        return detectCollisions(gameState.copy(enemies = newEnemies))
    }

    fun detectCollisions(gameState: GameState): GameState {
        val playerPosition = gameState.player.position
        val enemies = gameState.enemies.toMutableList()
        val collisionSquares = gameState.collisionSquares.toMutableList()

        for (i in enemies.indices.reversed()) {
            val enemy = enemies[i]
            if (enemy.position == playerPosition) {
                // Collision detected with player
                collisionSquares.add(enemy.position) // Add enemy's position to collisionSquares
                enemies.removeAt(i) // Remove enemy from list TODO: Not sure we need this
            } else if (collisionSquares.contains(enemy.position)) {
                // Collision detected with existing collision square
                if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                enemies.removeAt(i) // Remove enemy from list
            } else if (gameState.enemies.count { it.position == enemy.position } > 1) {
                // Collision detected with other enemies
                if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                enemies.removeAt(i) // Remove enemy from list
            }
        }

        // Check if there are no enemies remaining and the player is not on a collision square
        return if (collisionSquares.contains(playerPosition)) {
            // Trigger the next level
            handlePlayerCollision(gameState)
        } else if (enemies.isEmpty() && !collisionSquares.contains(playerPosition)) {
            // Trigger the next level
            nextLevel(gameState)
        } else {
            // Update the game state with the new enemies and collision squares
            gameState.copy(enemies = enemies, collisionSquares = collisionSquares)
        }
    }


    fun handlePlayerCollision(gameState: GameState): GameState {
        val oldPlayer = gameState.player
        oldPlayer.lives--
        return if (oldPlayer.lives > 0) {
            GameState(
                player = oldPlayer.copy(position = Position(GRID_SIZE / 2, GRID_SIZE / 2)),
                enemies = generateEnemies(gameState.level, oldPlayer.position).toMutableList(),
                collisionSquares = mutableListOf(),
                score = gameState.score,
                level = gameState.level
            )
        } else {
            startNewGame()
        }
    }

    fun nextLevel(gameState: GameState): GameState {
        val newLevel = gameState.level + 1
        return GameState(
            player = gameState.player.copy(position = Position(GRID_SIZE / 2, GRID_SIZE / 2)),
            enemies = generateEnemies(newLevel, gameState.player.position).toMutableList(),
            collisionSquares = mutableListOf(),
            score = gameState.score,
            level = newLevel
        )
    }

    fun startNewGame(): GameState {
        return GameState(
            player = Player(Position(GRID_SIZE / 2, GRID_SIZE / 2)),
            enemies = generateEnemies(1, Position(GRID_SIZE / 2, GRID_SIZE / 2)).toMutableList(),
            collisionSquares = mutableListOf(),
            score = 0,
            level = 1
        )
    }

    fun generateEnemies(level: Int, playerPosition: Position): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val gridSize = GRID_SIZE
        val random = Random()

        repeat(level + 2) {
            var position: Position
            do {
                position = Position(random.nextInt(gridSize), random.nextInt(gridSize))
            } while (isWithinRadius(2, position, playerPosition)) // Ensure enemies are not too close to the player
            enemies.add(Enemy(position))
        }
        return enemies
    }
     fun isWithinRadius(radius: Int, pos1: Position, pos2: Position): Boolean {
        return Math.abs(pos1.x - pos2.x) <= radius && Math.abs(pos1.y - pos2.y) <= radius
    }
}