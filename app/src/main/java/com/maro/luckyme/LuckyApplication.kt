package com.maro.luckyme

import android.app.Application
import timber.log.Timber

class LuckyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}