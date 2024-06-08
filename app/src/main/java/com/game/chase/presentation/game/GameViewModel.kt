package com.game.chase.presentation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.chase.core.constants.Direction
import com.game.chase.data.GameRepository
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.data.Score
import com.game.chase.presentation.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(private val gameRepository: GameRepository) : ViewModel() {
    private val _gameState = MutableLiveData<GameState>()
    val gameState: LiveData<GameState> = _gameState

    init {
        startNewGame()
    }

    fun movePlayer(direction: Direction) {
        val player = _gameState.value?.player ?: return
        when (direction) {
            Direction.UP -> player.position.y = (player.position.y - 1).coerceAtLeast(0)
            Direction.DOWN -> player.position.y = (player.position.y + 1).coerceAtMost(9)
            Direction.LEFT -> player.position.x = (player.position.x - 1).coerceAtLeast(0)
            Direction.RIGHT -> player.position.x = (player.position.x + 1).coerceAtMost(9)
        }
        // Additional logic for updating the game state
    }

    fun teleportPlayer() {
        val player = _gameState.value?.player ?: return
        if (player.teleportUses > 0) {
            player.position = Position(java.util.Random().nextInt(10), java.util.Random().nextInt(10))
            player.teleportUses--
            // Additional logic for updating the game state
        }
    }

    fun useBomb() {
        val player = _gameState.value?.player ?: return
        if (player.bombUses > 0) {
            // Logic for bomb effect
            player.bombUses--
            // Additional logic for updating the game state
        }
    }

    fun resetLevel() {
        _gameState.value?.let {
            it.player.lives--
            if (it.player.lives > 0) {
                _gameState.value = GameState(
                    player = it.player,
                    enemies = gameRepository.generateEnemies(it.level, it.player.position),
                    collisionSquares = listOf(),
                    score = it.score,
                    level = it.level
                )
            } else {
                startNewGame()
            }
        }
    }

    fun startNewGame() {
        _gameState.value = GameState(
            player = Player(Position(0, 0)),
            enemies = gameRepository.generateEnemies(1, Position(0, 0)),
            collisionSquares = listOf(),
            score = 0,
            level = 1
        )
    }

    fun saveScore(score: Int) {
        viewModelScope.launch {
            gameRepository.insertScore(Score(points = score))
        }
    }
}
