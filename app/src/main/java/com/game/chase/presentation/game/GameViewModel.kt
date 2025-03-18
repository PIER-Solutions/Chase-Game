package com.game.chase.presentation.game

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.chase.core.constants.Direction
import com.game.chase.data.db.impl.DefaultGameRepository
import com.game.chase.data.entity.Enemy
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.data.entity.Score
import com.game.chase.domain.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.game.chase.domain.game.GameInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface GameViewModelInterface {
    val gameState: LiveData<GameState>
    val topScores: LiveData<List<Score>>
    val showEndOfGameDialog: LiveData<Boolean>
    fun movePlayer(direction: Direction)
    fun teleportPlayer()
    fun useBomb()
    fun startNewGame()
    fun saveScore(score: Int)
    fun fetchTopScores()
    fun dismissEndOfGameDialog()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameRepository: DefaultGameRepository,
    private val gameInteractor: GameInteractor
) : ViewModel(), GameViewModelInterface {

    private val _gameState = MutableLiveData<GameState>()
    override val gameState: LiveData<GameState> = _gameState
    override val topScores: LiveData<List<Score>> = MutableLiveData()
    private val _showEndOfGameDialog = MutableLiveData<Boolean>()
    override val showEndOfGameDialog: LiveData<Boolean> = _showEndOfGameDialog

    init {
        startNewGame()
    }

    fun destroy() {
        gameInteractor.destroy()
    }

    private suspend fun processGameState(newGameState: GameState) {
        /*
        Order of operations:
        1. Player moves, teleports or uses bomb (then this method is called)
        2. Enemies move
        3. Detect collisions (also scores collisions)
        4. Handle collisions
        5. Update game state

         */

        val updatedEnemiesGameState = gameInteractor.updateEnemies(newGameState)
        val nextGameState = gameInteractor.detectCollisions(updatedEnemiesGameState)

        if (nextGameState.collisionSquares.contains(nextGameState.player.position)) {
            val oldPlayer = nextGameState.player
            oldPlayer.lives--
            if (oldPlayer.lives > 0) {
                // decrement a life and reset the level
                val newPlayerPosition = gameInteractor.getPlayerStartPosition()
                val newState = GameState(
                    player = oldPlayer.copy(position = newPlayerPosition),
                    enemies = gameInteractor.generateEnemies(nextGameState.level, newPlayerPosition).toMutableList(),
                    collisionSquares = mutableListOf(),
                    score = nextGameState.score,
                    level = nextGameState.level
                )
                _gameState.value = newState
            } else {
                // Player has no lives left, save the score and end the game
                withContext(Dispatchers.IO) { gameRepository.insertScore(Score(points = nextGameState.score)) }
                _gameState.value = nextGameState.copy(player = oldPlayer)
                fetchTopScores()
                _showEndOfGameDialog.value = true
            }
        } else if (nextGameState.enemies.isEmpty()) {
            // Add bonuses
            when
                (nextGameState.level % 3 == 0) {
                    true -> nextGameState.player.bombUses++
                    false -> nextGameState.player.teleportUses++
                }

            _gameState.value = gameInteractor.nextLevel(nextGameState)
        } else {
            _gameState.value = nextGameState
        }

    }

    override fun movePlayer(direction: Direction) {
        viewModelScope.launch {
            val oldGameState = _gameState.value ?: return@launch
            val newGameState = gameInteractor.movePlayer(oldGameState, direction)

            // Only update enemies if the player's position has changed
            if (oldGameState.player.position != newGameState.player.position) {
                processGameState(newGameState)
            } else {
                _gameState.value = newGameState
            }

        }
    }

    override fun teleportPlayer() {
        viewModelScope.launch {
            val newGameState = gameInteractor.teleportPlayer(_gameState.value ?: return@launch)
            processGameState(newGameState)
        }
    }

    override fun useBomb() {
        viewModelScope.launch {
            val newGameState = gameInteractor.useBomb(_gameState.value ?: return@launch)
            processGameState(newGameState)
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

    override fun dismissEndOfGameDialog() {
        _showEndOfGameDialog.value = false // Implement the function here
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
    override val showEndOfGameDialog: LiveData<Boolean>
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

    override fun startNewGame() {
        // Mock implementation
    }

    override fun saveScore(score: Int) {
        // Mock implementation
    }

    override fun fetchTopScores() {
        // Mock implementation
    }

    override fun dismissEndOfGameDialog() {
        // Mock implementation
    }
}