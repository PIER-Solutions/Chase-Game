package com.game.chase.data.game

import com.game.chase.data.game.db.model.Score

interface GameRepository {
    suspend fun insertScore(score: Score)
    suspend fun getTopScores(limit: Int): List<Score>
    suspend fun getLatestScore(): Score?
}