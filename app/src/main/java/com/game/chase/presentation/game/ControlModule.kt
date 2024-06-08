package com.game.chase.presentation.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.game.chase.core.constants.Direction

@Composable
fun ControlModule(modifier: Modifier = Modifier, onMove: (Direction) -> Unit, onTeleport: () -> Unit, onBomb: () -> Unit, onNewGame: () -> Unit) {
    Column(
        modifier = modifier
    ) {
        Row {
            Button(onClick = { onMove(Direction.UP) }) { Text("Up") }
            Button(onClick = { onMove(Direction.DOWN) }) { Text("Down") }
            Button(onClick = { onMove(Direction.LEFT) }) { Text("Left") }
            Button(onClick = { onMove(Direction.RIGHT) }) { Text("Right") }
        }
        Row {
            Button(onClick = onTeleport) { Text("Teleport") }
            Button(onClick = onBomb) { Text("Bomb") }
        }
        Button(onClick = onNewGame) { Text("New Game") }
    }
}
