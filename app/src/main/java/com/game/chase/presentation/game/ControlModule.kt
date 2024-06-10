package com.game.chase.presentation.game

//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.material3.Button
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import com.game.chase.core.constants.Direction
//
//@Composable
//fun ControlModule(modifier: Modifier = Modifier, onMove: (Direction) -> Unit, onTeleport: () -> Unit, onBomb: () -> Unit, onNewGame: () -> Unit) {
//    Column(
//        modifier = modifier
//    ) {
//        Row {
//            Button(onClick = { onMove(Direction.UP) }) { Text("Up") }
//            Button(onClick = { onMove(Direction.DOWN) }) { Text("Down") }
//            Button(onClick = { onMove(Direction.LEFT) }) { Text("Left") }
//            Button(onClick = { onMove(Direction.RIGHT) }) { Text("Right") }
//        }
//        Row {
//            Button(onClick = onTeleport) { Text("Teleport") }
//            Button(onClick = onBomb) { Text("Bomb") }
//        }
//        Button(onClick = onNewGame) { Text("New Game") }
//    }
//}

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.core.constants.Direction
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.presentation.GameState


@Preview(showBackground = true)
@Composable
fun PreviewControlModule() {
    ControlModule(
        onMove = { direction -> println("Move $direction") },
        onTeleport = { println("Teleport") },
        onBomb = { println("Bomb") },
        onNewGame = { println("New Game") },
        gameState = MutableLiveData(GameState(
            player = Player(Position(0, 0)),
            enemies = mutableListOf(),
            collisionSquares = listOf()
        ))
    )
}

@Composable
fun ControlModule(
    modifier: Modifier = Modifier,
    onMove: (Direction) -> Unit,
    onTeleport: () -> Unit,
    onBomb: () -> Unit,
    onNewGame: () -> Unit,
    gameState: LiveData<GameState>
) {
    val state by gameState.observeAsState()

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .width(250.dp)
                .height(250.dp)
                .padding(all = 20.dp)
        ) {
            // Top button with upward icon
            Button(
                onClick = { onMove(Direction.UP) },
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Move Up"
                )
            }

            // Bottom button with downward icon
            Button(
                onClick = { onMove(Direction.DOWN) },
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Move Down"
                )
            }

            // Start (left) button with back icon
            Button(
                onClick = { onMove(Direction.LEFT) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Move Left"
                )
            }

            // End (right) button with forward icon
            Button(
                onClick = { onMove(Direction.RIGHT) },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Move Right"
                )
            }

            // Teleport button
            Button(
                onClick = onTeleport,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text("Teleport (${state?.player?.teleportUses ?: "?"})")
            }

            // Bomb button
            Button(
                onClick = onBomb,
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Text("Bomb")
            }

            // New Game button
            Button(
                onClick = onNewGame,
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text("New Game")
            }
        }
    }
}