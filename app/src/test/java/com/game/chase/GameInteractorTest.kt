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
import org.mockito.kotlin.atLeast
import org.mockito.kotlin.check

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

    @Test
    fun testUseBomb_AddsScoreForEnemyBombed() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(enemy1),
            collisionSquares = mutableListOf(),
            score = 0
        )

        // Create a spy of gameInteractor
        val spyInteractor = spy(gameInteractor)

        // Act
        spyInteractor.useBomb(initialGameState)

        // Assert
        // Verify that updateEnemies was called with a GameState where score = 1
        verify(spyInteractor).updateEnemies(check<GameState> { gameState ->
            assertEquals(1, gameState.score) // This works even though a new level is started form lack of enemies
        })
    }

    //endregion

    //region updateEnemies Tests
    @Test
    fun testUpdateEnemies_MovesEnemiesCloserToPlayer() = runTest {
        val enemyDownRight = Enemy(Position(defaultPlayerPosition.x + 4, defaultPlayerPosition.y + 4))
        val enemyDown = Enemy(Position(defaultPlayerPosition.x + 4, defaultPlayerPosition.y))
        val enemyRight = Enemy(Position(defaultPlayerPosition.x , defaultPlayerPosition.y + 4))
        val enemyUpLeft = Enemy(Position(defaultPlayerPosition.x - 4, defaultPlayerPosition.y - 4))
        val enemyLeft = Enemy(Position(defaultPlayerPosition.x - 4, defaultPlayerPosition.y))
        val enemyUp = Enemy(Position(defaultPlayerPosition.x, defaultPlayerPosition.y - 4))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(enemyDownRight, enemyDown, enemyRight, enemyUpLeft, enemyLeft, enemyUp),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.updateEnemies(initialGameState)

        // Assert the enemies on each side of the player moved towards the player correctly
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x + 3, defaultPlayerPosition.y + 3) })
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x + 3, defaultPlayerPosition.y) })
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x, defaultPlayerPosition.y + 3) })
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x - 3, defaultPlayerPosition.y - 3) })
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x - 3, defaultPlayerPosition.y) })
        assertTrue(updatedGameState.enemies.any { it.position == Position(defaultPlayerPosition.x, defaultPlayerPosition.y - 3) })
    }

    @Test
    fun testUpdateEnemies_RemovesEnemiesOnCollisionSquares() = runTest {

        //straight down + off by 1
        val enemyDownRight = Enemy(Position(defaultPlayerPosition.x + 4, defaultPlayerPosition.y + 1))
        val enemyDown = Enemy(Position(defaultPlayerPosition.x + 4, defaultPlayerPosition.y))
        val enemyThatSurvives = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition,  bombUses = 2),
            enemies = mutableListOf(enemyDownRight, enemyDown, enemyThatSurvives),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.updateEnemies(initialGameState)

        // Assert
        assertTrue(updatedGameState.enemies.size == 1)
        assertTrue(updatedGameState.collisionSquares.size == 1)
        assertTrue(updatedGameState.collisionSquares.contains(Position(defaultPlayerPosition.x + 3, defaultPlayerPosition.y)))
    }

    @Test
    fun testUpdateEnemies_CallsDetectCollisions() = runTest {
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
        spyGameInteractor.detectCollisions(initialGameState)

        // Assert that detectCollisions was called
        verify(spyGameInteractor).detectCollisions(any())
    }

    //endregion

    //region detectCollision Tests

    @Test
    fun testDetectCollisions_CallsHandlePlayerCollisionOnPlayerCollision() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x, defaultPlayerPosition.y))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1),
            collisionSquares = mutableListOf()
        )
        // Create a spy of gameInteractor
        val spyGameInteractor = spy(gameInteractor)

        // Act
        spyGameInteractor.handlePlayerCollision(initialGameState)

        // Assert that handlePlayerCollision was called
        verify(spyGameInteractor).handlePlayerCollision(any())
    }

    @Test
    fun testDetectCollisions_RemovesEnemyOnCollisionSquare() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemyToSurvive = Enemy(Position(0, 0))
        val collisionSquare = Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemyToSurvive),
            collisionSquares = mutableListOf(collisionSquare)
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertTrue(updatedGameState.enemies.size == 1)
        assertTrue(updatedGameState.collisionSquares.contains(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)))
    }

    @Test
    fun testDetectCollisions_RemovesEnemiesOnEnemyCollision() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemyToSurvive = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2, enemyToSurvive),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertTrue(updatedGameState.enemies.size == 1)
        assertFalse(updatedGameState.enemies.contains(enemy1))
        assertFalse(updatedGameState.enemies.contains(enemy2))
    }

    @Test
    fun testDetectCollisions_AddsCollisionSquareOnEnemyCollision() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemyToSurvive = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2, enemyToSurvive),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertTrue(updatedGameState.collisionSquares.size == 1)
        assertTrue(updatedGameState.collisionSquares.contains(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)))
    }

    @Test
    fun testDetectCollisions_DoesNotRemoveCollisionSquaresWhenCollided() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemyToSurvive = Enemy(Position(0, 0))
        val collisionSquare = Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemyToSurvive),
            collisionSquares = mutableListOf(collisionSquare)
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertTrue(updatedGameState.collisionSquares.size == 1)
        assertTrue(updatedGameState.collisionSquares.contains(collisionSquare))
    }

    @Test
    fun testDetectCollisions_CallsNextLevelWhenNoEnemiesRemain() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2),
            collisionSquares = mutableListOf()
        )
        val spyInteractor = spy(gameInteractor)

        //Act
        spyInteractor.detectCollisions(initialGameState)

        //Assert
        verify(spyInteractor).nextLevel(any())
    }

    @Test
    fun testDetectCollisions_AddsScoreForEnemyRunningIntoCollisionSquare() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val survivingEnemy = Enemy(Position(0, 0)) // Keeps level from completing; simpler than spying everything
        val collisionSquare = Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y)
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, survivingEnemy),
            collisionSquares = mutableListOf(collisionSquare),
            score = 0
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertEquals(1, updatedGameState.score)
    }

    @Test
    fun testDetectCollisions_AddsScoreForTwoEnemiesColliding() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val survivingEnemy = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2, survivingEnemy),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertEquals(3, updatedGameState.score)
    }

    @Test
    fun testDetectCollisions_AddsScoreForThreeEnemiesColliding() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy3 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val survivingEnemy = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2, enemy3, survivingEnemy),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertEquals(5, updatedGameState.score)
    }

    @Test
    fun testDetectCollisions_AddsScoreForOtherEnemiesCollidingWhenPlayerIsCollided() = runTest {
        val enemy1 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy2 = Enemy(Position(defaultPlayerPosition.x + 1, defaultPlayerPosition.y))
        val enemy3 = Enemy(Position(defaultPlayerPosition.x, defaultPlayerPosition.y))
        val survivingEnemy = Enemy(Position(0, 0))
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(enemy1, enemy2, enemy3, survivingEnemy),
            collisionSquares = mutableListOf()
        )

        val updatedGameState = gameInteractor.detectCollisions(initialGameState)

        assertEquals(3, updatedGameState.score)
    }

    //endregion

    //region handlePlayerCollision Tests
    @Test
    fun testHandlePlayerCollision_DecreasesPlayerLives() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, lives = 3),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)

        assertEquals(2, resultGameState.player.lives)
    }

    @Test
    fun testHandlePlayerCollision_ResetsPlayerPosition() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)

        assertEquals(Position(GRID_SIZE / 2, GRID_SIZE / 2), resultGameState.player.position)
    }

    @Test
    fun testHandlePlayerCollision_CallsGenerateEnemiesWhenPlayerHasLivesRemaining() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, lives = 3),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )
        val spyInteractor = spy(gameInteractor)

        //Act
        spyInteractor.handlePlayerCollision(initialGameState)

        //Assert
        verify(spyInteractor).generateEnemies(any(), any())
    }

    @Test
    fun testHandlePlayerCollision_ResetsCollisionSquares() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf(Position(1, 1), Position(2, 2))
        )

        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)

        assertTrue(resultGameState.collisionSquares.isEmpty())
    }

    @Test
    fun testHandlePlayerCollision_KeepsScoreAndLevel() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf(),
            score = 100,
            level = 5
        )

        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)

        assertEquals(100, resultGameState.score)
        assertEquals(5, resultGameState.level)
    }

    @Test
    fun testHandlePlayerCollision_CallsStartNewGameWhenNoLivesRemain() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, lives = 1),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )
        val spyInteractor = spy(gameInteractor)

        //Act
        spyInteractor.handlePlayerCollision(initialGameState)

        //Assert
        verify(spyInteractor).startNewGame()
    }
    //endregion

    //region nextLevelTests
    @Test
    fun testNextLevel_IncreasesLevelByOne() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf(),
            level = 1
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        assertEquals(2, resultGameState.level)
    }

    @Test
    fun testNextLevel_ResetsPlayerPosition() = runTest {
        val initialGameState = GameState(
            player = Player(position = Position(5, 7)),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        assertEquals(Position(GRID_SIZE / 2, GRID_SIZE / 2), resultGameState.player.position)
    }

    @Test
    fun testNextLevel_GeneratesNewEnemies() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = mutableListOf(),
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        assertTrue(resultGameState.enemies.size > 0)
    }

    @Test
    fun testNextLevel_ResetsCollisionSquares() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf(Position(1, 1), Position(2, 2))
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        assertTrue(resultGameState.collisionSquares.isEmpty())
    }

    @Test
    fun testNextLevel_KeepsPlayerLives() = runTest {
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition, lives = 4),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf()
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        assertEquals(4, resultGameState.player.lives)
    }

    @Test
    fun testNextLevel_KeepsScore() = runTest {
        val initialScore = 100
        val initialLevel = 1
        val initialGameState = GameState(
            player = Player(defaultPlayerPosition),
            enemies = defaultEnemyList,
            collisionSquares = mutableListOf(),
            score = initialScore,
            level = initialLevel
        )

        val resultGameState = gameInteractor.nextLevel(initialGameState)

        val expectedScore = initialScore + (initialLevel * 3)
        assertEquals(expectedScore, resultGameState.score)
    }




    //endregion

    //region startNewGame Tests
    // Skipping this test as it is a simple function that does not require testing at this point.
    //endregion

    //region generateEnemies Tests
    @Test
    fun testGenerateEnemies_CreatesCorrectNumberOfEnemies() = runTest {
        val level = 3
        val enemies = gameInteractor.generateEnemies(level, defaultPlayerPosition)
        assertEquals(level + 2, enemies.size)
    }

    @Test
    fun testGenerateEnemies_CreatesEnemiesAtDifferentPositions() = runTest {
        val level = 3
        val enemies = gameInteractor.generateEnemies(level, defaultPlayerPosition)
        val enemyPositions = enemies.map { it.position }
        val distinctPositions = enemyPositions.distinct()
        assertEquals(enemyPositions.size, distinctPositions.size)
    }

    @Test
    fun testGenerateEnemies_CallsIsWithinRadius() = runTest {
        val spyInteractor = spy(gameInteractor)
        val level = 3
        spyInteractor.generateEnemies(level, defaultPlayerPosition)
        verify(spyInteractor, atLeast(level + 2)).isWithinRadius(any(), any(), any())
    }

    @Test
    fun testGenerateEnemies_IncreasesEnemyCountWithLevel() = runTest {
        val level = 5
        val enemies = gameInteractor.generateEnemies(level, defaultPlayerPosition)
        assertEquals(level + 2, enemies.size)
    }
    //endregion

    //region isWithinRadius Tests
    @Test
    fun testIsWithinRadius_ReturnsTrueWhenPositionsAreWithinRadius() = runTest {
        val position1 = Position(0, 0)
        val position2 = Position(1, 1)
        val radius = 2
        val result = gameInteractor.isWithinRadius(radius, position1, position2)
        assertTrue(result)
    }

    @Test
    fun testIsWithinRadius_ReturnsFalseWhenPositionsAreOutsideRadius() = runTest {
        val position1 = Position(10, 10)
        val position2 = Position(3, 3)
        val radius = 2
        val result = gameInteractor.isWithinRadius(radius, position1, position2)
        assertFalse(result)
    }

    @Test
    fun testIsWithinRadius_ReturnsTrueWhenPositionsAreOnRadiusBoundary() = runTest {
        val position1 = Position(0, 0)
        val position2 = Position(2, 2)
        val radius = 2
        val result = gameInteractor.isWithinRadius(radius, position1, position2)
        assertTrue(result)
    }

    @Test
    fun testIsWithinRadius_ReturnsFalseWhenPositionsAreJustOutsideRadiusBoundary() = runTest {
        val position1 = Position(0, 0)
        val position2 = Position(3, 3)
        val radius = 2
        val result = gameInteractor.isWithinRadius(radius, position1, position2)
        assertFalse(result)
    }
    //endregion
}