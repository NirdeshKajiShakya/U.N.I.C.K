package com.example.unick.model

data class SchoolReviewModel(
    val id: String = "",
    val schoolId: String = "",
    val reviewerUid: String = "",
    val reviewerName: String = "", // ✅ NEW
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: Long = 0L
)
