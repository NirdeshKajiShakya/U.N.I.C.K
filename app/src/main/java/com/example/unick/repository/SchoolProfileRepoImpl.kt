package com.example.unick.repository

import com.example.unick.model.SchoolGalleryModel
import com.example.unick.model.SchoolProfileModel
import com.example.unick.model.SchoolReviewModel
import com.google.firebase.database.*

class SchoolProfileRepoImpl : SchoolProfileRepo {

    private val db = FirebaseDatabase.getInstance().reference

    override fun observeSchoolProfile(schoolId: String, onData: (SchoolProfileModel?) -> Unit, onError: (String) -> Unit) {
        db.child("SchoolForm").child(schoolId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val model = snapshot.getValue(SchoolProfileModel::class.java)
                    onData(model)
                }
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    override fun updateSchoolProfile(schoolId: String, updated: Map<String, Any?>, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.child("SchoolForm").child(schoolId)
            .updateChildren(updated)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Update failed") }
    }

    override fun observeGallery(schoolId: String, onData: (List<SchoolGalleryModel>) -> Unit, onError: (String) -> Unit) {
        db.child("school_gallery").child(schoolId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(SchoolGalleryModel::class.java) }
                    onData(list)
                }
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    override fun addGalleryImage(schoolId: String, imageUrl: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val ref = db.child("school_gallery").child(schoolId).push()
        val model = SchoolGalleryModel(
            id = ref.key ?: "",
            schoolId = schoolId,
            imageUrl = imageUrl,
            createdAt = System.currentTimeMillis()
        )
        ref.setValue(model)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Add gallery failed") }
    }

    override fun observeReviews(schoolId: String, onData: (List<SchoolReviewModel>) -> Unit, onError: (String) -> Unit) {
        db.child("school_reviews").child(schoolId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.getValue(SchoolReviewModel::class.java) }
                    onData(list.sortedByDescending { it.createdAt })
                }
                override fun onCancelled(error: DatabaseError) {
                    onError(error.message)
                }
            })
    }

    override fun addReview(schoolId: String, reviewerUid: String, rating: Int, comment: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val ref = db.child("school_reviews").child(schoolId).push()
        val model = SchoolReviewModel(
            id = ref.key ?: "",
            schoolId = schoolId,
            reviewerUid = reviewerUid,
            rating = rating.coerceIn(1, 5),
            comment = comment,
            createdAt = System.currentTimeMillis()
        )
        ref.setValue(model)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError(it.message ?: "Add review failed") }
    }
}
