package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.*

class SchoolGalleryViewModel(private val appContext: Context) : ViewModel() {

    var loading = mutableStateOf(false).value
        private set

    var error = mutableStateOf<String?>(null).value
        private set

    val galleryImageUrls = mutableStateListOf<String>()

    private val db =
        FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")

    // ✅ storing gallery in a separate node
    private fun galleryRef(schoolId: String): DatabaseReference {
        return db.getReference("SchoolGallery").child(schoolId).child("images")
    }

    companion object {
        private var cloudinaryInitialized = false
    }

    private fun initCloudinary() {
        if (cloudinaryInitialized) return

        val config = mapOf(
            "cloud_name" to "dbelcobpj",
            "api_key" to "947959738234575",
            "api_secret" to "IhyBcq5TBPqdDzA5qa5LjyrQhAY"
        )
        MediaManager.init(appContext, config)
        cloudinaryInitialized = true
    }

    fun loadGallery(schoolId: String) {
        if (schoolId.isBlank()) return

        loading = true
        error = null
        galleryImageUrls.clear()

        galleryRef(schoolId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                loading = false
                val urls = mutableListOf<String>()
                for (child in snapshot.children) {
                    val url = child.getValue(String::class.java)
                    if (!url.isNullOrBlank()) urls.add(url)
                }
                galleryImageUrls.addAll(urls.reversed()) // newest first
            }

            override fun onCancelled(err: DatabaseError) {
                loading = false
                error = err.message
            }
        })
    }

    fun uploadGalleryImage(
        schoolId: String,
        imageUri: Uri,
        onMessage: (String) -> Unit
    ) {
        if (schoolId.isBlank()) {
            onMessage("School ID missing!")
            return
        }

        initCloudinary()

        loading = true
        error = null
        onMessage("Uploading image...")

        MediaManager.get().upload(imageUri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("GalleryUpload", "Upload started")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val url = resultData["url"]?.toString().orEmpty()
                if (url.isBlank()) {
                    loading = false
                    onMessage("Upload failed: empty url")
                    return
                }

                // ✅ save URL to Firebase
                val pushKey = galleryRef(schoolId).push().key ?: System.currentTimeMillis().toString()
                galleryRef(schoolId).child(pushKey).setValue(url)
                    .addOnSuccessListener {
                        loading = false
                        onMessage("✅ Uploaded!")
                        loadGallery(schoolId)
                    }
                    .addOnFailureListener { e ->
                        loading = false
                        error = e.message
                        onMessage("Failed saving to database")
                    }
            }

            override fun onError(requestId: String, errorInfo: ErrorInfo) {
                loading = false
                error = errorInfo.description
                onMessage("Upload error: ${errorInfo.description}")
            }

            override fun onReschedule(requestId: String, errorInfo: ErrorInfo) {
                loading = false
                error = errorInfo.description
                onMessage("Upload rescheduled: ${errorInfo.description}")
            }
        }).dispatch()
    }
}
