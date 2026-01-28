package com.example.unick.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.unick.model.SchoolForm
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SchoolRepository {

    private val database =
        FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
            .getReference("SchoolForm")


    companion object {
        private var isCloudinaryInitialized = false
    }

    private fun initializeCloudinary(context: Context) {
        if (!isCloudinaryInitialized) {
            val config = mapOf(
                "cloud_name" to "dbelcobpj",
                "api_key" to "947959738234575",
                "api_secret" to "IhyBcq5TBPqdDzA5qa5LjyrQhAY"
            )
            MediaManager.init(context.applicationContext, config)
            isCloudinaryInitialized = true
        }
    }

    private fun uploadImage(
        context: Context,
        uri: Uri,
        onUploadSuccess: (String) -> Unit,
        onUploadFailure: () -> Unit
    ) {
        initializeCloudinary(context)
        MediaManager.get().upload(uri).callback(object : UploadCallback {
            override fun onStart(requestId: String) {
                Log.d("Cloudinary", "Upload started")
            }

            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                val imageUrl = resultData["url"].toString()
                onUploadSuccess(imageUrl)
                Log.d("Cloudinary", "Upload successful: $imageUrl")
            }

            override fun onError(requestId: String, error: ErrorInfo) {
                Log.e("Cloudinary", "Upload error: ${error.description}")
                onUploadFailure()
            }

            override fun onReschedule(requestId: String, error: ErrorInfo) {
                Log.e("Cloudinary", "Upload reschedule: ${error.description}")
                onUploadFailure()
            }
        }).dispatch()
    }

    fun saveSchool(school: SchoolForm, imageUri: Uri?, context: Context, onComplete: (Boolean) -> Unit) {
        if (imageUri != null) {
            uploadImage(context, imageUri,
                onUploadSuccess = { imageUrl ->
                    val schoolWithImage = school.copy(imageUrl = imageUrl)
                    database.child(schoolWithImage.uid).setValue(schoolWithImage)
                        .addOnCompleteListener { task ->
                            onComplete(task.isSuccessful)
                        }
                },
                onUploadFailure = {
                    onComplete(false)
                }
            )
        } else {
            database.child(school.uid).setValue(school)
                .addOnCompleteListener { task ->
                    onComplete(task.isSuccessful)
                }
        }
    }

    fun fetchAllSchools(onComplete: (List<SchoolForm>) -> Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val schoolsList = mutableListOf<SchoolForm>()
                for (childSnapshot in snapshot.children) {
                    val school = childSnapshot.getValue(SchoolForm::class.java)
                    // CRITICAL FIX: Assign the Firebase Key (UID) to the object
                    school?.let { 
                        it.uid = childSnapshot.key ?: ""
                        schoolsList.add(it) 
                    }
                }
                onComplete(schoolsList)
                Log.d("SchoolRepository", "Fetched ${schoolsList.size} schools")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("SchoolRepository", "Error fetching schools: ${error.message}")
                onComplete(emptyList())
            }
        })
    }
    fun updateSchoolVerificationStatus(uid: String, isVerified: Boolean, onComplete: (Boolean) -> Unit) {
        val updates = mapOf<String, Any>(
            "verified" to isVerified,
            "rejected" to false // If verifying, ensure rejected is false
        )
        database.child(uid).updateChildren(updates)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
            .addOnFailureListener { e ->
                Log.e("SchoolRepository", "Error updating verification status: ${e.message}")
                onComplete(false)
            }
    }

    fun updateSchoolRejectionStatus(uid: String, isRejected: Boolean, onComplete: (Boolean) -> Unit) {
        val updates = mapOf<String, Any>(
            "verified" to false, // If rejecting, ensure verified is false
            "rejected" to isRejected
        )
        database.child(uid).updateChildren(updates)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
            .addOnFailureListener { e ->
                Log.e("SchoolRepository", "Error updating rejection status: ${e.message}")
                onComplete(false)
            }
    }
}