package com.example.SuaraRasa_A209008

import android.app.Application
import com.example.SuaraRasa_A209008.data.AppContainer
import com.example.SuaraRasa_A209008.data.AppDataContainer

class MoodTrackerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
