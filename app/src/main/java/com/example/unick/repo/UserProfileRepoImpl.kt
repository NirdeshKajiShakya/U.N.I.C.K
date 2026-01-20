package com.example.unick.repo

import android.util.Log
import com.example.unick.view.ApplicationItemForUserProfile
import com.example.unick.view.ShortlistedSchoolForUserProfile
import com.example.unick.model.UserProfileModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import com.google.firebase.database.DatabaseException

class UserProfileRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
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

    // student applciations

    override suspend fun getApplications(userId: String): Result<List<ApplicationItemForUserProfile>> {
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("Users").child(userId).child("applications").get().await()
            }
            val list = snapshot.children.mapNotNull { it.getValue(ApplicationItemForUserProfile::class.java) }
            Log.d(TAG, "✅ Fetched ${list.size} applications")
            Result.success(list)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error fetching applications", e)
            Result.failure(e)
        }
    }
}
