package com.example.unick.model

data class Notification(
    val title: String = "",
    val description: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    val id: String = ""
)
