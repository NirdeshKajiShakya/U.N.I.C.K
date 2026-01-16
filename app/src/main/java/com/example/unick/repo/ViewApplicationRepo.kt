package com.example.unick.repo

import com.example.unick.model.StudentApplication

interface ViewApplicationRepo {
    // Get all applications for a specific school
    suspend fun getApplicationsForSchool(schoolId: String): Result<List<StudentApplication>>

    // Get a single application by ID
    suspend fun getApplicationById(applicationId: String): Result<StudentApplication>

    // Update application status (accept or reject)
    suspend fun updateApplicationStatus(
        applicationId: String,
        status: String,
        reviewedBy: String
    ): Result<Unit>
}