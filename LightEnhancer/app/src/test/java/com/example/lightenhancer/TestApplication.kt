package com.example.lightenhancer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        setTheme(R.style.TestTheme)
    }
}