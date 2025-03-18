package com.game.chase.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.game.chase.data.entity.Score
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlinx.datetime.*


@Composable
fun ScoreItem(score: Score) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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