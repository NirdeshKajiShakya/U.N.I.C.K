package com.example.unick.model

import android.R
import android.location.Location
import androidx.compose.foundation.pager.PagerSnapDistance

data class Schools(
    val id : Int = 0,
    val name : String = "",
    val fee : String = "",
    val rating : String = "",
    val extraCurricular : String = "",
    val location : String = "",
    val distance : String = "",
    val achievements : List<String>,
    val schoolType : String,
    val genderType : String
){
    fun toMap() : Map<String, Any?> {
        return mapOf(
            "name" to name,
            "fee" to fee,
            "rating" to rating,
            "extraCurricular" to extraCurricular,
            "location" to location,
            "distance" to distance,
            "achievements" to achievements,
            "schoolType" to schoolType,
            "genderType" to genderType
        )

    }
}
