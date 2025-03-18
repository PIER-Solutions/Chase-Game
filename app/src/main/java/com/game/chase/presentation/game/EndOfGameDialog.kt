package com.game.chase.presentation.game

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.game.chase.data.entity.Score

@Composable
fun EndOfGameDialog(
    topScores: List<Score>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Game Over") },
        text = {
            Column {
                Text("Top 10 High Scores:")
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