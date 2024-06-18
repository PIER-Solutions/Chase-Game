package com.game.chase.data.db

import androidx.lifecycle.LiveData
import com.game.chase.data.entity.Score
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class GameRepository @Inject constructor(
    private val scoreDao: ScoreDao
) {
    suspend fun insertScore(score: Score) {
        scoreDao.insert(score)
    }

    suspend fun getTopScores(limit: Int): List<Score> {
        return scoreDao.getTopScores(limit)
    }

    fun getAllScores(): LiveData<List<Score>> {
        return scoreDao.getAllScores()
    }

}


