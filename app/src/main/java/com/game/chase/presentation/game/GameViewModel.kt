package com.game.chase.presentation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.chase.core.constants.Direction
import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.data.Enemy
import com.game.chase.data.GameRepository
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.data.Score
import com.game.chase.presentation.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sign

interface GameViewModelInterface {
    val gameState: LiveData<GameState>
    fun movePlayer(direction: Direction)
    fun teleportPlayer()
    fun useBomb()
    fun resetLevel()
    fun startNewGame()
    fun saveScore(score: Int)
}

@HiltViewModel
class GameViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel(), GameViewModelInterface {


    private val _gameState = MutableLiveData<GameState>()
    override val gameState: LiveData<GameState> = _gameState
    init {
        startNewGame()
    }

    override fun movePlayer(direction: Direction) {
        val oldPlayer = _gameState.value?.player ?: return
        val newPosition = when (direction) {
            Direction.UP -> Position(oldPlayer.position.x, (oldPlayer.position.y - 1).coerceAtLeast(0))
            Direction.DOWN -> Position(oldPlayer.position.x, (oldPlayer.position.y + 1).coerceAtMost(GRID_SIZE - 1))
            Direction.LEFT -> Position((oldPlayer.position.x - 1).coerceAtLeast(0), oldPlayer.position.y)
            Direction.RIGHT -> Position((oldPlayer.position.x + 1).coerceAtMost(GRID_SIZE - 1), oldPlayer.position.y)
        }
        val newPlayer = oldPlayer.copy(position = newPosition)
        _gameState.value = _gameState.value?.copy(player = newPlayer)
        val newPositions = moveEnemies()
        detectCollisions(newPositions)
    }

    override fun teleportPlayer() {
        val oldPlayer = _gameState.value?.player ?: return
        if (oldPlayer.teleportUses > 0) {
            val newPosition = Position(java.util.Random().nextInt(GRID_SIZE - 1), java.util.Random().nextInt(GRID_SIZE - 1))
            val newPlayer = oldPlayer.copy(position = newPosition, teleportUses = oldPlayer.teleportUses - 1)
            _gameState.value = _gameState.value?.copy(player = newPlayer)
            // Additional logic for updating the game state
            val newPositions = moveEnemies()
            detectCollisions(newPositions)
        }
    }

    override fun useBomb() {
        val oldPlayer = _gameState.value?.player ?: return
        if (oldPlayer.bombUses > 0) {
            val newPlayer = oldPlayer.copy(bombUses = oldPlayer.bombUses - 1)
            _gameState.value = _gameState.value?.copy(player = newPlayer)

            val enemies = _gameState.value?.enemies?.toMutableList() ?: return
            val collisionSquares = _gameState.value?.collisionSquares?.toMutableList() ?: return
            val playerPosition = newPlayer.position

            for (i in enemies.indices.reversed()) {
                val enemy = enemies[i]
                if (isWithinRadius(1, playerPosition, enemy.position, )) {
                    collisionSquares.add(enemy.position)
                    enemies.removeAt(i)
                    //TODO: Calculate points
                }
            }

            _gameState.value = _gameState.value?.copy(enemies = enemies, collisionSquares = collisionSquares)
            val newPositions = moveEnemies()
            detectCollisions(newPositions)
        }
    }

    private fun moveEnemies(): List<Position> {
        val oldEnemies = _gameState.value?.enemies ?: return emptyList()
        val playerPosition = _gameState.value?.player?.position ?: return emptyList()
        val newEnemies = mutableListOf<Enemy>()

        for (enemy in oldEnemies) {
            val dx = playerPosition.x - enemy.position.x
            val dy = playerPosition.y - enemy.position.y
            val newPosition = if (Math.abs(dx) > Math.abs(dy)) {
                // Move in x direction
                Position(enemy.position.x + dx.sign, enemy.position.y)
            } else {
                // Move in y direction
                Position(enemy.position.x, enemy.position.y + dy.sign)
            }
            newEnemies.add(enemy.copy(position = newPosition))
        }

        _gameState.value = _gameState.value?.copy(enemies = newEnemies)

        return newEnemies.map { it.position }
    }

    private fun detectCollisions(newPositions: List<Position>) {
        val playerPosition = _gameState.value?.player?.position ?: return
        val enemies = _gameState.value?.enemies?.toMutableList() ?: return
        val collisionSquares = _gameState.value?.collisionSquares?.toMutableList() ?: return

        //TODO: add point system

        for (i in enemies.indices.reversed()) {
            val enemy = enemies[i]
            if (enemy.position == playerPosition) {
                // Collision detected with player
                _gameState.value?.player?.lives?.minus(1)
                collisionSquares.add(playerPosition)
                collisionSquares.add(enemy.position) // Add enemy's position to collisionSquares
                enemies.removeAt(i) // Remove enemy from list
                //TODO: reset level if there are still lives left
            } else if (collisionSquares.contains(enemy.position)) {
                // Collision detected with existing collision square
                if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                enemies.removeAt(i) // Remove enemy from list
            } else if (newPositions.count { it == enemy.position } > 1) {
                // Collision detected with other enemies
                if (!collisionSquares.contains(enemy.position)) { collisionSquares.add(enemy.position) }
                enemies.removeAt(i) // Remove enemy from list
            }
        }

        // Check if there are no enemies remaining and the player is not on a collision square
        if (enemies.isEmpty() && !collisionSquares.contains(playerPosition)) {
            // Trigger the next level
            nextLevel()
        }
        else {
            // Update the game state with the new enemies and collision squares
//            updateGameState(enemies, collisionSquares)
            _gameState.value = _gameState.value?.copy(enemies = enemies, collisionSquares = collisionSquares)
        }


    }

    private fun nextLevel() {
        _gameState.value?.let {
            val newLevel = it.level + 1
            _gameState.value = GameState(
//                player = it.player, // this setting will make the player start the new level at the same position
                player = it.player.copy(position = Position(GRID_SIZE / 2, GRID_SIZE / 2)),
                enemies = generateEnemies(newLevel, it.player.position).toMutableList(),
                collisionSquares = listOf(),
                score = it.score,
                level = newLevel
            )
        }
    }

    override fun resetLevel() {
        _gameState.value?.let {
            it.player.lives--
            if (it.player.lives > 0) {
                _gameState.value = GameState(
                    player = it.player.copy(position = Position(GRID_SIZE / 2, GRID_SIZE / 2)),
                    enemies = generateEnemies(it.level, it.player.position).toMutableList(),
                    collisionSquares = listOf(),
                    score = it.score,
                    level = it.level
                )
            } else {
                startNewGame()
            }
        }
    }

    override fun startNewGame() {
        _gameState.value = GameState(
            player = Player(Position(GRID_SIZE / 2, GRID_SIZE / 2)),
            enemies = generateEnemies(1, Position(GRID_SIZE / 2, GRID_SIZE / 2)).toMutableList(),
            collisionSquares = listOf(),
            score = 0,
            level = 1
        )
    }

    private fun generateEnemies(level: Int, playerPosition: Position): List<Enemy> {
        val enemies = mutableListOf<Enemy>()
        val gridSize = GRID_SIZE
        val random = java.util.Random()

        repeat(level + 2) {
            var position: Position
            do {
                position = Position(random.nextInt(gridSize), random.nextInt(gridSize))
            } while (isWithinRadius(2, position, playerPosition)) // Ensure enemies are not too close to the player
            enemies.add(Enemy(position))
        }
        return enemies
    }

    private fun isWithinRadius(radius: Int, pos1: Position, pos2: Position): Boolean {
        return Math.abs(pos1.x - pos2.x) <= radius && Math.abs(pos1.y - pos2.y) <= radius
    }

    override fun saveScore(score: Int) {
        viewModelScope.launch {
            gameRepository.insertScore(Score(points = score))
        }
    }
}

class MockGameViewModel : ViewModel(), GameViewModelInterface {
    // Override any functions and properties from GameViewModel that are used in GameScreen
    // For example, if GameViewModel has a gameState property, you can override it like this:
    override val gameState = MutableLiveData(
        GameState(
            player = Player(Position(0, 0)),
            enemies = mutableListOf(
                Enemy(Position(1, 1)),
                Enemy(Position(2, 2))
            ),
            collisionSquares = listOf(
                Position(3, 3),
                Position(4, 4)
            ),
            score = 0,
            level = 1
    )) // Replace GameState() with your dummy data

    override fun movePlayer(direction: Direction) {}
    override fun teleportPlayer() {}
    override fun useBomb() {}
    override fun resetLevel() {}
    override fun startNewGame() {}
    override fun saveScore(score: Int) {}
}