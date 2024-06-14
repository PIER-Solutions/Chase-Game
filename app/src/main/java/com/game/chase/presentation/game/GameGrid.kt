import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.domain.game.GameState

import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

@Preview(showBackground = true)
@Composable
fun PreviewGameGrid() {
    val mockGameState = MutableLiveData(
        GameState(
        player = Player(Position(9, 9)),
        enemies = mutableListOf(),
        collisionSquares = mutableListOf()
    )
    )
    GameGrid(
        modifier = Modifier
        .fillMaxSize()
        .background(Color.LightGray), gameState = mockGameState)
}

@Composable
fun GameGrid(modifier: Modifier = Modifier, gameState: LiveData<GameState>) {
    val initialState = GameState(
        player = Player(Position(0, 0)),
        enemies = mutableListOf(),
        collisionSquares = mutableListOf()
    )

    BoxWithConstraints(modifier = modifier.border(2.dp, Color.Black)) {
        val numColumns = 19
        val numRows = 19

        // Assuming you want the grid to occupy as much space as possible
//        val tileSizePx = min(
//            (constraints.maxWidth - numColumns) / numColumns,
//            (constraints.maxHeight - numRows) / numRows
//        )
//        val tileSizeDp = with(LocalDensity.current) { tileSizePx.toDp() }

        val gridSize = maxWidth.value.coerceAtMost(maxHeight.value)
        val cellSize = (gridSize / 19).dp

        Column {

            gameState.observeAsState(initial = initialState).value.let { state ->
                for (y in 0 until 19) {
                    Row {
                        for (x in 0 until 19) {
                            val position = Position(x, y)
                            val isPlayer = state.player.position == position
                            val isEnemy = state.enemies.any { it.position == position }
                            val isCollision = state.collisionSquares.contains(position)

                            Box(
                                modifier = Modifier
                                    .size(cellSize)
                                    .background(
                                        when {
                                            isPlayer -> Color.Blue
                                            isEnemy -> Color.Red
                                            isCollision -> Color.Gray
                                            else -> Color.White
                                        },
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isPlayer) {
                                    Text("P", color = Color.White)
                                } else if (isEnemy) {
                                    Text("E", color = Color.White)
                                } else if (isCollision) {
                                    Text("X", color = Color.Black)
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

