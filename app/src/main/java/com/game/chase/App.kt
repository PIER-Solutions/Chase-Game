package com.game.chase

import android.app.Application
import com.game.chase.core.util.log.LogWrapper
import com.game.chase.core.util.log.LogX
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    @Inject
    lateinit var logWrapper: LogWrapper

    override fun onCreate() {
        super.onCreate()
        LogX.init(logWrapper)
    }
}