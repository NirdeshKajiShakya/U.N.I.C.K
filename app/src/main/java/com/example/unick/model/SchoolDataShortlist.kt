package com.example.unick.model

data class School(
    val id: String = "",
    val name: String = "",
    val type: String = "",
    val distance: String = "",
    val rating: String = "",
    val match: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val fees: String = "",
    val contact: String = "",
    val website: String = ""
)

data class UserShortlist(
    val userId: String = "",
    val schoolIds: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)