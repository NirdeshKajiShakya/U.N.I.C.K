package com.example.unick.repository

import com.example.unick.view.SchoolDataShortlist
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShortlistRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun userId(): String = auth.currentUser?.uid ?: ""

    suspend fun getShortlistedSchools(): List<SchoolDataShortlist> {
        val snapshot = firestore
            .collection("users")
            .document(userId())
            .collection("shortlist")
            .get()
            .await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(SchoolDataShortlist::class.java)
        }
    }

    suspend fun removeFromShortlist(schoolId: String) {
        firestore
            .collection("users")
            .document(userId())
            .collection("shortlist")
            .document(schoolId)
            .delete()
            .await()
    }
}
