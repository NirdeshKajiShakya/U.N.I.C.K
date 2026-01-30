package com.example.unick.model

data class EditProfileModel(
    val fullName: String = "",
    val email: String = "",
    val contact: String = "",
    val dob: String = "",
    val gender: String = "",
    val location: String = "",
    val profilePictureUrl: String = ""
)
