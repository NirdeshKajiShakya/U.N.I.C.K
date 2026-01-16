package com.example.unick.model

data class StudentApplication(
    val applicationId: String = "",
    val schoolId: String = "",
    val studentId: String = "",

    val fullName: String = "",
    val dob: String = "",
    val gender: String = "",
    val bloodGroup: String = "",
    val interests: String = "",

    val lastSchoolName: String = "",
    val standard: String = "",

    val fatherName: String = "",
    val fatherPhone: String = "",
    val motherName: String = "",
    val motherPhone: String = "",

    val presentAddress: String = "",
    val permanentAddress: String = "",
    val schoolBudget: String = "",

    // Application status: "pending", "accepted", "rejected"
    val status: String = "pending",

    // Review info
    val reviewedBy: String = "",
    val reviewedAt: Long = 0L,

    val timestamp: Long = System.currentTimeMillis()
)
