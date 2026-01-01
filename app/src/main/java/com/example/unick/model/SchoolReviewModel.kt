package com.example.unick.model

data class SchoolReviewModel(
    val id: String = "",
    val schoolId: String = "",
    val reviewerUid: String = "",
    val rating: Int = 0,          // 1..5
    val comment: String = "",
    val createdAt: Long = 0L
)
