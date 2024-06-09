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
            val newPosition = Position(java.util.Random().nextInt(10), java.util.Random().nextInt(10))
            val newPlayer = oldPlayer.copy(position = newPosition, teleportUses = oldPlayer.teleportUses - 1)
            _gameState.value = _gameState.value?.copy(player = newPlayer)
            // Additional logic for updating the game state
            val newPositions = moveEnemies()
            detectCollisions(newPositions)
        }
    }

    override fun useBomb() {
        val player = _gameState.value?.player ?: return
        if (player.bombUses > 0) {
            // TODO: Logic for bomb effect - 1-block radius
            player.bombUses--
            // Additional logic for updating the game state
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
            } else if (newPositions.count { it == enemy.position } > 1) {
                // Collision detected with other enemies
                collisionSquares.add(enemy.position)
                enemies.removeAt(i) // Remove enemy from list
            } else if (collisionSquares.contains(enemy.position)) {
                // Collision detected with existing collision square
                collisionSquares.add(enemy.position)
                enemies.removeAt(i) // Remove enemy from list
            }
        }

        //TODO check for enemy count = 0 to start new level with more enemies

        _gameState.value = _gameState.value?.copy(enemies = enemies, collisionSquares = collisionSquares)
    }

    override fun resetLevel() {
        _gameState.value?.let {
            it.player.lives--
            if (it.player.lives > 0) {
                _gameState.value = GameState(
                    player = it.player,
                    enemies = gameRepository.generateEnemies(it.level, it.player.position).toMutableList(),
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
            enemies = gameRepository.generateEnemies(1, Position(10, 10)).toMutableList(),
            collisionSquares = listOf(),
            score = 0,
            level = 1
        )
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