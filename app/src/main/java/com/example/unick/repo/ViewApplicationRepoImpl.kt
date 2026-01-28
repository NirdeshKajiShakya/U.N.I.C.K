package com.example.unick.repo

import android.util.Log
import com.example.unick.model.StudentApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class ViewApplicationRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
) : ViewApplicationRepo {

    private val COLLECTION = "student_applications"
    private val NOTIFICATIONS = "notifications"
    private val TAG = "ViewApplicationRepoImpl"

    override suspend fun getApplicationsForSchool(schoolId: String): Result<List<StudentApplication>> {
        return try {
            Log.d(TAG, "Fetching applications for school: $schoolId")

            val databaseRef = database.getReference(COLLECTION)
            val snapshot = databaseRef.get().await()

            val applications = mutableListOf<StudentApplication>()

            for (child: DataSnapshot in snapshot.children) {
                val app = child.getValue(StudentApplication::class.java)
                if (app != null && app.schoolId == schoolId) {
                    val appWithId = app.copy(applicationId = child.key ?: "")
                    applications.add(appWithId)
                }
            }

            applications.sortByDescending { it.timestamp }

            Log.d(TAG, "✅ Found ${applications.size} applications for school: $schoolId")
            Result.success(applications)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching applications: ${e.message}", e)
            Result.failure(Exception("Failed to fetch applications: ${e.message}"))
        }
    }

    override suspend fun getApplicationById(applicationId: String): Result<StudentApplication> {
        return try {
            Log.d(TAG, "Fetching application: $applicationId")

            val databaseRef = database.getReference(COLLECTION).child(applicationId)
            val snapshot = databaseRef.get().await()

            val application = snapshot.getValue(StudentApplication::class.java)

            if (application != null) {
                val appWithId = application.copy(applicationId = snapshot.key ?: applicationId)
                Log.d(TAG, "✅ Found application: ${appWithId.fullName}")
                Result.success(appWithId)
            } else {
                Log.e(TAG, "❌ Application not found: $applicationId")
                Result.failure(Exception("Application not found"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching application: ${e.message}", e)
            Result.failure(Exception("Failed to fetch application: ${e.message}"))
        }
    }

    override suspend fun updateApplicationStatus(
        applicationId: String,
        status: String,
        reviewedBy: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating application $applicationId status to: $status")

            // First, get the application to retrieve student info
            val appSnapshot = database.getReference(COLLECTION).child(applicationId).get().await()
            val application = appSnapshot.getValue(StudentApplication::class.java)

            if (application != null) {
                val databaseRef = database.getReference(COLLECTION).child(applicationId)

                // Update application status
                val updates = mapOf(
                    "status" to status,
                    "reviewedBy" to reviewedBy,
                    "reviewedAt" to System.currentTimeMillis()
                )

                databaseRef.updateChildren(updates).await()

                // Create notification for the student
                createNotificationForStudent(
                    studentId = application.studentId,
                    status = status,
                    schoolId = application.schoolId,
                    studentName = application.fullName
                )

                Log.d(TAG, "✅ Application status updated to: $status")
                Result.success(Unit)
            } else {
                Result.failure(Exception("Application not found"))
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating application status: ${e.message}", e)
            Result.failure(Exception("Failed to update status: ${e.message}"))
        }
    }

    private suspend fun createNotificationForStudent(
        studentId: String,
        status: String,
        schoolId: String,
        studentName: String
    ) {
        try {
            Log.d(TAG, "Creating notification for student: $studentId, status: $status")

            // Generate unique notification ID
            val notificationId = database.getReference(NOTIFICATIONS).child(studentId).push().key

            // Get school name from database
            val schoolSnapshot = database.getReference("schools").child(schoolId).get().await()
            val schoolName = schoolSnapshot.child("schoolName").value as? String ?: "School"

            // Format timestamp
            val currentTime = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault()).format(Date())

            // Create notification message based on status
            val (title, description) = when (status.lowercase()) {
                "accepted" -> {
                    "Application Accepted! 🎉" to "Your application to $schoolName has been accepted. Congratulations! 🎊"
                }
                "rejected" -> {
                    "Application Update" to "Your application to $schoolName was not accepted. Better luck next time!"
                }
                else -> {
                    "Application Status Update" to "Your application to $schoolName has been updated."
                }
            }

            // Build notification object
            val notification = mapOf(
                "id" to notificationId,
                "title" to title,
                "description" to description,
                "timestamp" to currentTime,
                "isRead" to false,
                "type" to "student",
                "applicationId" to "",
                "schoolId" to schoolId,
                "studentName" to ""
            )

            // Save to Firebase
            if (notificationId != null) {
                database.getReference(NOTIFICATIONS)
                    .child(studentId)
                    .child(notificationId)
                    .setValue(notification)
                    .await()

                Log.d(TAG, "✅ Notification created for student: $studentId")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating notification: ${e.message}", e)
            // Don't fail the whole operation if notification fails
        }
    }

    /**
     * Create a notification for the school when a student applies
     */
    suspend fun createNotificationForSchool(
        schoolId: String,
        applicationId: String,
        studentName: String
    ) {
        try {
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
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error creating school notification: ${e.message}", e)
            // Don't fail the whole operation if notification fails
        }
    }
}