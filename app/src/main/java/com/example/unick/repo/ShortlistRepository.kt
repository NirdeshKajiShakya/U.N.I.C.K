package com.example.unick.repository

import android.util.Log
import com.example.unick.model.School
import com.example.unick.model.UserShortlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class ShortlistRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "ShortlistRepository"

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User not authenticated")
    }

    // Get all shortlisted schools for current user
    fun getShortlistedSchools(): Flow<Result<List<School>>> = flow {
        try {
            val userId = getCurrentUserId()
            val userShortlist = firestore.collection("shortlists")
                .document(userId)
                .get()
                .await()
                .toObject(UserShortlist::class.java) ?: UserShortlist()

            if (userShortlist.schoolIds.isEmpty()) {
                emit(Result.success(emptyList()))
                return@flow
            }

            val schools = mutableListOf<School>()
            for (schoolId in userShortlist.schoolIds) {
                val school = firestore.collection("schools")
                    .document(schoolId)
                    .get()
                    .await()
                    .toObject(School::class.java)

                if (school != null) {
                    schools.add(school)
                }
            }
            emit(Result.success(schools))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching shortlisted schools", e)
            emit(Result.failure(e))
        }
    }

    // Add school to shortlist
    suspend fun addToShortlist(schoolId: String): Result<Unit> = try {
        val userId = getCurrentUserId()
        val userShortlistRef = firestore.collection("shortlists").document(userId)

        firestore.runTransaction { transaction ->
            val shortlist = transaction.get(userShortlistRef).toObject(UserShortlist::class.java)
                ?: UserShortlist(userId = userId)

            val updatedSchoolIds = shortlist.schoolIds.toMutableList()
            if (!updatedSchoolIds.contains(schoolId)) {
                updatedSchoolIds.add(schoolId)
            }

            val updatedShortlist = shortlist.copy(
                schoolIds = updatedSchoolIds,
                updatedAt = System.currentTimeMillis()
            )

            transaction.set(userShortlistRef, updatedShortlist)
        }.await()

        Log.d(TAG, "School added to shortlist: $schoolId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error adding to shortlist", e)
        Result.failure(e)
    }

    // Remove school from shortlist
    suspend fun removeFromShortlist(schoolId: String): Result<Unit> = try {
        val userId = getCurrentUserId()
        val userShortlistRef = firestore.collection("shortlists").document(userId)

        firestore.runTransaction { transaction ->
            val shortlist = transaction.get(userShortlistRef).toObject(UserShortlist::class.java)
                ?: UserShortlist(userId = userId)

            val updatedSchoolIds = shortlist.schoolIds.toMutableList()
            updatedSchoolIds.remove(schoolId)

            val updatedShortlist = shortlist.copy(
                schoolIds = updatedSchoolIds,
                updatedAt = System.currentTimeMillis()
            )

            transaction.set(userShortlistRef, updatedShortlist)
        }.await()

        Log.d(TAG, "School removed from shortlist: $schoolId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error removing from shortlist", e)
        Result.failure(e)
    }

    // Check if school is in shortlist
    suspend fun isInShortlist(schoolId: String): Boolean = try {
        val userId = getCurrentUserId()
        val userShortlist = firestore.collection("shortlists")
            .document(userId)
            .get()
            .await()
            .toObject(UserShortlist::class.java) ?: return false

        userShortlist.schoolIds.contains(schoolId)
    } catch (e: Exception) {
        Log.e(TAG, "Error checking shortlist status", e)
        false
    }

    // Get all available schools (for browsing)
    fun getAllSchools(): Flow<Result<List<School>>> = flow {
        try {
            val schools = firestore.collection("schools")
                .get()
                .await()
                .toObjects(School::class.java)

            emit(Result.success(schools))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all schools", e)
            emit(Result.failure(e))
        }
    }

    // Search schools by name
    fun searchSchools(query: String): Flow<Result<List<School>>> = flow {
        try {
            val schools = firestore.collection("schools")
                .get()
                .await()
                .toObjects(School::class.java)
                .filter { it.name.contains(query, ignoreCase = true) }

            emit(Result.success(schools))
        } catch (e: Exception) {
            Log.e(TAG, "Error searching schools", e)
            emit(Result.failure(e))
        }
    }

    // Clear entire shortlist
    suspend fun clearShortlist(): Result<Unit> = try {
        val userId = getCurrentUserId()
        firestore.collection("shortlists")
            .document(userId)
            .delete()
            .await()

        Log.d(TAG, "Shortlist cleared")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error clearing shortlist", e)
        Result.failure(e)
    }
}