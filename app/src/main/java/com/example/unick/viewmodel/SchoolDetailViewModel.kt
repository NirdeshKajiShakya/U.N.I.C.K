package com.example.unick.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.unick.model.SchoolGalleryModel
import com.example.unick.model.SchoolProfileModel
import com.example.unick.model.SchoolReviewModel
import com.example.unick.repository.SchoolProfileRepo
import com.example.unick.repository.SchoolProfileRepoImpl
import com.google.firebase.database.FirebaseDatabase


class SchoolDetailViewModel(
    private val repo: SchoolProfileRepo = SchoolProfileRepoImpl()
) : ViewModel() {

    // ---- UI STATE ----
    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    var schoolProfile by mutableStateOf<SchoolProfileModel?>(null)
        private set

    var gallery by mutableStateOf<List<SchoolGalleryModel>>(emptyList())
        private set

    var reviews by mutableStateOf<List<SchoolReviewModel>>(emptyList())
        private set

    var avgRating by mutableStateOf(0.0)
        private set

    var totalReviews by mutableStateOf(0)
        private set

    private var currentSchoolId: String? = null

    private val db =
        FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
            .reference

    // ---- LOAD EVERYTHING ----
    fun loadSchoolDetail(schoolId: String) {
        if (schoolId.isBlank()) {
            error = "School id missing"
            return
        }
        currentSchoolId = schoolId
        loading = true
        error = null

        repo.observeSchoolProfile(
            schoolId = schoolId,
            onData = { profile ->
                schoolProfile = profile
                loading = false
            },
            onError = { msg ->
                error = msg
                loading = false
            }
        )

        repo.observeGallery(
            schoolId = schoolId,
            onData = { list -> gallery = list },
            onError = { msg -> error = msg }
        )

        repo.observeReviews(
            schoolId = schoolId,
            onData = { list ->
                reviews = list
                totalReviews = list.size
                avgRating = if (list.isEmpty()) 0.0 else list.map { it.rating }.average()
            },
            onError = { msg -> error = msg }
        )
    }

    // ✅ helper to fetch fullName from Users node
    private fun fetchUserFullName(uid: String, onDone: (String) -> Unit) {
        db.child("Users").child(uid).child("fullName")
            .get()
            .addOnSuccessListener { snap ->
                val name = snap.getValue(String::class.java).orEmpty().trim()
                onDone(if (name.isNotBlank()) name else "Anonymous")
            }
            .addOnFailureListener {
                onDone("Anonymous")
            }
    }

    // ---- SUBMIT REVIEW ----
    // (kept your signature, but now it saves reviewerName too)
    fun submitReview(
        reviewerUid: String,
        rating: Int,
        comment: String
    ) {
        val schoolId = currentSchoolId ?: run {
            error = "School not loaded"
            return
        }

        if (reviewerUid.isBlank()) {
            error = "Reviewer id missing"
            return
        }

        val cleanComment = comment.trim()
        if (cleanComment.isBlank()) {
            error = "Please write a review comment"
            return
        }

        loading = true

        fetchUserFullName(reviewerUid) { reviewerName ->
            repo.addReview(
                schoolId = schoolId,
                reviewerUid = reviewerUid,
                reviewerName = reviewerName,
                rating = rating,
                comment = cleanComment,
                onSuccess = { loading = false },
                onError = { msg ->
                    error = msg
                    loading = false
                }
            )
        }
    }

    // ---- UPDATE SCHOOL PROFILE (EDIT PROFILE SCREEN USES THIS) ----
    fun updateSchoolFields(updated: Map<String, Any?>) {
        val schoolId = currentSchoolId ?: run {
            error = "School not loaded"
            return
        }
        if (updated.isEmpty()) return

        loading = true
        repo.updateSchoolProfile(
            schoolId = schoolId,
            updated = updated,
            onSuccess = { loading = false },
            onError = { msg ->
                error = msg
                loading = false
            }
        )
    }

    // ---- ADD GALLERY IMAGE (PASS CLOUDINARY URL HERE) ----
    fun addGalleryImageUrl(imageUrl: String) {
        val schoolId = currentSchoolId ?: run {
            error = "School not loaded"
            return
        }
        if (imageUrl.isBlank()) {
            error = "Image URL missing"
            return
        }

        loading = true
        repo.addGalleryImage(
            schoolId = schoolId,
            imageUrl = imageUrl,
            onSuccess = { loading = false },
            onError = { msg ->
                error = msg
                loading = false
            }
        )
    }

    fun clearError() {
        error = null
    }
}
