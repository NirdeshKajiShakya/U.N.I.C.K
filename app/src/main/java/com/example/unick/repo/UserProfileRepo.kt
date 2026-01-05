package com.example.unick.repo

import com.example.unick.view.ApplicationItemForUserProfile
import com.example.unick.view.ShortlistedSchoolForUserProfile
import com.example.unick.view.UserProfileModel

interface UserProfileRepo {
    suspend fun getUserProfile(userId: String): Result<UserProfileModel>
    suspend fun getShortlistedSchools(userId: String): Result<List<ShortlistedSchoolForUserProfile>>
    suspend fun getApplications(userId: String): Result<List<ApplicationItemForUserProfile>>

}