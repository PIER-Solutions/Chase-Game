import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.data.entity.Player
import com.game.chase.data.entity.Position
import com.game.chase.domain.game.GameState

import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.MutableLiveData
import com.game.chase.core.constants.GRID_SIZE
import com.game.chase.ui.theme.*
import android.content.res.Configuration
import com.game.chase.presentation.game.MockGameViewModel

@Preview(
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "DefaultPreviewDark"
)
@Preview(
    showBackground = false,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    name = "DefaultPreviewLight"
)
@Composable
fun PreviewGameGrid() {
    GameGrid(
        modifier = Modifier
        .fillMaxSize(), gameState = MockGameViewModel().gameState.value)
}

@Composable
fun GameGrid(modifier: Modifier = Modifier, gameState: GameState?) {
    val initialState = GameState(
        player = Player(Position(0, 0)),
        enemies = mutableListOf(),
        collisionSquares = mutableListOf()
    )

    BoxWithConstraints(modifier = modifier.border(2.dp, MaterialTheme.colorScheme.outline)) {
        val gridSize = maxWidth.value.coerceAtMost(maxHeight.value)
        val cellSize = (gridSize / (GRID_SIZE + 1)).dp

        Column {

            (gameState ?: initialState).let { state ->
                for (y in 0 until GRID_SIZE) {
                    Row {
                        for (x in 0 until GRID_SIZE) {
                            val position = Position(x, y)
                            val isDeadPlayer = state.player.lives == 0
                            val isPlayer = state.player.position == position
                            val isEnemy = state.enemies.any { it.position == position }
                            val isCollision = state.collisionSquares.contains(position)

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(
                                        when {
//                                            isPlayer -> MaterialTheme.colorScheme.onBackground
//                                            isEnemy -> MaterialTheme.colorScheme.onBackground
                                            isCollision -> MaterialTheme.colorScheme.tertiary
                                            else -> MaterialTheme.colorScheme.tertiaryContainer
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayer) {
                                    if (isDeadPlayer) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Dead Player")
                                    } else {
                                        Icon(Icons.Filled.Face, contentDescription = "Player", tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                    }
                                } else if (isEnemy) {
                                    Icon(Icons.Filled.Face, contentDescription = "Enemy", tint = Theme1RedLight)
                                } else if (isCollision) {
                                    Icon(Icons.Filled.Close, contentDescription = "Dead Player")
                                } else {
//                                    Text("${position.x},${position.y}")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

