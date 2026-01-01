package com.example.unick.model

/**
 * This matches your Firebase RTDB "SchoolForm/{uid}" structure.
 * Keep fields as String/Boolean to avoid parsing headaches.
 */
data class SchoolProfileModel(
    val uid: String = "",
    val schoolName: String = "",
    val description: String = "",
    val curriculum: String = "",
    val programsOffered: String = "", // class 1-12 streams / programs
    val establishedYear: String = "",
    val totalStudents: String = "",
    val principalName: String = "",

    val admissionFee: String = "",
    val tuitionFee: String = "",

    val scholarshipAvailable: Boolean = false,
    val hostelFacility: Boolean = false,
    val transportFacility: Boolean = false,

    val facilities: String = "",
    val extracurricular: String = "",

    val email: String = "",
    val contactNumber: String = "",
    val location: String = "",
    val website: String = "",

    // Banner / main profile image
    val imageUrl: String = "",

    // Optional: store google map URL directly (your requirement)
    val googleMapUrl: String = ""
)
