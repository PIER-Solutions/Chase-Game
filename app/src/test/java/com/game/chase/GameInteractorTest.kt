package com.game.chase

import com.game.chase.core.constants.Direction
import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.data.Enemy
import com.game.chase.data.GameRepository
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.domain.game.GameInteractor
import com.game.chase.domain.game.GameState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class GameInteractorTest {

    private lateinit var gameRepository: GameRepository
    private lateinit var gameInteractor: GameInteractor

    private val defaultPlayerPosition = Position(GRID_SIZE / 2, GRID_SIZE / 2)

    @Before
    fun setup() {
        gameRepository = mock(GameRepository::class.java)
        gameInteractor = GameInteractor(gameRepository)
    }

    /*
     * NOTE: 0,0 is the top left corner of the grid!
     */

    //region movePlayer Tests
    @Test
    fun testMovePlayer_Right() = runTest {
        // Arrange
        val defaultPlayerPos = defaultPlayerPosition
        val initialGameState = GameState(
            player = Player(defaultPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(defaultPlayerPos.x + 3, defaultPlayerPos.y)),
                Enemy(Position(defaultPlayerPos.x + 5, defaultPlayerPos.y - 3)),
                Enemy(Position(defaultPlayerPos.x, defaultPlayerPos.y + 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.RIGHT)

        // Assert
        assertEquals(Position(defaultPlayerPos.x + 1, defaultPlayerPos.y), updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_Left() = runTest {
        // Arrange
        val defaultPlayerPos = defaultPlayerPosition
        val initialGameState = GameState(
            player = Player(defaultPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(defaultPlayerPos.x + 3, defaultPlayerPos.y)),
                Enemy(Position(defaultPlayerPos.x + 5, defaultPlayerPos.y - 3)),
                Enemy(Position(defaultPlayerPos.x, defaultPlayerPos.y + 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.LEFT)

        // Assert
        assertEquals(Position(defaultPlayerPos.x - 1, defaultPlayerPos.y), updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_Up() = runTest {
        // Arrange
        val defaultPlayerPos = defaultPlayerPosition
        val initialGameState = GameState(
            player = Player(defaultPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(defaultPlayerPos.x + 3, defaultPlayerPos.y)),
                Enemy(Position(defaultPlayerPos.x + 5, defaultPlayerPos.y - 3)),
                Enemy(Position(defaultPlayerPos.x, defaultPlayerPos.y + 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.UP)

        // Assert
        assertEquals(Position(defaultPlayerPos.x, defaultPlayerPos.y - 1), updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_Down() = runTest {
        // Arrange
        val defaultPlayerPos = defaultPlayerPosition
        val initialGameState = GameState(
            player = Player(defaultPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(defaultPlayerPos.x + 3, defaultPlayerPos.y)),
                Enemy(Position(defaultPlayerPos.x + 5, defaultPlayerPos.y - 3)),
                Enemy(Position(defaultPlayerPos.x, defaultPlayerPos.y + 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.DOWN)

        // Assert
        assertEquals(Position(defaultPlayerPos.x, defaultPlayerPos.y + 1), updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_OutOfBounds_Left() = runTest {
        // Arrange
        val initialPlayerPos = Position(0,0)
        val initialGameState = GameState(
            player = Player(initialPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(3, 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.LEFT)

        // Assert
        assertEquals(initialPlayerPos, updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_OutOfBounds_Up() = runTest {
        // Arrange
        val initialPlayerPos = Position(0,0)
        val initialGameState = GameState(
            player = Player(initialPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(3, 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.UP)

        // Assert
        assertEquals(initialPlayerPos, updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_OutOfBounds_Down() = runTest {
        // Arrange
        val initialPlayerPos = Position(GRID_SIZE,GRID_SIZE)
        val initialGameState = GameState(
            player = Player(initialPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(3, 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.DOWN)

        // Assert
        assertEquals(initialPlayerPos, updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_OutOfBounds_Right() = runTest {
        // Arrange
        val initialPlayerPos = Position(GRID_SIZE,GRID_SIZE)
        val initialGameState = GameState(
            player = Player(initialPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(3, 3)),
            ),
            collisionSquares = mutableListOf()
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.RIGHT)

        // Assert
        assertEquals(initialPlayerPos, updatedGameState.player.position)
    }
    @Test
    fun testMovePlayer_OntoCollisionSquare() = runTest {
        // Arrange
        val collisionPosition = Position(1, 0)
        val initialPlayerPos = Position(0,0)
        val initialGameState = GameState(
            player = Player(initialPlayerPos), // Player is next to a collision square
            enemies = mutableListOf(
                Enemy(Position(3, 3)),
            ),
            collisionSquares = mutableListOf(collisionPosition) // Collision square is to the right of the player
        )

        // Act
        val updatedGameState = gameInteractor.movePlayer(initialGameState, Direction.RIGHT) // Attempt to move player onto collision square

        // Assert
        assertEquals(initialPlayerPos, updatedGameState.player.position) // Player position should remain the same
    }
    @Test
    fun testMovePlayer_CallsUpdateEnemies() = runTest {
        // Arrange
        val defaultPlayerPos = defaultPlayerPosition
        val initialGameState = GameState(
            player = Player(defaultPlayerPos),
            enemies = mutableListOf(
                Enemy(Position(defaultPlayerPos.x + 3, defaultPlayerPos.y)),
                Enemy(Position(defaultPlayerPos.x + 5, defaultPlayerPos.y - 2)),
                Enemy(Position(defaultPlayerPos.x - 2, defaultPlayerPos.y + 1)),
            ),
            collisionSquares = mutableListOf()
        )

        // Create a spy of gameInteractor
        val spyGameInteractor = spy(gameInteractor)

        // Act
        spyGameInteractor.movePlayer(initialGameState, Direction.RIGHT)

        // Assert
        // Verify that updateEnemies was called
        verify(spyGameInteractor).updateEnemies(any())
    }
    //endregion
    //region teleportPlayer Tests
        /*
        PlayerDoesNotTeleportIfNoTeleportsRemain
            - assert that player position, enemies, and collision squares remain the same
        DoesNotTeleportOntoEnemySquare
        DoesNotTeleportOntoCollisionSquare
        UpdateEnemiesIsCalledAfterTeleport
        TeleportChangesPlayerPosition
            - assert pre and post teleport positions are different

         */
    // endregion
    //region useBomb Tests
        /*
        PlayerDoesNotUseBombIfNoBombsRemain
            - assert that player position, enemies, and collision squares remain the same
        BombReplacesAllEnemiesWithinRadiusWithCollisionSquares
            - all enemies within radius are removed from enemies list
            - each of those positions is now a collision square
            - enemy positions didn't change? (requires mocking the response from updateEnemies)
        BombDoesNotRemoveEnemiesOutsideRadius
        BombDoesNotRemovePlayer
        BombDoesNotRemoveCollisionSquares
        UpdateEnemiesIsCalledAfterBomb
        PlayerPositionDoesNotChangeAfterBomb
         */

    //endregion
    //region updateEnemies Tests
        /*

         */
    //endregion
    //region detectCollision Tests

    //endregion
    //region handlePlayerCollision Tests

    //endregion
    //region nextLevelTests

    //endregion
    //region resetLevel Tests

    //endregion
    //region startNewGame Tests

    //endregion
    //region generateEnemies Tests

    //endregion
    //region isWithinRadius Tests

    //endregion
}