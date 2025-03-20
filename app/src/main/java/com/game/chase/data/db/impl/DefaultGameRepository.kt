package com.game.chase.data.db.impl

import androidx.lifecycle.LiveData
import com.game.chase.data.db.GameRepository
import com.game.chase.data.db.ScoreDao
import com.game.chase.data.entity.Score
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

    fun getAllScores(): LiveData<List<Score>> {
        return scoreDao.getAllScores()
    }

}


