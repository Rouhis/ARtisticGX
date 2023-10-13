package com.example.artisticgx

import android.app.Application
import android.content.Context
import android.util.Log

// Create an application singleton to get context
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