package com.game.chase

import com.game.chase.core.constants.Direction
import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.data.Enemy
import com.game.chase.data.GameRepository
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.domain.game.GameInteractor
import com.game.chase.domain.game.GameState
import com.game.chase.domain.game.util.PositionGenerator
import com.game.chase.domain.game.util.impl.SpecificPositionGenerator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.any

class GameInteractorTest {
    /*
     * NOTE: 0,0 is the top left corner of the grid!
     */

    private lateinit var gameRepository: GameRepository
    private lateinit var positionGenerator: PositionGenerator
    private lateinit var gameInteractor: GameInteractor

    private val defaultPlayerPosition = Position(GRID_SIZE / 2, GRID_SIZE / 2)
    private val defaultEnemyList =  mutableListOf(
        Enemy(Position(defaultPlayerPosition.x + 3, defaultPlayerPosition.y)),
        Enemy(Position(defaultPlayerPosition.x + 5, defaultPlayerPosition.y - 3)),
        Enemy(Position(defaultPlayerPosition.x, defaultPlayerPosition.y + 3)),
    )
    @Before
    fun setup() {
        gameRepository = mock(GameRepository::class.java)
        positionGenerator = SpecificPositionGenerator(listOf(Position(0, 0), Position(1, 1), Position(2, 2)))
        gameInteractor = GameInteractor(gameRepository, positionGenerator)
    }

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
    @Test
    fun testTeleportPlayer_DoesMovePlayer() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 1),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        assertNotEquals(defaultPlayerPosition, resultGameState.player.position) // Player moved
    }
    @Test
    fun testTeleportPlayer_DoesNotTeleportPlayerIfNoTeleportsRemain() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 0),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        assertEquals(initialGameState, resultGameState)
    }
    @Test
    fun testTeleportPlayer_DoesNotMovePlayerOntoEnemySquare() = runTest {
        //Arrange
        val enemyPosition = Position(1, 1)
        val expectedFinalPlayerPosition = Position(5, 5) // Must be far enough away from enemies to not cause a collision when they update
        val specificPositions = listOf(enemyPosition, expectedFinalPlayerPosition)
        positionGenerator = SpecificPositionGenerator(specificPositions)
        gameInteractor = GameInteractor(gameRepository, positionGenerator)

        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 1),
            enemies = mutableListOf(Enemy(enemyPosition)),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        //Assert
        assertNotEquals(defaultPlayerPosition, resultGameState.player.position) // Player moved
        assertNotEquals(enemyPosition, resultGameState.player.position) // Player is not on Enemy square
        assertEquals(expectedFinalPlayerPosition, resultGameState.player.position) // Player is on expected square
    }
    @Test
    fun testTeleportPlayer_DoesNotMovePlayerOntoCollisionSquare() = runTest {
        val collisionPosition = Position(1, 1)
        val expectedFinalPlayerPosition = Position(5, 5) // Must be far enough away from enemies to not cause a collision when they update
        val specificPositions = listOf(collisionPosition, expectedFinalPlayerPosition)
        positionGenerator = SpecificPositionGenerator(specificPositions)
        gameInteractor = GameInteractor(gameRepository, positionGenerator)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 1),
            enemies = defaultEnemyList, // may need to update later to avoid collisions
            collisionSquares = mutableListOf(collisionPosition)
        )

        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        //Assert
        assertNotEquals(defaultPlayerPosition, resultGameState.player.position) // Player moved
        assertNotEquals(collisionPosition, resultGameState.player.position) // Player is not on Collision square
        assertEquals(expectedFinalPlayerPosition, resultGameState.player.position) // Player is on expected square
    }
    @Test
    fun testTeleportPlayer_UpdateEnemiesIsCalledAfterTeleport() = runTest {
        val spyInteractor = spy(gameInteractor)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 1),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        spyInteractor.teleportPlayer(initialGameState)

        verify(spyInteractor).updateEnemies(any())
    }
    @Test
    fun testTeleportPlayer_ChangesPlayerPosition() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, teleportUses = 1),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )
        //TODO: need to force where the player teleports to to it isn't right next to the enemy
        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        assertNotEquals(initialGameState.player.position, resultGameState.player.position)
    }
    @Test
    fun testTeleportPlayer_TeleportsRemainingGetsDecremented() = runTest {
        val initialTeleportUses = 2
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  teleportUses = initialTeleportUses),
            enemies = mutableListOf(Enemy(Position(0, 0))),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.teleportPlayer(initialGameState)

        assertEquals(initialTeleportUses - 1, resultGameState.player.teleportUses)
    }
    // endregion

    //region useBomb Tests
    @Test
    fun testUseBomb_DoesNotChangeStateIfNoBombsRemain() = runTest {
        val initialEnemyPosition1 = Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)
        val initialEnemyPosition2 = Position(defaultPlayerPosition.x - 1, defaultPlayerPosition.y -1)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 0),
            enemies = mutableListOf(
                Enemy(initialEnemyPosition1),
                Enemy(initialEnemyPosition2),
                Enemy(Position(0, 0))
            ),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        assertEquals(initialGameState, resultGameState)
    }

    @Test
    fun testUseBomb_BombsRemainingGetsDecremented() = runTest {
        val initialBombUses = 2
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = initialBombUses),
            enemies = mutableListOf(Enemy(Position(0, 0))),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        assertEquals(initialBombUses - 1, resultGameState.player.bombUses)
    }

    @Test
    fun testUseBomb_ReplacesAllEnemiesWithinRangeWithCollisionSquares() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x - 1, defaultPlayerPosition.y -1))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(
                enemy1,
                enemy2,
                Enemy(Position(0, 0))
            ),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        // each of the enemy positions within range is now a collision square
        assertTrue(resultGameState.collisionSquares.containsAll(listOf(enemy1.position, enemy2.position)))

        // all enemies within radius are removed from enemies list
        assertTrue(resultGameState.enemies.size == 1)
        assertFalse(resultGameState.enemies.contains(enemy1))
        assertFalse(resultGameState.enemies.contains(enemy2))
    }

    @Test
    fun testUseBomb_BombDoesNotRemoveEnemiesOutsideRadius() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(
                enemy1,
                enemy2
            ),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        // all enemies outside the radius remain in enemies list
        assertTrue(resultGameState.enemies.size == 1)
        assertFalse(resultGameState.enemies.contains(enemy1))
    }

    @Test
    fun testUseBomb_BombDoesNotRemovePlayerOrChangePlayerPosition() = runTest {
        val enemy1 = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(
                enemy1
            ),
            collisionSquares = mutableListOf()
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        assertTrue(resultGameState.enemies.size == 1)
        assertEquals(initialGameState.player.position, resultGameState.player.position)
    }

    @Test
    fun testUseBomb_BombDoesNotRemoveCollisionSquares() = runTest {
        val enemy1 = Enemy(Position(0, 0))
        val collisionSquare1 = Position(4, 1)
        val collisionSquare2 = Position(1, 12)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(
                enemy1
            ),
            collisionSquares = mutableListOf(
                collisionSquare1,
                collisionSquare2
            )
        )

        //Act
        val resultGameState = gameInteractor.useBomb(initialGameState)

        assertTrue(resultGameState.enemies.size == 1)
        assertEquals(initialGameState.player.position, resultGameState.player.position)
        assertTrue(resultGameState.collisionSquares.containsAll(initialGameState.collisionSquares))
    }

    @Test
    fun testUseBomb_CallsUpdateEnemiesWhenFinished() = runTest {
        val enemy1 = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(
                enemy1
            ),
            collisionSquares = mutableListOf()
        )
        val spyInteractor = spy(gameInteractor)

        //Act
        spyInteractor.useBomb(initialGameState)

        //Assert
        verify(spyInteractor).updateEnemies(any())
    }

    @Test
    fun testUseBomb_NextLevelIsCalledIfNoEnemiesRemain() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(enemy1),
            collisionSquares = mutableListOf()
        )
        val spyInteractor = spy(gameInteractor)

        //Act
        spyInteractor.useBomb(initialGameState)

        //Assert
        verify(spyInteractor).nextLevel(any())
    }

    /* TODO: Add tests for:
        bombs remaining gets decremented

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