package com.example.unick.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.repository.cloudinary.CloudinaryRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI state holder
data class UploadState(
    val loading: Boolean = false,
    val uploadedUrl: String? = null,
    val error: String? = null
)

class SchoolMediaViewModel(
    private val cloudinaryRepo: CloudinaryRepo
) : ViewModel() {

    private val _bannerState = MutableStateFlow(UploadState())
    val bannerState: StateFlow<UploadState> = _bannerState

    fun uploadBanner(uri: Uri) {
        _bannerState.value = UploadState(loading = true)

        viewModelScope.launch {
            val result = cloudinaryRepo.uploadImage(
                imageUri = uri,
                folder = "unick/schools/banners"
            )

            result
                .onSuccess { url ->
                    _bannerState.value = UploadState(
                        loading = false,
                        uploadedUrl = url
                    )
                }
                .onFailure { throwable ->
                    _bannerState.value = UploadState(
                        loading = false,
                        error = throwable.message ?: "Upload failed"
                    )
                }
        }
    }
}
