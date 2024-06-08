import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.game.chase.data.Player
import com.game.chase.data.Position
import com.game.chase.presentation.GameState

@Composable
fun GameGrid(modifier: Modifier = Modifier, gameState: LiveData<GameState>) {
    val initialState = GameState(
        player = Player(Position(0, 0)),
        enemies = listOf(),
        collisionSquares = listOf()
    )
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        gameState.observeAsState(initial = initialState).value.let { state ->
            for (y in 0 until 10) {
                Row {
                    for (x in 0 until 10) {
                        val position = Position(x, y)
                        val isPlayer = state.player.position == position
                        val isEnemy = state.enemies.any { it.position == position }
                        val isCollision = state.collisionSquares.contains(position)

                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    when {
                                        isPlayer -> Color.Blue
                                        isEnemy -> Color.Red
                                        isCollision -> Color.Gray
                                        else -> Color.White
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isPlayer) {
                                Text("P", color = Color.White)
                            } else if (isEnemy) {
                                Text("E", color = Color.White)
                            } else if (isCollision) {
                                Text("X", color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}
