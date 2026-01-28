package com.example.unick.repo

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class NotificationRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
) : NotificationRepo {

    private val TAG = "NotificationRepoImpl"
    private val NOTIFICATIONS = "notifications"

    override suspend fun createNotificationForSchool(
        schoolId: String,
        applicationId: String,
        studentName: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Creating notification for school: $schoolId for application: $applicationId")

            // Generate unique notification ID
            val notificationId = database.getReference(NOTIFICATIONS).child(schoolId).push().key

            // Format timestamp
            val currentTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())

            // Create notification message
            val title = "New Application Received 📝"
            val description = "$studentName has applied to your school. Review and take action."

            // Build notification object
            val notification = mapOf(
                "id" to notificationId,
                "title" to title,
                "description" to description,
                "timestamp" to currentTime,
                "isRead" to false,
                "type" to "school",
                "applicationId" to applicationId,
                "schoolId" to schoolId,
                "studentName" to studentName
            )

            // Save to Firebase
            if (notificationId != null) {
                database.getReference(NOTIFICATIONS)
                    .child(schoolId)
                    .child(notificationId)
                    .setValue(notification)
                    .await()

                Log.d(TAG, "✅ Notification created for school: $schoolId")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to generate notification ID"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating school notification: ${e.message}", e)
            Result.failure(e)
        }
    }
}