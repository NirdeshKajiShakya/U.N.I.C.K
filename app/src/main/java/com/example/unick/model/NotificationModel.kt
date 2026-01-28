package com.example.unick.model

data class Notification(
    val title: String = "",
    val description: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    val id: String = "",
    // Type: "student" for student notifications, "school" for school notifications
    val type: String = "student",
    // Application ID for navigation (used in school notifications)
    val applicationId: String = "",
    // School ID for context (used in student notifications)
    val schoolId: String = "",
    // Student name for school notifications
    val studentName: String = ""
)
