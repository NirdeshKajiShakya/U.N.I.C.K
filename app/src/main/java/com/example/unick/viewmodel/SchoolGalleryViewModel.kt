package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.firebase.database.*

class SchoolGalleryViewModel(private val appContext: Context) : ViewModel() {

    // ✅ MUST be Compose state
    var loading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    val galleryImageUrls = mutableStateListOf<String>()

    private val db =
        FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")

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
        MediaManager.init(appContext.applicationContext, config)
        cloudinaryInitialized = true
    }

    fun loadGallery(schoolId: String) {
        if (schoolId.isBlank()) {
            error = "School ID missing"
            loading = false
            return
        }

        loading = true
        error = null
        galleryImageUrls.clear()

        galleryRef(schoolId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val urls = mutableListOf<String>()
                for (child in snapshot.children) {
                    val url = child.getValue(String::class.java)
                    if (!url.isNullOrBlank()) urls.add(url)
                }

                galleryImageUrls.clear()
                galleryImageUrls.addAll(urls.reversed()) // newest first

                loading = false
            }

            override fun onCancelled(err: DatabaseError) {
                error = err.message
                loading = false
            }
        })
    }

    /** ✅ Upload multiple images one-by-one */
    fun uploadGalleryImages(
        schoolId: String,
        imageUris: List<Uri>,
        onMessage: (String) -> Unit
    ) {
        if (schoolId.isBlank()) {
            onMessage("School ID missing!")
            return
        }
        if (imageUris.isEmpty()) return

        initCloudinary()
        loading = true
        error = null

        var index = 0
        var successCount = 0

        fun uploadNext() {
            if (index >= imageUris.size) {
                loading = false
                onMessage("✅ Uploaded $successCount/${imageUris.size} images")
                loadGallery(schoolId)
                return
            }

            val uri = imageUris[index]
            index++

            onMessage("Uploading ${index}/${imageUris.size} ...")

            MediaManager.get().upload(uri).callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    Log.d("GalleryUpload", "Upload started")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["url"]?.toString().orEmpty()
                    if (url.isBlank()) {
                        Log.e("GalleryUpload", "Empty URL from cloudinary")
                        uploadNext()
                        return
                    }

                    val pushKey =
                        galleryRef(schoolId).push().key ?: System.currentTimeMillis().toString()

                    galleryRef(schoolId).child(pushKey).setValue(url)
                        .addOnSuccessListener {
                            successCount++
                            uploadNext()
                        }
                        .addOnFailureListener { e ->
                            Log.e("GalleryUpload", "DB save failed: ${e.message}")
                            uploadNext()
                        }
                }

                override fun onError(requestId: String, errorInfo: ErrorInfo) {
                    Log.e("GalleryUpload", "Upload error: ${errorInfo.description}")
                    uploadNext()
                }

                override fun onReschedule(requestId: String, errorInfo: ErrorInfo) {
                    Log.e("GalleryUpload", "Reschedule: ${errorInfo.description}")
                    uploadNext()
                }
            }).dispatch()
        }

        uploadNext()
    }
}
