package com.game.chase.presentation.game

import GameGrid
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.ui.theme.MyApplicationTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text


@Composable
fun GameScreen(navController: NavHostController, modifier: Modifier = Modifier, viewModel: GameViewModelInterface = hiltViewModel<GameViewModel>()) {
    // Assuming GameState is obtained from the ViewModel
    val gameState = viewModel.gameState //TODO does this need .observeAsState()  ?
    val topScores = viewModel.topScores.observeAsState()

    // Call destroy method of GameInteractor when GameScreen leaves the composition
    DisposableEffect(key1 = viewModel) {
        onDispose {
            (viewModel as? GameViewModel)?.destroy()
        }
    }

    if (gameState.value?.player?.lives == 0) {
        viewModel.fetchTopScores()
        AlertDialog(
            onDismissRequest = { /*TODO: Handle on dismiss*/ },
            title = { Text(text = "Game Over") },
            text = {
                Column {
                    Text(text = "Top 10 Scores:")
                    topScores.value?.forEachIndexed { index, score ->
                        Text(text = "${index + 1}. ${score.points}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { /*TODO: Handle on confirm*/ }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameGrid(modifier = Modifier.weight(0.5f).fillMaxWidth(), gameState = gameState)
        ControlModule(modifier = Modifier.weight(0.5f).fillMaxWidth().height(100.dp),
            onMove = { direction -> viewModel.movePlayer(direction) },
            onTeleport = { viewModel.teleportPlayer() },
            onBomb = { viewModel.useBomb() },
            onNewGame = { viewModel.startNewGame() },
            gameState = gameState
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
