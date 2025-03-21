package com.game.chase.presentation.game.ui

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.game.chase.data.joke.Joke
import com.game.chase.data.game.db.model.Score

@Composable
fun EndOfGameDialog(
    gameScore: Score?,
    topScores: List<Score>,
    joke: Joke?,
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
                    Text(gameScore?.points.toString())
                }
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                    Text("Top 10 High Scores:")
                }
                LazyColumn {
                    items(topScores) { score ->
                        ScoreItem(score = score, isCurrentScore = gameScore?.id == score.id)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                if (joke != null) {
                    Text(
                        "Here's a joke to make you feel better:",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        joke.setup,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        joke.punchline,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("New Game")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun EndOfGameDialogPreview() {
    val topScores = listOf(
        Score(points = 1000, date = System.currentTimeMillis(), id = 1),
        Score(points = 900, date = System.currentTimeMillis(), id = 2),
        Score(points = 800, date = System.currentTimeMillis(), id = 10),
        Score(points = 700, date = System.currentTimeMillis(), id = 3),
        Score(points = 600, date = System.currentTimeMillis(), id = 4),
        Score(points = 500, date = System.currentTimeMillis(), id = 5),
        Score(points = 400, date = System.currentTimeMillis(), id = 6),
        Score(points = 300, date = System.currentTimeMillis(), id = 7),
        Score(points = 200, date = System.currentTimeMillis(), id = 8),
        Score(points = 100, date = System.currentTimeMillis(), id = 9),
    )
    EndOfGameDialog(
        gameScore = Score(points = 800, id = 10),
        topScores = topScores,
        joke = Joke("setup", "Do you want a brief explanation of what an acorn is?", "In a nutshell, it's an oak tree."),
        onDismiss = {} // Dummy implementation
    )
}