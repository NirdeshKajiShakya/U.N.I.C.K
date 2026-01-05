package com.example.unick.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SchoolForm(
    val uid: String = "",
    val imageUrl: String? = null,
    val schoolName: String = "",
    val location: String = "",
    val totalStudents: String = "",
    val establishedYear: String = "",
    val principalName: String = "",
    val contactNumber: String = "",
    val email: String = "",
    val website: String = "",
    val curriculum: String = "",
    val programsOffered: String = "",
    val facilities: String = "",
    val tuitionFee: String = "",
    val admissionFee: String = "",
    val scholarshipAvailable: Boolean = false,
    val transportFacility: Boolean = false,
    val hostelFacility: Boolean = false,
    val extracurricular: String = "",
    val description: String = ""
) : Parcelable
