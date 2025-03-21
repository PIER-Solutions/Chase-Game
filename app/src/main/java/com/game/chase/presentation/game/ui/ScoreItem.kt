package com.game.chase.presentation.game.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.game.chase.data.game.db.model.Score
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlinx.datetime.*


@Composable
fun ScoreItem(score: Score, isCurrentScore: Boolean = false) {
    val backgroundColor = if (isCurrentScore) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) // Highlight color
    } else {
        Color.Transparent // Default color
    }


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = score.points.toString())
        Text(text = formatDate(score.date))
    }
}

fun formatDate(timestamp: Long): String {
    val instant = Instant.ofEpochMilli(timestamp)
    val localDateTime = instant.toKotlinInstant().toLocalDateTime(TimeZone.currentSystemDefault())
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")
    return localDateTime.toJavaLocalDateTime().format(formatter)
}

@Preview(showBackground = true)
@Composable
fun ScoreItemPreview() {
    val score = Score(points = 1234, date = System.currentTimeMillis(), id = 1)
    ScoreItem(score = score, isCurrentScore = false)
}

@Preview(showBackground = true)
@Composable
fun ScoreItemPreviewHighlighted() {
    val score = Score(points = 1234, date = System.currentTimeMillis(), id = 1)
    ScoreItem(score = score, isCurrentScore = true)
}