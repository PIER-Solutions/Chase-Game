package com.game.chase.presentation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.chase.core.constants.Direction
import com.game.chase.data.db.GameRepository
import com.game.chase.data.entity.Enemy
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.data.entity.Score
import com.game.chase.domain.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.game.chase.domain.game.GameInteractor

interface GameViewModelInterface {
    val gameState: LiveData<GameState>
    val topScores: LiveData<List<Score>>
    fun movePlayer(direction: Direction)
    fun teleportPlayer()
    fun useBomb()
    fun resetLevel()
    fun startNewGame()
    fun saveScore(score: Int)
    fun fetchTopScores()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: GameRepository,
    private val gameInteractor: GameInteractor
) : ViewModel(), GameViewModelInterface {

    private val _gameState = MutableLiveData<GameState>()
    override val gameState: LiveData<GameState> = _gameState
    override val topScores: LiveData<List<Score>> = MutableLiveData()

    init {
        startNewGame()
    }

    fun destroy() {
        gameInteractor.destroy()
    }

    override fun movePlayer(direction: Direction) {
        viewModelScope.launch {
            _gameState.value = gameInteractor.movePlayer(_gameState.value ?: return@launch, direction)
        }
    }

    override fun teleportPlayer() {
        viewModelScope.launch {
            _gameState.value = gameInteractor.teleportPlayer(_gameState.value ?: return@launch)
        }
    }

    override fun useBomb() {
        viewModelScope.launch {
            _gameState.value = gameInteractor.useBomb(_gameState.value ?: return@launch)
        }
    }

    override fun resetLevel() {
        viewModelScope.launch {
            _gameState.value = gameInteractor.handlePlayerCollision(_gameState.value ?: return@launch)
        }
    }

    override fun startNewGame() {
        _gameState.value = gameInteractor.startNewGame()
    }

    override fun saveScore(score: Int) {
        viewModelScope.launch {
            gameRepository.insertScore(Score(points = score))
        }
    }

    override fun fetchTopScores() {
        viewModelScope.launch {
            (topScores as MutableLiveData).value = gameRepository.getTopScores(10)
        }
    }
}


class MockGameViewModel : ViewModel(), GameViewModelInterface {
    override val gameState = MutableLiveData(
        GameState(
            player = Player(Position(9, 9), lives = 3, teleportUses = 2, bombUses = 2),
            enemies = mutableListOf(
                Enemy(Position(1, 7)),
                Enemy(Position(12, 2)),
                Enemy(Position(3, 13))
            ),
            collisionSquares = mutableListOf(
                Position(14, 4),
                Position(6, 16)
            ),
            score = 30,
            level = 3
        )
    )
    override val topScores: LiveData<List<Score>>
        get() = MutableLiveData()

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

    override fun fetchTopScores() {
        // Mock implementation
    }
}