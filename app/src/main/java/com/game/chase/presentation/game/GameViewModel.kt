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
import com.game.chase.domain.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.game.chase.domain.game.GameInteractor

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
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameInteractor: GameInteractor
) : ViewModel(), GameViewModelInterface {

    private val _gameState = MutableLiveData<GameState>()
    override val gameState: LiveData<GameState> = _gameState

    init {
        startNewGame()
    }

    override fun movePlayer(direction: Direction) {
        _gameState.value = gameInteractor.movePlayer(_gameState.value ?: return, direction)
    }

    override fun teleportPlayer() {
        _gameState.value = gameInteractor.teleportPlayer(_gameState.value ?: return)
    }

    override fun useBomb() {
        _gameState.value = gameInteractor.useBomb(_gameState.value ?: return)
    }

    override fun resetLevel() {
        _gameState.value = gameInteractor.handlePlayerCollision(_gameState.value ?: return)
    }

    override fun startNewGame() {
        _gameState.value = gameInteractor.startNewGame()
    }

    override fun saveScore(score: Int) {
        viewModelScope.launch {
            gameRepository.insertScore(Score(points = score))
        }
    }
}


class MockGameViewModel : ViewModel(), GameViewModelInterface {
    override val gameState = MutableLiveData(
        GameState(
            player = Player(Position(0, 0)),
            enemies = mutableListOf(),
            collisionSquares = mutableListOf()
        )
    )

    override fun movePlayer(direction: Direction) {
        // Mock implementation
    }

    override fun teleportPlayer() {
        // Mock implementation
    }

    override fun useBomb() {
        // Mock implementation
    }

    override fun resetLevel() {
        // Mock implementation
    }

    override fun startNewGame() {
        // Mock implementation
    }

    override fun saveScore(score: Int) {
        // Mock implementation
    }
}