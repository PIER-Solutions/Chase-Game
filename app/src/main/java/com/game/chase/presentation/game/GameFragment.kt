package com.game.chase.presentation.game

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.game.chase.presentation.ControlModule
import com.game.chase.ui.theme.MyApplicationTheme

class GameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyApplicationTheme {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        content = { innerPadding ->
                            GameScreen(modifier = Modifier.padding(innerPadding))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GameScreen(modifier: Modifier = Modifier) {
    val viewModel: GameViewModel = viewModel()
//    val gameState by viewModel.gameState.observeAsState()
    val gameState by viewModel.gameState.observeAsState()
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        GameGrid(modifier = Modifier.weight(1f).fillMaxWidth(), gameState = gameState)
        ControlModule(
            modifier = Modifier.fillMaxWidth().height(100.dp),
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
        GameScreen()
    }
}

