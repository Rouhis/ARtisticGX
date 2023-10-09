package com.example.artisticgx

import android.app.Application
import android.content.Context
import android.util.Log

class MyApp : Application() {
    companion object {
        lateinit var appContext: Context
            private set
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}