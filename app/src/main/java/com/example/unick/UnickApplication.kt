package com.example.unick

import android.app.Application
import com.google.firebase.FirebaseApp

/**
 * Application class to initialize Firebase on app start.
 * Ensure you add `google-services.json` to the app/ folder and the google-services Gradle plugin
 * in the project-level Gradle files if you prefer automatic configuration.
 */
class UnickApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase programmatically. This works without the google-services plugin
        // as long as `google-services.json` is present in the app/ module.
        FirebaseApp.initializeApp(this)
    }
}

