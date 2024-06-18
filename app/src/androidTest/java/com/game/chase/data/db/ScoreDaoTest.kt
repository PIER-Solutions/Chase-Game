package com.game.chase.data.db

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.game.chase.data.entity.Score
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScoreDaoTest {

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: GameDatabase
    private lateinit var scoreDao: ScoreDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java).build()
        scoreDao = database.scoreDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    //region insert

    @Test
    fun insert_SingleScore() = runBlocking {
        val score = Score(1, 1000)
        scoreDao.insert(score)

        val allScores = scoreDao.getAllScores().getOrAwaitValue()
        assertTrue(allScores.contains(score))
    }

    @Test
    fun insert_MultipleScores() = runBlocking {
        val scores = listOf(
            Score(1, 1000),
            Score(2, 2000),
            Score(3, 3000)
        )
        scores.forEach { scoreDao.insert(it) }

        val allScores = scoreDao.getAllScores().getOrAwaitValue()
        assertTrue(allScores.containsAll(scores))
    }

    @Test
    fun insert_WithConflictDuplicateScores() = runBlocking {
        val score = Score(1, 1000)
        scoreDao.insert(score)

        val duplicateScore = Score(1, 2000)
        scoreDao.insert(duplicateScore)

        val allScores = scoreDao.getAllScores().getOrAwaitValue()
        assertTrue(allScores.contains(duplicateScore))
        assertFalse(allScores.contains(score))
    }

    //endregion

    //region getTopScores
    @Test
    fun getTopScores_WithDifferentLimits() = runBlocking {
        val scores = listOf(
            Score(1, 1000),
            Score(2, 2000),
            Score(3, 3000)
        )
        scores.forEach { scoreDao.insert(it) }

        val topScores1 = scoreDao.getTopScores(0)
        assertTrue(topScores1.isEmpty())

        val topScores2 = scoreDao.getTopScores(5)
        assertEquals(scores.sortedByDescending { it.points }, topScores2)
    }

    @Test
    fun getTopScores_WithNoScores() = runBlocking {
        val topScores = scoreDao.getTopScores(5)
        assertTrue(topScores.isEmpty())
    }

    @Test
    fun getTopScores_WithLessScoresThanLimit() = runBlocking {
        val scores = listOf(
            Score(1, 1000),
            Score(2, 2000)
        )
        scores.forEach { scoreDao.insert(it) }

        val topScores = scoreDao.getTopScores(5)
        assertEquals(scores.sortedByDescending { it.points }, topScores)
    }

    @Test
    fun getTopScores_WithMoreScoresThanLimit() = runBlocking {
        val scores = listOf(
            Score(1, 1000),
            Score(2, 2000),
            Score(3, 3000)
        )
        scores.forEach { scoreDao.insert(it) }

        val topScores = scoreDao.getTopScores(2)
        assertEquals(scores.sortedByDescending { it.points }.take(2), topScores)
    }

    @Test
    fun getTopScores_Order() = runBlocking {
        val scores = listOf(
            Score(1, 1000),
            Score(2, 2000),
            Score(3, 3000)
        )
        scores.forEach { scoreDao.insert(it) }

        val topScores = scoreDao.getTopScores(3)
        assertEquals(scores.sortedByDescending { it.points }, topScores)
    }

    //endregion
}