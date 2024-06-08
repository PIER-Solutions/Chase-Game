package com.game.chase.presentation.game

import GameGrid
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.presentation.ControlModule
import com.game.chase.ui.theme.MyApplicationTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun GameScreen(navController: NavHostController, modifier: Modifier = Modifier, viewModel: GameViewModelInterface = hiltViewModel<GameViewModel>()) {
    // Assuming GameState is obtained from the ViewModel
    val gameState = viewModel.gameState //TODO does this need .observeAsState()  ?

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameGrid(modifier = Modifier.weight(1f).fillMaxWidth(), gameState = gameState)
        ControlModule(modifier = Modifier.fillMaxWidth().height(100.dp),
            onMove = { direction -> viewModel.movePlayer(direction) },
            onTeleport = { viewModel.teleportPlayer() },
            onBomb = { viewModel.useBomb() },
            onNewGame = { viewModel.startNewGame() }
        )
    }
}



@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    MyApplicationTheme {

        val mockNavController = rememberNavController()

        GameScreen(navController = mockNavController, viewModel = MockGameViewModel())
    }
}
