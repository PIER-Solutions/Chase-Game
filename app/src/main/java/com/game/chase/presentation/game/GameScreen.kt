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
import androidx.compose.ui.Modifier
import androidx.fragment.app.Fragment
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.game.chase.presentation.ControlModule
import com.game.chase.ui.theme.MyApplicationTheme
import androidx.hilt.navigation.compose.hiltViewModel

//@Composable
//fun GameScreen(modifier: Modifier = Modifier, viewModel: GameViewModel = hiltViewModel()) {
//    // Assuming GameState is obtained from the ViewModel
//    val gameState = viewModel.gameState
//
//    Column(
//        modifier = modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        GameGrid(modifier = Modifier.weight(1f).fillMaxWidth(), gameState = gameState)
//        ControlModule(modifier = Modifier.fillMaxWidth().height(100.dp),
//            onMove = { direction -> viewModel.movePlayer(direction) },
//            onTeleport = { viewModel.teleportPlayer() },
//            onBomb = { viewModel.useBomb() },
//            onNewGame = { viewModel.startNewGame() }
//        )
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GameScreenPreview() {
//    MyApplicationTheme {
//        GameScreen(viewModel = GameViewModel())
//    }
//}


