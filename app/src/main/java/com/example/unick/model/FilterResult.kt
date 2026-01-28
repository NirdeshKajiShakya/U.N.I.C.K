package com.example.unick.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FilterResult(
    val feeRange: String = "Any",
    val location: String = "Any",
    val passRate: String = "Any",
    val levels: List<String> = emptyList(),
    val curriculums: List<String> = emptyList(),
    val facilities: List<String> = emptyList(),
    val radiusKm: Double? = null
) : Parcelable
