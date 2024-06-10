package com.game.chase.data

import androidx.lifecycle.LiveData
import java.util.Random
import javax.inject.Inject
import javax.inject.Singleton

@Singleton 
class GameRepository @Inject constructor(private val scoreDao: ScoreDao) {
    val allScores: LiveData<List<Score>> = scoreDao.getAllScores()

    suspend fun insertScore(score: Score) {
        scoreDao.insert(score)
    }


}


