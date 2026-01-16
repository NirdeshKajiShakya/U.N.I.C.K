package com.example.unick.repo

import android.util.Log
import com.example.unick.model.StudentApplication
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DatabaseException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Firebase Realtime Database implementation of [ApplicationRepo].
 *
 * Notes:
 * - Make sure Firebase is initialized (google-services.json + Firebase setup) in the app.
 * - This class uses the Firebase Database Kotlin extensions (ktx) and kotlinx-coroutines-play-services
 * - Firebase Realtime Database must be enabled in Firebase Console
 */
class ApplicationRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ApplicationRepo {

    private val COLLECTION = "student_applications"
    private val TAG = "ApplicationRepoImpl"
    private val TIMEOUT_MS = 30000L // 30 seconds timeout

    override suspend fun submitApplication(
        application: StudentApplication
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Starting application submission...")
            Log.d(TAG, "Student Name: ${application.fullName}")
            Log.d(TAG, "Firebase Database instance: ${database.app.name}")

            // Get reference to the database
            val databaseRef = database.getReference(COLLECTION)

            // Generate a unique key for this application
            val newApplicationRef = databaseRef.push()
            val applicationId = newApplicationRef.key ?: throw Exception("Failed to generate application ID")

            Log.d(TAG, "Generated Application ID: $applicationId")

            // Include the applicationId in the saved data
            val applicationWithId = application.copy(applicationId = applicationId)

            // Add application to Realtime Database with timeout to prevent infinite loading
            withTimeout(TIMEOUT_MS) {
                newApplicationRef.setValue(applicationWithId).await()
            }

            Log.d(TAG, "✅ Application submitted successfully! Application ID: $applicationId")
            Result.success(Unit)

        } catch (e: DatabaseException) {
            // Specific Firebase Realtime Database errors
            val errorMessage = when {
                e.message?.contains("Permission denied", ignoreCase = true) == true -> {
                    "❌ Permission Denied: Please enable Firebase Realtime Database and configure security rules.\n\n" +
                    "Steps:\n" +
                    "1. Go to Firebase Console\n" +
                    "2. Navigate to Realtime Database\n" +
                    "3. Create database if not exists\n" +
                    "4. Set security rules to allow writes:\n" +
                    "   {\n" +
                    "     \"rules\": {\n" +
                    "       \".read\": true,\n" +
                    "       \".write\": true\n" +
                    "     }\n" +
                    "   }"
                }
                e.message?.contains("disconnected", ignoreCase = true) == true -> {
                    "❌ Network Error: Unable to reach Firebase servers. Please check your internet connection."
                }
                else -> {
                    "❌ Database Error: ${e.message}"
                }
            }
            Log.e(TAG, errorMessage, e)
            Result.failure(Exception(errorMessage))

        } catch (e: TimeoutCancellationException) {
            val errorMessage = "❌ Request Timeout: The submission took too long. Please check your internet connection and try again."
            Log.e(TAG, errorMessage, e)
            Result.failure(Exception(errorMessage))

        } catch (e: Exception) {
            // General errors
            val errorMessage = when {
                e.message?.contains("network", ignoreCase = true) == true -> {
                    "❌ Network Error: Please check your internet connection and try again."
                }
                e.message?.contains("permission", ignoreCase = true) == true -> {
                    "❌ Permission Error: Firebase Realtime Database may not be enabled. Check Firebase Console."
                }
                else -> {
                    "❌ Unexpected Error: ${e.message ?: "Unknown error occurred"}\n\n" +
                    "Please ensure:\n" +
                    "1. Internet connection is active\n" +
                    "2. Firebase Realtime Database is enabled in Firebase Console\n" +
                    "3. google-services.json is properly configured"
                }
            }
            Log.e(TAG, errorMessage, e)
            Result.failure(Exception(errorMessage))
        }
    }
}
