package com.example.unick.repository

import com.example.unick.model.SchoolGalleryModel
import com.example.unick.model.SchoolProfileModel
import com.example.unick.model.SchoolReviewModel

interface SchoolProfileRepo {

    fun observeSchoolProfile(
        schoolId: String,
        onData: (SchoolProfileModel?) -> Unit,
        onError: (String) -> Unit
    )

    fun updateSchoolProfile(
        schoolId: String,
        updated: Map<String, Any?>,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun observeGallery(
        schoolId: String,
        onData: (List<SchoolGalleryModel>) -> Unit,
        onError: (String) -> Unit
    )

    fun addGalleryImage(
        schoolId: String,
        imageUrl: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )

    fun observeReviews(
        schoolId: String,
        onData: (List<SchoolReviewModel>) -> Unit,
        onError: (String) -> Unit
    )

    // ✅ UPDATED: reviewerName added
    fun addReview(
        schoolId: String,
        reviewerUid: String,
        reviewerName: String,
        rating: Int,
        comment: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    )
}
