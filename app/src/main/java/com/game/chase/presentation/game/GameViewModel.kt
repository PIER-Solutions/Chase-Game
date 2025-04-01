package com.game.chase.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.game.chase.core.constants.Direction
import com.game.chase.core.util.log.LogX
import com.game.chase.data.entity.Enemy
import com.game.chase.data.joke.Joke
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.data.game.db.model.Score
import com.game.chase.domain.game.GameState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.game.chase.domain.game.GameInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

interface GameViewModelInterface {
    val gameState: StateFlow<GameState>
    val topScores: StateFlow<List<Score>>
    val showEndOfGameDialog: StateFlow<Boolean>
    fun movePlayer(direction: Direction)
    fun teleportPlayer()
    fun useBomb()
    fun startNewGame()
    fun saveScore(score: Int)
    fun fetchTopScores()
    fun getLatestScore()
    fun dismissEndOfGameDialog()
    fun fetchJoke()
}

@HiltViewModel
class GameViewModel @Inject constructor(
    private val gameInteractor: GameInteractor
) : ViewModel(), GameViewModelInterface {

    private val _gameState = MutableStateFlow(gameInteractor.startNewGame())
    override val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _topScores = MutableStateFlow<List<Score>>(emptyList())
    override val topScores: StateFlow<List<Score>> = _topScores.asStateFlow()

    private val _showEndOfGameDialog = MutableStateFlow(false)
    override val showEndOfGameDialog: StateFlow<Boolean> = _showEndOfGameDialog

    private val _joke = MutableStateFlow<Joke?>(null)
    val joke: StateFlow<Joke?> = _joke

    private val _latestScore = MutableStateFlow<Score?>(null)
    val latestScore: StateFlow<Score?> = _latestScore

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
                LogX.d(TAG, "Player Died; ${oldPlayer.lives} lives remaining")
                // decrement a life and reset the level
                val newPlayerPosition = gameInteractor.getPlayerStartPosition()
                val newState = GameState(
                    player = oldPlayer.copy(position = newPlayerPosition),
                    enemies = gameInteractor.generateEnemies(nextGameState.level, newPlayerPosition).toMutableList(),
                    collisionSquares = mutableListOf(),
                    score = nextGameState.score,
                    level = nextGameState.level
                )
                _gameState.update { newState }
            } else {
                // Player has no lives left, save the score and end the game
                LogX.d(TAG, "Player Died. Game Over")
                withContext(Dispatchers.IO) {
                    // withContext changes the context of the existing coroutine; everything inside will run concurrently within that coroutine
                    saveScore(nextGameState.score)
                    getLatestScore()
                    fetchTopScores()
                    _gameState.update { nextGameState.copy(player = oldPlayer) }
                    _showEndOfGameDialog.update { true }
                    LogX.d(TAG, "GameState: $_gameState")
                }
            }
        } else if (nextGameState.enemies.isEmpty()) {
            // Add bonuses
            when
                (nextGameState.level % 3 == 0) {
                    true -> nextGameState.player.bombUses++
                    false -> nextGameState.player.teleportUses++
                }

            _gameState.update { gameInteractor.nextLevel(nextGameState) }
        } else {
            _gameState.update { nextGameState }
        }

    }

    override fun movePlayer(direction: Direction) {
        viewModelScope.launch {
            _gameState.update { oldGameState ->
                val newGameState = gameInteractor.movePlayer(oldGameState, direction)

                // Only update enemies if the player's position has changed
                if (oldGameState.player.position != newGameState.player.position) {
                    viewModelScope.launch {
                        LogX.d(TAG, "Player Moved; Updating game state")
                        processGameState(newGameState)
                    }
                }
                newGameState
            }
        }
    }

    override fun teleportPlayer() {
        viewModelScope.launch {
            _gameState.update { oldGameState ->
                // Only update enemies if the player's position has changed
                if (oldGameState.player.teleportUses > 0) {
                    val newGameState = gameInteractor.teleportPlayer(oldGameState)
                    viewModelScope.launch {
                        LogX.d(TAG, "Teleport Used; Updating game state")
                        processGameState(newGameState)
                    }
                }
                oldGameState
            }
        }
    }

    override fun useBomb() {
        viewModelScope.launch {
            _gameState.update { oldGameState ->
                // Only update enemies if the player's position has changed
                if (oldGameState.player.bombUses > 0) {
                    val newGameState = gameInteractor.useBomb(oldGameState)
                    viewModelScope.launch {
                        LogX.d(TAG, "Bomb used; Updating Game state")
                        processGameState(newGameState)
                    }
                }
                oldGameState
            }
        }
    }

    override fun startNewGame() {
        _gameState.update { gameInteractor.startNewGame() }
        viewModelScope.launch {
            LogX.d(TAG, "Fetching new Joke")
            fetchJoke()
        }
    }

    override fun saveScore(score: Int) {
        viewModelScope.launch {
            gameInteractor.insertScore(score)
        }
    }

    override fun fetchTopScores() {
        viewModelScope.launch {
            _topScores.update { gameInteractor.getTopScores(10) }
        }
    }

    override fun getLatestScore() {
        viewModelScope.launch {
            _latestScore.update {
                val score = gameInteractor.getLatestScore()
                LogX.d(TAG, "Latest Score Retrieved: $score")
                score
            }
        }
    }

    override fun dismissEndOfGameDialog() {
        _showEndOfGameDialog.update {
            LogX.d(TAG, "End of Game dialog Dismissed")
            false
        }
    }

    override fun fetchJoke() {
        viewModelScope.launch {
            val joke = withContext(Dispatchers.IO) {
                gameInteractor.fetchJoke()
            }
            _joke.update {
                LogX.d(TAG, "New Joke: $joke")
                joke
            }
        }
    }

    companion object {
        private const val TAG = "GAME_LOGIC"
    }
}


class MockGameViewModel : ViewModel(), GameViewModelInterface {
    override val gameState = MutableStateFlow(
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

    private val _topScores = MutableStateFlow<List<Score>>(emptyList())
    override val topScores: StateFlow<List<Score>> = _topScores.asStateFlow()

    private val _showEndOfGameDialog = MutableStateFlow(false)
    override val showEndOfGameDialog: StateFlow<Boolean> = _showEndOfGameDialog.asStateFlow()

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

    override fun getLatestScore() {
        // Mock implementation
    }

    override fun dismissEndOfGameDialog() {
        // Mock implementation
    }

    override fun fetchJoke() {
        // Mock implementation
    }
}