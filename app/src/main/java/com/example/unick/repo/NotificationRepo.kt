package com.example.unick.repo

interface NotificationRepo {
    suspend fun createNotificationForSchool(
        schoolId: String,
        applicationId: String,
        studentName: String
    ): Result<Unit>
}