package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.model.SchoolProfileModel
import com.example.unick.repository.SchoolProfileRepo
import com.example.unick.repository.SchoolProfileRepoImpl
import com.example.unick.repository.cloudinary.CloudinaryRepo
import com.example.unick.repository.cloudinary.CloudinaryRepoImpl
import com.example.unick.utils.CloudinaryConfig
import kotlinx.coroutines.launch

class SchoolEditProfileViewModel(
    context: Context,
    private val repo: SchoolProfileRepo = SchoolProfileRepoImpl(),
    private val cloudinary: CloudinaryRepo = CloudinaryRepoImpl(context)
) : ViewModel() {

    var loading by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    var schoolProfile by mutableStateOf<SchoolProfileModel?>(null)
        private set

    private var schoolId: String = ""

    // Editable fields
    var schoolName by mutableStateOf("")
    var location by mutableStateOf("")
    var email by mutableStateOf("")
    var contactNumber by mutableStateOf("")
    var description by mutableStateOf("")

    fun loadSchool(id: String) {
        schoolId = id
        loading = true

        repo.observeSchoolProfile(
            schoolId = id,
            onData = { profile ->
                schoolProfile = profile
                schoolName = profile?.schoolName ?: ""
                location = profile?.location ?: ""
                email = profile?.email ?: ""
                contactNumber = profile?.contactNumber ?: ""
                description = profile?.description ?: ""
                loading = false
            },
            onError = {
                error = it
                loading = false
            }
        )
    }

    fun saveChanges() {
        if (schoolId.isBlank()) {
            error = "School id missing"
            return
        }

        val updated = mapOf(
            "schoolName" to schoolName,
            "location" to location,
            "email" to email,
            "contactNumber" to contactNumber,
            "description" to description
        )

        loading = true
        repo.updateSchoolProfile(
            schoolId = schoolId,
            updated = updated,
            onSuccess = { loading = false },
            onError = {
                error = it
                loading = false
            }
        )
    }

    fun uploadBanner(uri: Uri) {
        if (schoolId.isBlank()) return
        loading = true

        viewModelScope.launch {
            val res = cloudinary.uploadImage(uri, CloudinaryConfig.DEFAULT_FOLDER)
            res.fold(
                onSuccess = { url ->
                    repo.updateSchoolProfile(
                        schoolId = schoolId,
                        updated = mapOf("imageUrl" to url),
                        onSuccess = { loading = false },
                        onError = { msg -> error = msg; loading = false }
                    )
                },
                onFailure = {
                    error = it.message
                    loading = false
                }
            )
        }
    }

    fun uploadGalleryImages(uris: List<Uri>) {
        if (schoolId.isBlank()) return
        loading = true

        viewModelScope.launch {
            try {
                for (u in uris) {
                    val res = cloudinary.uploadImage(u, CloudinaryConfig.DEFAULT_FOLDER)
                    res.getOrNull()?.let { url ->
                        repo.addGalleryImage(
                            schoolId = schoolId,
                            imageUrl = url,
                            onSuccess = {},
                            onError = { error = it }
                        )
                    }
                }
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }
}
