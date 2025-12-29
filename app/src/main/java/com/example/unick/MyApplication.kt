package com.example.unick

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = mapOf(
            "cloud_name" to "dbelcobpj",
            "api_key" to "947959738234575",
            "api_secret" to "IhyBcq5TBPqdDzA5qa5LjyrQhAY"
        )
        MediaManager.init(this, config)
    }
}
