package com.example.unick.repo

import com.example.unick.model.StudentApplication

/**
 * Repository for student application submission.
 * For school-side viewing and management, use ViewApplicationRepo.
 */
interface ApplicationRepo {
    suspend fun submitApplication(
        application: StudentApplication
    ): Result<Unit>
}