package com.example.unick.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unick.repo.UserProfileRepo
import com.example.unick.view.ApplicationItemForUserProfile
import com.example.unick.view.ShortlistedSchoolForUserProfile
import com.example.unick.view.UserProfileModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// -------------------- STATES --------------------
sealed class UserProfileState<out T> {
    object Idle : UserProfileState<Nothing>()
    object Loading : UserProfileState<Nothing>()
    data class Success<T>(val data: T) : UserProfileState<T>()
    data class Error(val message: String) : UserProfileState<Nothing>()
}

// -------------------- VIEWMODEL --------------------
class UserProfileViewModel(
    private val repository: UserProfileRepo
) : ViewModel() {

    private val TAG = "UserProfileVM"

    // User profile state
    private val _userProfile = MutableStateFlow<UserProfileState<UserProfileModel>>(UserProfileState.Idle)
    val userProfile: StateFlow<UserProfileState<UserProfileModel>> = _userProfile

    // Shortlisted schools state
    private val _shortlistedSchools = MutableStateFlow<UserProfileState<List<ShortlistedSchoolForUserProfile>>>(UserProfileState.Idle)
    val shortlistedSchools: StateFlow<UserProfileState<List<ShortlistedSchoolForUserProfile>>> = _shortlistedSchools

    // Applications state
    private val _applications = MutableStateFlow<UserProfileState<List<ApplicationItemForUserProfile>>>(UserProfileState.Idle)
    val applications: StateFlow<UserProfileState<List<ApplicationItemForUserProfile>>> = _applications

    // -------------------- LOAD USER DATA --------------------
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            _userProfile.value = UserProfileState.Loading
            _shortlistedSchools.value = UserProfileState.Loading
            _applications.value = UserProfileState.Loading

            try {
                // Fetch user profile
                repository.getUserProfile(userId).fold(
                    onSuccess = { _userProfile.value = UserProfileState.Success(it) },
                    onFailure = { _userProfile.value = UserProfileState.Error(it.message ?: "Unknown error") }
                )

                // Fetch shortlisted schools
                repository.getShortlistedSchools(userId).fold(
                    onSuccess = { _shortlistedSchools.value = UserProfileState.Success(it) },
                    onFailure = { _shortlistedSchools.value = UserProfileState.Error(it.message ?: "Unknown error") }
                )

                // Fetch applications
                repository.getApplications(userId).fold(
                    onSuccess = { _applications.value = UserProfileState.Success(it) },
                    onFailure = { _applications.value = UserProfileState.Error(it.message ?: "Unknown error") }
                )

            } catch (e: Exception) {
                Log.e(TAG, "❌ Unexpected error loading user data", e)
                _userProfile.value = UserProfileState.Error("Unexpected error")
                _shortlistedSchools.value = UserProfileState.Error("Unexpected error")
                _applications.value = UserProfileState.Error("Unexpected error")
            }
        }
    }

    // -------------------- RESET STATES --------------------
    fun resetStates() {
        _userProfile.value = UserProfileState.Idle
        _shortlistedSchools.value = UserProfileState.Idle
        _applications.value = UserProfileState.Idle
    }

    // -------------------- FACTORY --------------------
    companion object {
        fun Factory(repository: UserProfileRepo) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserProfileViewModel(repository) as T
            }
        }
    }
}
