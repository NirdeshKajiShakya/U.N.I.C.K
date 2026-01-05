package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unick.model.EditProfileModel
import com.example.unick.repo.EditProfileRepo
import com.example.unick.repo.EditProfileRepoImpl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// -------------------- UI STATE --------------------
data class EditUserProfileUiState(
    val profile: EditProfileModel = EditProfileModel(),
    val isLoading: Boolean = true,
    val saveSuccess: Boolean? = null,
    val deleteSuccess: Boolean? = null,
    val errorMessage: String? = null
)

// -------------------- VIEWMODEL --------------------
class EditUserProfileViewModel(
    private val repository: EditProfileRepo = EditProfileRepoImpl()
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
            "classPref" -> currentProfile.copy(classPref = value)
            "levelPref" -> currentProfile.copy(levelPref = value)
            "typePref" -> currentProfile.copy(typePref = value)
            else -> currentProfile
        }
        _uiState.value = _uiState.value.copy(profile = updatedProfile)
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
                        auth.currentUser?.delete()
                        _uiState.value = _uiState.value.copy(deleteSuccess = true)
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
        fun Factory(repository: EditProfileRepo = EditProfileRepoImpl()) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EditUserProfileViewModel(repository) as T
                }
            }
    }
}

