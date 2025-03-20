package com.game.chase.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.game.chase.data.entity.Score

@Composable
fun EndOfGameDialog(
    score: String,
    topScores: List<Score>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Game Over") },
        text = {
            Column {
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("Your Score: ")
                }
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text(score)
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("Top 10 High Scores:")
                }
                LazyColumn {
                    items(topScores) { score ->
                        ScoreItem(score = score)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EndOfGameDialogPreview() {
    val topScores = listOf(
        Score(points = 1000, date = System.currentTimeMillis()),
        Score(points = 900, date = System.currentTimeMillis()),
        Score(points = 800, date = System.currentTimeMillis()),
        Score(points = 700, date = System.currentTimeMillis()),
        Score(points = 600, date = System.currentTimeMillis()),
        Score(points = 500, date = System.currentTimeMillis()),
        Score(points = 400, date = System.currentTimeMillis()),
        Score(points = 300, date = System.currentTimeMillis()),
        Score(points = 200, date = System.currentTimeMillis()),
        Score(points = 100, date = System.currentTimeMillis()),
    )
    EndOfGameDialog(
        score = "12345",
        topScores = topScores,
        onDismiss = {} // Dummy implementation
    )
}