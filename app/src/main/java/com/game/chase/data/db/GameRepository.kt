package com.game.chase.data.db

import com.game.chase.data.entity.Score

interface GameRepository {
    suspend fun insertScore(score: Score)
    suspend fun getTopScores(limit: Int): List<Score>
}