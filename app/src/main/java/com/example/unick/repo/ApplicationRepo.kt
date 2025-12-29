package com.example.unick.repo

import com.example.unick.model.StudentApplication

interface ApplicationRepo {
    suspend fun submitApplication(
        application: StudentApplication
    ): Result<Unit>
}