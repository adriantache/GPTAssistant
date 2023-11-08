package com.adriantache.gptassistant

import android.app.Application
import com.adriantache.gptassistant.di.koinSetup

class GptApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        koinSetup()
    }
}
