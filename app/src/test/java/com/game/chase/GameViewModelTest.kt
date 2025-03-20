package com.game.chase

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.any
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.kotlin.argThat
import kotlin.math.ceil

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

//    // Executes each task synchronously using Architecture Components.
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    // Set the Main dispatcher for coroutines.
//    @get:Rule
//    val mainDispatcherRule = MainDispatcherRule()
//
//    private lateinit var gameRepository: GameRepository
//    private lateinit var positionGenerator: PositionGenerator
//    private lateinit var gameInteractor: GameInteractor
//    private lateinit var gameViewModel: GameViewModel
//
//    private val defaultPlayerPosition = androidx.room.parser.expansion.Position(5, 5)
//    private val defaultEnemyList = mutableListOf(
//        Enemy(androidx.room.parser.expansion.Position(1, 1)), Enemy(
//        androidx.room.parser.expansion.Position(2, 2)))
//    private val GRID_WIDTH = 10
//    private val GRID_HEIGHT = 10
//
//    @Before
//    fun setup() {
//        gameRepository = org.mockito.Mockito.mock(GameRepository::class.java)
//        positionGenerator = org.mockito.Mockito.mock(PositionGenerator::class.java)
//        gameInteractor = GameInteractor(gameRepository, positionGenerator)
//        gameViewModel = GameViewModel(gameInteractor)
//    }
//
//    @Test
//    fun testTeleportPlayer_DoesNotMovePlayerOntoEnemySquare() = runTest {
//        //Arrange
//        val enemyPosition = androidx.room.parser.expansion.Position(1, 1)
//        val expectedFinalPlayerPosition = androidx.room.parser.expansion.Position(
//            5,
//            5
//        ) // Must be far enough away from enemies to not cause a collision when they update
//        val specificPositions = listOf(enemyPosition, expectedFinalPlayerPosition)
//        positionGenerator = SpecificPositionGenerator(specificPositions)
//    }





    //region handlePlayerCollision Tests
//    @Test
//    fun testHandlePlayerCollision_DecreasesPlayerLives() = runTest {
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition, lives = 3),
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf()
//        )
//
//        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)
//
//        assertEquals(2, resultGameState.player.lives)
//    }
//
//    @Test
//    fun testHandlePlayerCollision_ResetsPlayerPosition() = runTest {
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition),
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf()
//        )
//
//        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)
//
//        assertEquals(Position(ceil((com.game.chase.core.constants.GRID_WIDTH / 2).toDouble()).toInt(), ceil((com.game.chase.core.constants.GRID_HEIGHT / 2).toDouble()).toInt()), resultGameState.player.position)
//    }
//
//    @Test
//    fun testHandlePlayerCollision_CallsGenerateEnemiesWhenPlayerHasLivesRemaining() = runTest {
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition, lives = 3),
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf()
//        )
//        val spyInteractor = spy(gameInteractor)
//
//        //Act
//        spyInteractor.handlePlayerCollision(initialGameState)
//
//        //Assert
//        verify(spyInteractor).generateEnemies(org.mockito.kotlin.any(), org.mockito.kotlin.any())
//    }
//
//    @Test
//    fun testHandlePlayerCollision_ResetsCollisionSquares() = runTest {
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition),
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf(Position(1, 1), Position(2, 2))
//        )
//
//        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)
//
//        assertTrue(resultGameState.collisionSquares.isEmpty())
//    }
//
//    @Test
//    fun testHandlePlayerCollision_KeepsScoreAndLevel() = runTest {
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition),
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf(),
//            score = 100,
//            level = 5
//        )
//
//        val resultGameState = gameInteractor.handlePlayerCollision(initialGameState)
//
//        assertEquals(100, resultGameState.score)
//        assertEquals(5, resultGameState.level)
//    }
//
//    @Test
//    fun testHandlePlayerCollision_CallsInsertScoreWithCorrectScoreWhenNoLivesRemain() = runTest {
//        // Arrange
//        val initialScore = 100
//        val initialGameState = GameState(
//            player = Player(defaultPlayerPosition, lives = 0), // Player has no lives
//            enemies = defaultEnemyList,
//            collisionSquares = mutableListOf(),
//            score = initialScore // Set the initial score
//        )
//        val gameInteractor = GameInteractor(gameRepository, positionGenerator)
//
//        // Act
//        gameInteractor.handlePlayerCollision(initialGameState)
//
//        // Assert
//        verify(gameRepository).insertScore(argThat { score -> score.points == initialScore}) // Verify with the correct score (but ignore other fields in the object)
//    }
    //endregion
}

