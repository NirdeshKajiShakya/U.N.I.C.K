package com.example.unick.repo

import android.util.Log
import com.example.unick.view.ApplicationItemForUserProfile
import com.example.unick.view.ShortlistedSchoolForUserProfile
import com.example.unick.view.UserProfileModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import com.google.firebase.database.DatabaseException

class UserProfileRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : UserProfileRepo {

    private val TAG = "UserProfileRepoImpl"
    private val TIMEOUT_MS = 30000L

//    user profile

    override suspend fun getUserProfile(userId: String): Result<UserProfileModel> {
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("Users").child(userId).get().await()
            }
            val profile = snapshot.getValue(UserProfileModel::class.java)
                ?: return Result.failure(Exception("User profile not found"))
            Log.d(TAG, "✅ Fetched user profile: ${profile}")
            Result.success(profile)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "❌ Timeout fetching user profile", e)
            Result.failure(Exception("Request timeout"))
        } catch (e: DatabaseException) {
            Log.e(TAG, "❌ Firebase error fetching user profile", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Unknown error fetching user profile", e)
            Result.failure(e)
        }
    }

//    shortlisted schools

    override suspend fun getShortlistedSchools(userId: String): Result<List<ShortlistedSchoolForUserProfile>> {
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("Users").child(userId).child("shortlistedSchools").get().await()
            }
            val list = snapshot.children.mapNotNull { it.getValue(ShortlistedSchoolForUserProfile::class.java) }
            Log.d(TAG, "✅ Fetched ${list.size} shortlisted schools")
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching shortlisted schools", e)
            Result.failure(e)
        }
    }

    // student applications

    override suspend fun getApplications(userId: String): Result<List<ApplicationItemForUserProfile>> {
        return try {
            Log.d(TAG, "🔍 Fetching applications for userId: $userId")

            // Fetch all applications and filter client-side (avoids Firebase index requirement)
            val applicationsSnapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("student_applications")
                    .get()
                    .await()
            }

            Log.d(TAG, "📦 Total applications in database: ${applicationsSnapshot.childrenCount}")

            val applicationsList = mutableListOf<ApplicationItemForUserProfile>()

            for (appSnapshot in applicationsSnapshot.children) {
                val studentIdFromDb = appSnapshot.child("studentId").getValue(String::class.java) ?: ""

                // Only process applications for this user
                if (studentIdFromDb != userId) continue

                val applicationId = appSnapshot.child("applicationId").getValue(String::class.java) ?: appSnapshot.key ?: ""
                val schoolId = appSnapshot.child("schoolId").getValue(String::class.java) ?: ""
                val status = appSnapshot.child("status").getValue(String::class.java) ?: "pending"

                Log.d(TAG, "📋 Found application: $applicationId")
                Log.d(TAG, "📋 schoolId from application: '$schoolId'")
                Log.d(TAG, "📋 status: $status")

                // Fetch school name - check multiple locations
                var schoolName = "Unknown School"
                if (schoolId.isNotEmpty()) {
                    try {
                        Log.d(TAG, "🔍 Looking for school with ID: $schoolId")

                        // First try SchoolForm (where schools edit their profile)
                        var schoolSnapshot = withTimeout(TIMEOUT_MS) {
                            database.getReference("SchoolForm").child(schoolId).get().await()
                        }
                        Log.d(TAG, "📂 SchoolForm exists: ${schoolSnapshot.exists()}, children: ${schoolSnapshot.childrenCount}")

                        // Log all keys in SchoolForm to see what's available
                        if (schoolSnapshot.exists()) {
                            val keys = schoolSnapshot.children.map { it.key }
                            Log.d(TAG, "📂 SchoolForm keys: $keys")
                        }

                        schoolName = schoolSnapshot.child("schoolName").getValue(String::class.java) ?: ""
                        Log.d(TAG, "📂 SchoolForm/schoolName: '$schoolName'")

                        // If not found in SchoolForm, try "schools" node (initial registration)
                        if (schoolName.isEmpty()) {
                            schoolSnapshot = withTimeout(TIMEOUT_MS) {
                                database.getReference("schools").child(schoolId).get().await()
                            }
                            Log.d(TAG, "📂 schools exists: ${schoolSnapshot.exists()}, children: ${schoolSnapshot.childrenCount}")

                            // Log all keys in schools to see what's available
                            if (schoolSnapshot.exists()) {
                                val keys = schoolSnapshot.children.map { it.key }
                                Log.d(TAG, "📂 schools keys: $keys")
                            }

                            schoolName = schoolSnapshot.child("name").getValue(String::class.java) ?: ""
                            Log.d(TAG, "📂 schools/name: '$schoolName'")
                        }

                        // Fallback to school ID if still empty
                        if (schoolName.isEmpty()) {
                            schoolName = "School ID: ${schoolId.take(8)}"
                        }

                        Log.d(TAG, "🏫 Final school name: $schoolName")
                    } catch (e: Exception) {
                        Log.e(TAG, "Error fetching school name for $schoolId", e)
                        schoolName = "School ID: ${schoolId.take(8)}"
                    }
                }

                applicationsList.add(
                    ApplicationItemForUserProfile(
                        id = applicationId,
                        schoolId = schoolId,
                        schoolName = schoolName,
                        status = status.replaceFirstChar { it.uppercase() },
                        applicationCode = applicationId.take(8).uppercase()
                    )
                )
            }

            Log.d(TAG, "✅ Fetched ${applicationsList.size} applications for user $userId")
            Result.success(applicationsList)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "❌ Timeout fetching applications", e)
            Result.failure(Exception("Request timeout. Please check your internet connection."))
        } catch (e: DatabaseException) {
            Log.e(TAG, "❌ Firebase error fetching applications: ${e.message}", e)
            Result.failure(Exception("Database error: ${e.message}"))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching applications: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun deleteApplication(applicationId: String): Result<Unit> {
        return try {
            Log.d(TAG, "🗑️ Deleting application: $applicationId")

            withTimeout(TIMEOUT_MS) {
                database.getReference("student_applications").child(applicationId).removeValue().await()
            }

            Log.d(TAG, "✅ Application deleted successfully")
            Result.success(Unit)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "❌ Timeout deleting application", e)
            Result.failure(Exception("Request timeout"))
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error deleting application: ${e.message}", e)
            Result.failure(e)
        }
    }
}
