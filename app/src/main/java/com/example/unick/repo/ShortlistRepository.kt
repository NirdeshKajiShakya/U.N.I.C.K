package com.example.unick.repository

import android.util.Log
import com.example.unick.model.School
import com.example.unick.model.UserShortlist
import com.example.unick.model.SchoolForm
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class ShortlistRepository {
    private val database = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/").reference
    private val auth = FirebaseAuth.getInstance()
    private val TAG = "ShortlistRepository"

    private fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: throw Exception("User not authenticated")
    }

    // Get all shortlisted schools for current user
    fun getShortlistedSchools(): Flow<Result<List<School>>> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            val shortlistRef = database.child("Shortlists").child(userId)

            val listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val schoolIds = mutableListOf<String>()
                        for (child in snapshot.children) {
                            child.getValue(String::class.java)?.let { schoolIds.add(it) }
                        }

                        if (schoolIds.isEmpty()) {
                            trySend(Result.success(emptyList()))
                            return
                        }

                        // Fetch school details from SchoolForm
                        val schools = mutableListOf<School>()
                        var processedCount = 0

                        for (schoolId in schoolIds) {
                            database.child("SchoolForm").child(schoolId).get()
                                .addOnSuccessListener { schoolSnapshot ->
                                    val schoolForm = schoolSnapshot.getValue(SchoolForm::class.java)
                                    if (schoolForm != null) {
                                        schools.add(School(
                                            id = schoolForm.uid,
                                            name = schoolForm.schoolName,
                                            type = schoolForm.curriculum.ifEmpty { "School" },
                                            distance = schoolForm.location,
                                            rating = "4.5",
                                            match = "95%",
                                            imageUrl = schoolForm.imageUrl ?: "",
                                            description = schoolForm.description,
                                            fees = schoolForm.tuitionFee,
                                            contact = schoolForm.contactNumber,
                                            website = schoolForm.website
                                        ))
                                    }
                                    processedCount++
                                    if (processedCount == schoolIds.size) {
                                        trySend(Result.success(schools))
                                    }
                                }
                                .addOnFailureListener {
                                    processedCount++
                                    if (processedCount == schoolIds.size) {
                                        trySend(Result.success(schools))
                                    }
                                }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing shortlist data", e)
                        trySend(Result.failure(e))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Error fetching shortlisted schools", error.toException())
                    trySend(Result.failure(error.toException()))
                }
            }

            shortlistRef.addValueEventListener(listener)

            awaitClose {
                shortlistRef.removeEventListener(listener)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up shortlist listener", e)
            trySend(Result.failure(e))
            close()
        }
    }

    // Add school to shortlist
    suspend fun addToShortlist(schoolId: String): Result<Unit> = try {
        val userId = getCurrentUserId()
        database.child("Shortlists").child(userId).child(schoolId).setValue(schoolId).await()

        Log.d(TAG, "School added to shortlist: $schoolId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error adding to shortlist", e)
        Result.failure(e)
    }

    // Remove school from shortlist
    suspend fun removeFromShortlist(schoolId: String): Result<Unit> = try {
        val userId = getCurrentUserId()
        database.child("Shortlists").child(userId).child(schoolId).removeValue().await()

        Log.d(TAG, "School removed from shortlist: $schoolId")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error removing from shortlist", e)
        Result.failure(e)
    }

    // Check if school is in shortlist
    suspend fun isInShortlist(schoolId: String): Boolean = try {
        val userId = getCurrentUserId()
        val snapshot = database.child("Shortlists").child(userId).child(schoolId).get().await()
        snapshot.exists()
    } catch (e: Exception) {
        Log.e(TAG, "Error checking shortlist status", e)
        false
    }

    // Get all available schools (for browsing)
    fun getAllSchools(): Flow<Result<List<School>>> = flow {
        try {
            val snapshot = database.child("SchoolForm").get().await()
            val schools = mutableListOf<School>()

            for (child in snapshot.children) {
                val schoolForm = child.getValue(SchoolForm::class.java)
                if (schoolForm != null) {
                    schools.add(School(
                        id = schoolForm.uid,
                        name = schoolForm.schoolName,
                        type = schoolForm.curriculum.ifEmpty { "School" },
                        distance = schoolForm.location,
                        rating = "4.5",
                        match = "95%",
                        imageUrl = schoolForm.imageUrl ?: "",
                        description = schoolForm.description,
                        fees = schoolForm.tuitionFee,
                        contact = schoolForm.contactNumber,
                        website = schoolForm.website
                    ))
                }
            }

            emit(Result.success(schools))
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching all schools", e)
            emit(Result.failure(e))
        }
    }

    // Search schools by name
    fun searchSchools(query: String): Flow<Result<List<School>>> = flow {
        try {
            val snapshot = database.child("SchoolForm").get().await()
            val schools = mutableListOf<School>()

            for (child in snapshot.children) {
                val schoolForm = child.getValue(SchoolForm::class.java)
                if (schoolForm != null && schoolForm.schoolName.contains(query, ignoreCase = true)) {
                    schools.add(School(
                        id = schoolForm.uid,
                        name = schoolForm.schoolName,
                        type = schoolForm.curriculum.ifEmpty { "School" },
                        distance = schoolForm.location,
                        rating = "4.5",
                        match = "95%",
                        imageUrl = schoolForm.imageUrl ?: "",
                        description = schoolForm.description,
                        fees = schoolForm.tuitionFee,
                        contact = schoolForm.contactNumber,
                        website = schoolForm.website
                    ))
                }
            }

            emit(Result.success(schools))
        } catch (e: Exception) {
            Log.e(TAG, "Error searching schools", e)
            emit(Result.failure(e))
        }
    }

    // Clear entire shortlist
    suspend fun clearShortlist(): Result<Unit> = try {
        val userId = getCurrentUserId()
        database.child("Shortlists").child(userId).removeValue().await()

        Log.d(TAG, "Shortlist cleared")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "Error clearing shortlist", e)
        Result.failure(e)
    }
}