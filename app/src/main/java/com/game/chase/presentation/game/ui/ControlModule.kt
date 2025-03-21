package com.game.chase.presentation.game.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.core.constants.Direction
import androidx.compose.ui.tooling.preview.Preview
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.domain.game.GameState


@Preview(showBackground = true)
@Composable
fun PreviewControlModule() {
    ControlModule(
        onMove = { direction -> println("Move $direction") },
        onTeleport = { println("Teleport") },
        onBomb = { println("Bomb") },
        onNewGame = { println("New Game") },
        gameState = GameState(
            player = Player(Position(0, 0)),
            enemies = mutableListOf(),
            collisionSquares = mutableListOf()
        )
    )
}

@Composable
fun ControlModule(
    modifier: Modifier = Modifier,
    onMove: (Direction) -> Unit,
    onTeleport: () -> Unit,
    onBomb: () -> Unit,
    onNewGame: () -> Unit,
    gameState: GameState?
) {

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Row 1: Display player lives, level, and score
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text("Lives: ${gameState?.player?.lives ?: "?"}")
            Text("Level: ${gameState?.level ?: "?"}")
            Text("Score: ${gameState?.score ?: "?"}")
        }
        Spacer(modifier = Modifier.height(20.dp))
        // Row 2: Teleport, Bomb, New Game, and Save buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = onTeleport) {
                Text("Teleport (${gameState?.player?.teleportUses ?: "?"})")
            }

            Button(onClick = onBomb) {
                Text("Bomb (${gameState?.player?.bombUses ?: "?"})")
            }

            Button(onClick = onNewGame) {
                Text("New Game")
            }
        }

        // Row 3: Directional controls
        // Row 3: Directional controls
        Box(
            modifier = modifier
                .fillMaxWidth()
                .width(200.dp)
                .height(200.dp)
                .padding(all = 20.dp)
        ) {
            // Placeholder in the center
            val placeholderModifier = Modifier.align(Alignment.Center)

            // Top button with upward icon
            Button(
                onClick = { onMove(Direction.UP) },
                modifier = placeholderModifier.offset(y = (-50).dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowUp,
                    contentDescription = "Move Up"
                )
            }

            // Bottom button with downward icon
            Button(
                onClick = { onMove(Direction.DOWN) },
                modifier = placeholderModifier.offset(y = 50.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Move Down"
                )
            }

            // Start (left) button with back icon
            Button(
                onClick = { onMove(Direction.LEFT) },
                modifier = placeholderModifier.offset(x = (-75).dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Move Left"
                )
            }

            // End (right) button with forward icon
            Button(
                onClick = { onMove(Direction.RIGHT) },
                modifier = placeholderModifier.offset(x = 75.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Move Right"
                )
            }
        }
    }
}