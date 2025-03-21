package com.game.chase.data.game.impl

import com.game.chase.data.game.GameRepository
import com.game.chase.data.game.db.ScoreDao
import com.game.chase.data.game.db.model.Score
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class DefaultGameRepository @Inject constructor(
    private val scoreDao: ScoreDao
) : GameRepository {
    override suspend fun insertScore(score: Score) {
        scoreDao.insert(score)
    }

    override suspend fun getTopScores(limit: Int): List<Score> {
        return scoreDao.getTopScores(limit)
    }

    override suspend fun getLatestScore(): Score? {
        return scoreDao.getLatestScore()
    }
}


