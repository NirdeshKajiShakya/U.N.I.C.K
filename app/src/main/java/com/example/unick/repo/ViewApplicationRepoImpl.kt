package com.example.unick.repo

import android.util.Log
import com.example.unick.model.StudentApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Firebase Realtime Database implementation for viewing and managing school applications.
 */
class ViewApplicationRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : ViewApplicationRepo {

    private val COLLECTION = "student_applications"
    private val TAG = "ViewApplicationRepoImpl"

    /**
     * Get all applications for a specific school.
     * Queries the database where schoolId matches.
     */
    override suspend fun getApplicationsForSchool(schoolId: String): Result<List<StudentApplication>> {
        return try {
            Log.d(TAG, "Fetching applications for school: $schoolId")

            val databaseRef = database.getReference(COLLECTION)
            val snapshot = databaseRef
                .orderByChild("schoolId")
                .equalTo(schoolId)
                .get()
                .await()

            val applications = mutableListOf<StudentApplication>()

            for (child: DataSnapshot in snapshot.children) {
                val app = child.getValue(StudentApplication::class.java)
                if (app != null) {
                    // Include the Firebase key as applicationId
                    val appWithId = app.copy(applicationId = child.key ?: "")
                    applications.add(appWithId)
                }
            }

            // Sort by timestamp descending (newest first)
            applications.sortByDescending { it.timestamp }

            Log.d(TAG, "✅ Found ${applications.size} applications for school: $schoolId")
            Result.success(applications)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching applications: ${e.message}", e)
            Result.failure(Exception("Failed to fetch applications: ${e.message}"))
        }
    }

    /**
     * Get a single application by its ID.
     */
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

    /**
     * Update the status of an application (accept or reject).
     * Also records who reviewed it and when.
     */
    override suspend fun updateApplicationStatus(
        applicationId: String,
        status: String,
        reviewedBy: String
    ): Result<Unit> {
        return try {
            Log.d(TAG, "Updating application $applicationId status to: $status")

            val databaseRef = database.getReference(COLLECTION).child(applicationId)

            // Update status, reviewedBy, and reviewedAt fields
            val updates = mapOf(
                "status" to status,
                "reviewedBy" to reviewedBy,
                "reviewedAt" to System.currentTimeMillis()
            )

            databaseRef.updateChildren(updates).await()

            Log.d(TAG, "✅ Application status updated to: $status")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating application status: ${e.message}", e)
            Result.failure(Exception("Failed to update status: ${e.message}"))
        }
    }
}