package com.example.unick.repo

import com.example.unick.model.EditProfileModel
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class EditProfileRepoImpl : EditProfileRepo {

    private val database = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/").reference
    private val USERS_PATH = "Users"  // Must match RegisterViewModel path (capital U)

    override suspend fun getUserProfile(userId: String): Result<EditProfileModel> {
        return try {
            val snapshot = database.child(USERS_PATH).child(userId).get().await()
            if (snapshot.exists()) {
                val profile = EditProfileModel(
                    fullName = snapshot.child("fullName").getValue(String::class.java) ?: "",
                    email = snapshot.child("email").getValue(String::class.java) ?: "",
                    contact = snapshot.child("contact").getValue(String::class.java) ?: "",
                    dob = snapshot.child("dob").getValue(String::class.java) ?: "",
                    gender = snapshot.child("gender").getValue(String::class.java) ?: "",
                    location = snapshot.child("location").getValue(String::class.java) ?: "",
                    classPref = snapshot.child("classPref").getValue(String::class.java) ?: "Class",
                    levelPref = snapshot.child("levelPref").getValue(String::class.java) ?: "Level",
                    typePref = snapshot.child("typePref").getValue(String::class.java) ?: "Type"
                )
                Result.success(profile)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(userId: String, profile: EditProfileModel): Result<Unit> {
        return try {
            val updates = mapOf(
                "fullName" to profile.fullName,
                "email" to profile.email,
                "contact" to profile.contact,
                "dob" to profile.dob,
                "gender" to profile.gender,
                "location" to profile.location,
                "classPref" to profile.classPref,
                "levelPref" to profile.levelPref,
                "typePref" to profile.typePref
            )
            database.child(USERS_PATH).child(userId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteUserProfile(userId: String): Result<Unit> {
        return try {
            database.child(USERS_PATH).child(userId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}