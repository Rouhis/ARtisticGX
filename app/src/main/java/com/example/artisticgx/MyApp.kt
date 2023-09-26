package com.example.artisticgx

import android.app.Application
import android.content.Context
import android.util.Log

class MyApp: Application() {
    companion object {
        lateinit var appContext: Context
        lateinit var instance: MyApp
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("QQQ", "MyApp onCreate()")
        appContext = applicationContext
        instance = this
    }
}