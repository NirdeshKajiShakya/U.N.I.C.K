package com.example.unick.repo
import com.example.unick.model.EditProfileModel

interface EditProfileRepo {
    suspend fun getUserProfile(userId: String): Result<EditProfileModel>
    suspend fun updateUserProfile(userId: String, profile: EditProfileModel): Result<Unit>
    suspend fun deleteUserProfile(userId: String): Result<Unit>
}