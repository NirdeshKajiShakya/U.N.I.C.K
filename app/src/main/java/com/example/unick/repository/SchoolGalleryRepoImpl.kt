package com.example.unick.repository

import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class SchoolGalleryRepoImpl : SchoolGalleryRepo {

    private val db = FirebaseDatabase.getInstance().reference

    override suspend fun getGallery(schoolId: String): Result<List<String>> = runCatching {
        val snap = db.child("schools").child(schoolId).child("galleryImages").get().await()
        val map = snap.value as? Map<*, *>

        // If stored as map: { key1:url1, key2:url2 }
        map?.values?.mapNotNull { it?.toString() } ?: emptyList()
    }

    override suspend fun addImages(schoolId: String, newUrls: List<String>): Result<List<String>> = runCatching {
        val galleryRef = db.child("schools").child(schoolId).child("galleryImages")

        // push each new url
        newUrls.forEach { url ->
            val key = galleryRef.push().key ?: return@forEach
            galleryRef.child(key).setValue(url).await()
        }

        // return updated list
        getGallery(schoolId).getOrElse { emptyList() }
    }
}
