package com.game.chase.presentation.game

import GameGrid
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.ui.theme.AppTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.game.chase.domain.game.GameState

@Composable
fun GameScreen(navController: NavHostController, modifier: Modifier = Modifier, viewModel: GameViewModelInterface = hiltViewModel<GameViewModel>()) {
    AppTheme {
        // Observe LiveData from the viewModel
        val gameState = viewModel.gameState.observeAsState().value
        val topScores = viewModel.topScores.observeAsState()
        val showEndOfGameDialog = viewModel.showEndOfGameDialog.observeAsState()

        // Observe StateFlow from the ViewModel
        val joke = (viewModel as GameViewModel).joke.collectAsState().value

        // Call destroy method of GameInteractor when GameScreen leaves the composition
        DisposableEffect(key1 = viewModel) {
            onDispose {
                (viewModel as? GameViewModel)?.destroy()
            }
        }

        if (showEndOfGameDialog.value == true) {
            EndOfGameDialog(
                score = gameState?.score?.toString() ?: "",
                topScores = topScores.value ?: emptyList(),
                joke = joke,
                onDismiss = {
                    viewModel.dismissEndOfGameDialog()
                    viewModel.startNewGame()
                }
            )
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            GameGrid(
                modifier = Modifier
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                gameState = gameState)
            ControlModule(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .height(100.dp),
                onMove = { direction -> viewModel.movePlayer(direction) },
                onTeleport = { viewModel.teleportPlayer() },
                onBomb = { viewModel.useBomb() },
                onNewGame = { viewModel.startNewGame() },
                gameState = gameState
            )
        }
    }
}



@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun GameScreenPreview() {
    AppTheme {
        val mockNavController = rememberNavController()
        GameScreen(navController = mockNavController, viewModel = MockGameViewModel())
    }
}
