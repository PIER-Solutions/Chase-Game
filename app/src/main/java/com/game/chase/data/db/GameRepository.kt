package com.game.chase.data.db

import androidx.lifecycle.LiveData
import com.game.chase.data.entity.Score
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class GameRepository @Inject constructor(private val scoreDao: ScoreDao) {
    val allScores: LiveData<List<Score>> = scoreDao.getAllScores()

    suspend fun insertScore(score: Score) {
        scoreDao.insert(score)
    }


}


