package com.example.unick.repo

import android.util.Log
import com.example.unick.model.SchoolForm
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
import com.google.firebase.database.DatabaseException

class CompareSchoolRepoImpl(
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
) : CompareSchoolRepo {

    private val TAG = "CompareSchoolRepoImpl"
    private val TIMEOUT_MS = 30000L

    override suspend fun getAllSchools(): Result<List<SchoolForm>> {
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("SchoolForm").get().await()
            }
            val schools = snapshot.children.mapNotNull {
                it.getValue(SchoolForm::class.java)
            }
            Log.d(TAG, "✅ Fetched ${schools.size} schools for comparison")
            Result.success(schools)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "❌ Timeout fetching schools", e)
            Result.failure(Exception("Request timeout"))
        } catch (e: DatabaseException) {
            Log.e(TAG, "❌ Firebase error fetching schools", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Unknown error fetching schools", e)
            Result.failure(e)
        }
    }

    override suspend fun getSchoolById(schoolId: String): Result<SchoolForm> {
        return try {
            val snapshot = withTimeout(TIMEOUT_MS) {
                database.getReference("SchoolForm").child(schoolId).get().await()
            }
            val school = snapshot.getValue(SchoolForm::class.java)
                ?: return Result.failure(Exception("School not found"))
            Log.d(TAG, "✅ Fetched school: ${school.schoolName}")
            Result.success(school)
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "❌ Timeout fetching school", e)
            Result.failure(Exception("Request timeout"))
        } catch (e: DatabaseException) {
            Log.e(TAG, "❌ Firebase error fetching school", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Unknown error fetching school", e)
            Result.failure(e)
        }
    }
}

