package com.game.chase

class GameViewModelTest {

//    private lateinit var gameRepository: GameRepository
//    private lateinit var gameViewModel: GameViewModel
//
//    @Before
//    fun setup() {
//        gameRepository = Mockito.mock(GameRepository::class.java)
//        gameViewModel = GameViewModel(gameRepository)
//    }
//
//    @Test
//    fun testMovePlayer1(): Unit = runBlockingTest {
//        val initialGameState = GameState(
//            player = Player(Position(0, 0)),
//            enemies = mutableListOf(),
//            collisionSquares = listOf(),
//            score = 0,
//            level = 1
//        )
//        gameViewModel.setLiveDataForTesting(initialGameState)
//
//        gameViewModel.movePlayer(Direction.RIGHT)
//
//        val updatedGameState = gameViewModel.gameState.value
//        assert(updatedGameState?.player?.position?.x == 1)
//        assert(updatedGameState?.player?.position?.y == 0)
//    }
//
//    @Test
//    fun testMovePlayer() = runBlockingTest {
//        // Arrange
//        val initialPosition = Position(0, 0)
//        val expectedPosition = Position(1, 0)
//        val player = Player(initialPosition)
//        val initialGameState = GameState(player = player, enemies = mutableListOf(), collisionSquares = listOf(), score = 0, level = 1)
//        gameViewModel.setLiveDataForTesting(initialGameState)
//
//        // Act
//        gameViewModel.movePlayer(Direction.RIGHT)
//
//        // Assert
//        val updatedGameState = gameViewModel.gameState.value
//        assertEquals(expectedPosition, updatedGameState?.player?.position)
//    }
//
//
//    @Test
//    fun testTeleportPlayer() = runBlockingTest {
//        val initialGameState = GameState(
//            player = Player(Position(0, 0), teleportUses = 1),
//            enemies = mutableListOf(),
//            collisionSquares = listOf(),
//            score = 0,
//            level = 1
//        )
//        gameViewModel.setLiveDataForTesting(initialGameState)
//
//        gameViewModel.teleportPlayer()
//
//        val updatedGameState = gameViewModel.gameState.value
//        assert(updatedGameState?.player?.teleportUses == 0)
//    }
//
//    @Test
//    fun testUseBomb() = runBlockingTest {
//        val initialGameState = GameState(
//            player = Player(Position(0, 0), bombUses = 1),
//            enemies = mutableListOf(),
//            collisionSquares = listOf(),
//            score = 0,
//            level = 1
//        )
//        gameViewModel.setLiveDataForTesting(initialGameState)
//
//        gameViewModel.useBomb()
//
//        val updatedGameState = gameViewModel.gameState.value
//        assert(updatedGameState?.player?.bombUses == 0)
//    }
//
//    @Test
//    fun testResetLevel() = runBlockingTest {
//        val initialGameState = GameState(
//            player = Player(Position(0, 0), lives = 2),
//            enemies = mutableListOf(),
//            collisionSquares = listOf(),
//            score = 0,
//            level = 1
//        )
//        gameViewModel.setLiveDataForTesting(initialGameState)
//
//        gameViewModel.resetLevel()
//
//        val updatedGameState = gameViewModel.gameState.value
//        assert(updatedGameState?.player?.lives == 1)
//    }
//
//    @Test
//    fun testStartNewGame() = runBlockingTest {
//        gameViewModel.startNewGame()
//
//        val updatedGameState = gameViewModel.gameState.value
//        assert(updatedGameState?.player?.lives == 3)
//        assert(updatedGameState?.player?.teleportUses == 3)
//        assert(updatedGameState?.player?.bombUses == 2)
//        assert(updatedGameState?.score == 0)
//        assert(updatedGameState?.level == 1)
//    }
//
//    @Test
//    fun testSaveScore() = runBlockingTest {
//        gameViewModel.saveScore(100)
//
//        verify(gameRepository).insertScore(Score(points = 100))
//    }


}

