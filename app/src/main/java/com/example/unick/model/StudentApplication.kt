package com.example.unick.model

data class StudentApplication(
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

    val timestamp: Long = System.currentTimeMillis()
)
