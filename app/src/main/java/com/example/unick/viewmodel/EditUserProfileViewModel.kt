package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unick.model.EditProfileModel
import com.example.unick.repo.EditProfileRepo
import com.example.unick.repo.EditProfileRepoImpl
import com.example.unick.repository.cloudinary.CloudinaryRepoImpl
import com.example.unick.utils.CloudinaryConfig
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// -------------------- UI STATE --------------------
data class EditUserProfileUiState(
    val profile: EditProfileModel = EditProfileModel(),
    val isLoading: Boolean = true,
    val isUploadingImage: Boolean = false,
    val saveSuccess: Boolean? = null,
    val deleteSuccess: Boolean? = null,
    val errorMessage: String? = null
)

// -------------------- VIEWMODEL --------------------
class EditUserProfileViewModel(
    private val repository: EditProfileRepo = EditProfileRepoImpl(),
    private val context: Context? = null
) : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid

    private val _uiState = MutableStateFlow(EditUserProfileUiState())
    val uiState: StateFlow<EditUserProfileUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    // -------------------- LOAD USER PROFILE --------------------
    private fun loadUserProfile() {
        userId?.let { uid ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isLoading = true)
                repository.getUserProfile(uid)
                    .onSuccess { profile ->
                        _uiState.value = _uiState.value.copy(
                            profile = profile,
                            isLoading = false
                        )
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = e.message ?: "Failed to load profile"
                        )
                    }
            }
        } ?: run {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "User not logged in"
            )
        }
    }

    // -------------------- UPDATE FIELD --------------------
    fun updateField(field: String, value: String) {
        val currentProfile = _uiState.value.profile
        val updatedProfile = when (field) {
            "fullName" -> currentProfile.copy(fullName = value)
            "email" -> currentProfile.copy(email = value)
            "contact" -> currentProfile.copy(contact = value)
            "dob" -> currentProfile.copy(dob = value)
            "gender" -> currentProfile.copy(gender = value)
            "location" -> currentProfile.copy(location = value)
            "profilePictureUrl" -> currentProfile.copy(profilePictureUrl = value)
            else -> currentProfile
        }
        _uiState.value = _uiState.value.copy(profile = updatedProfile)
    }

    // -------------------- UPLOAD PROFILE PICTURE --------------------
    fun uploadProfilePicture(uri: Uri) {
        context?.let { ctx ->
            viewModelScope.launch {
                _uiState.value = _uiState.value.copy(isUploadingImage = true)

                val cloudinary = CloudinaryRepoImpl(ctx)
                val result = cloudinary.uploadImage(uri, CloudinaryConfig.DEFAULT_FOLDER)

                result.fold(
                    onSuccess = { url ->
                        val updatedProfile = _uiState.value.profile.copy(profilePictureUrl = url)
                        _uiState.value = _uiState.value.copy(
                            profile = updatedProfile,
                            isUploadingImage = false
                        )
                        // Auto-save after successful upload
                        saveUserProfile()
                    },
                    onFailure = { e ->
                        _uiState.value = _uiState.value.copy(
                            isUploadingImage = false,
                            errorMessage = "Failed to upload image: ${e.message}"
                        )
                    }
                )
            }
        }
    }

    // -------------------- SAVE USER PROFILE --------------------
    fun saveUserProfile() {
        userId?.let { uid ->
            viewModelScope.launch {
                repository.updateUserProfile(uid, _uiState.value.profile)
                    .onSuccess {
                        _uiState.value = _uiState.value.copy(saveSuccess = true)
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            saveSuccess = false,
                            errorMessage = e.message ?: "Failed to update profile"
                        )
                    }
            }
        }
    }

    // -------------------- DELETE ACCOUNT --------------------
    fun deleteAccount() {
        userId?.let { uid ->
            viewModelScope.launch {
                repository.deleteUserProfile(uid)
                    .onSuccess {
                        // Also delete Firebase Auth user
                        try {
                            auth.currentUser?.delete()?.await()
                            _uiState.value = _uiState.value.copy(deleteSuccess = true)
                        } catch (e: Exception) {
                            _uiState.value = _uiState.value.copy(
                                deleteSuccess = false,
                                errorMessage = "Account deleted but auth deletion failed: ${e.message}"
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.value = _uiState.value.copy(
                            deleteSuccess = false,
                            errorMessage = e.message ?: "Failed to delete account"
                        )
                    }
            }
        }
    }

    // -------------------- CLEAR STATUS --------------------
    fun clearStatus() {
        _uiState.value = _uiState.value.copy(
            saveSuccess = null,
            deleteSuccess = null,
            errorMessage = null
        )
    }

    // -------------------- FACTORY --------------------
    companion object {
        fun Factory(repository: EditProfileRepo = EditProfileRepoImpl(), context: Context? = null) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EditUserProfileViewModel(repository, context) as T
                }
            }
    }
}