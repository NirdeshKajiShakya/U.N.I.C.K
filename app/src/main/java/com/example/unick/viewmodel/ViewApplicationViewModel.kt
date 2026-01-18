package com.example.unick.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unick.model.StudentApplication
import com.example.unick.repo.ViewApplicationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * UI State for the applications list screen
 */
sealed class ApplicationsListState {
    object Loading : ApplicationsListState()
    data class Success(val applications: List<StudentApplication>) : ApplicationsListState()
    data class Error(val message: String) : ApplicationsListState()
}

/**
 * UI State for application detail screen
 */
sealed class ApplicationDetailState {
    object Loading : ApplicationDetailState()
    data class Success(val application: StudentApplication) : ApplicationDetailState()
    data class Error(val message: String) : ApplicationDetailState()
}

/**
 * UI State for status update operations
 */
sealed class StatusUpdateState {
    object Idle : StatusUpdateState()
    object Loading : StatusUpdateState()
    object Success : StatusUpdateState()
    data class Error(val message: String) : StatusUpdateState()
}

/**
 * ViewModel for school-side application management.
 * Handles listing applications, viewing details, and accepting/rejecting.
 */
class ViewApplicationViewModel(
    private val repository: ViewApplicationRepo
) : ViewModel() {

    private val TAG = "ViewApplicationVM"

    // List of applications state
    private val _applicationsState = MutableStateFlow<ApplicationsListState>(ApplicationsListState.Loading)
    val applicationsState: StateFlow<ApplicationsListState> = _applicationsState

    // Single application detail state
    private val _detailState = MutableStateFlow<ApplicationDetailState>(ApplicationDetailState.Loading)
    val detailState: StateFlow<ApplicationDetailState> = _detailState

    // Status update state (for accept/reject operations)
    private val _statusUpdateState = MutableStateFlow<StatusUpdateState>(StatusUpdateState.Idle)
    val statusUpdateState: StateFlow<StatusUpdateState> = _statusUpdateState

    // Currently selected application for detail view
    private val _selectedApplication = MutableStateFlow<StudentApplication?>(null)
    val selectedApplication: StateFlow<StudentApplication?> = _selectedApplication

    /**
     * Load all applications for a specific school.
     */
    fun loadApplicationsForSchool(schoolId: String) {
        viewModelScope.launch {
            Log.d(TAG, "📋 Loading applications for school: $schoolId")
            _applicationsState.value = ApplicationsListState.Loading

            val result = repository.getApplicationsForSchool(schoolId)

            _applicationsState.value = if (result.isSuccess) {
                val apps = result.getOrNull() ?: emptyList()
                Log.d(TAG, "✅ Loaded ${apps.size} applications")
                ApplicationsListState.Success(apps)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "❌ Failed to load applications: $error")
                ApplicationsListState.Error(error)
            }
        }
    }

    /**
     * Load a single application's details.
     */
    fun loadApplicationDetail(applicationId: String) {
        viewModelScope.launch {
            Log.d(TAG, "📄 Loading application detail: $applicationId")
            _detailState.value = ApplicationDetailState.Loading

            val result = repository.getApplicationById(applicationId)

            _detailState.value = if (result.isSuccess) {
                val app = result.getOrNull()
                if (app != null) {
                    _selectedApplication.value = app
                    Log.d(TAG, "✅ Loaded application for: ${app.fullName}")
                    ApplicationDetailState.Success(app)
                } else {
                    ApplicationDetailState.Error("Application not found")
                }
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "❌ Failed to load application: $error")
                ApplicationDetailState.Error(error)
            }
        }
    }

    /**
     * Accept an application.
     * Updates status to "accepted" and records reviewer info.
     */
    fun acceptApplication(applicationId: String, reviewedBy: String, schoolId: String) {
        updateStatus(applicationId, "accepted", reviewedBy, schoolId)
    }

    /**
     * Reject an application.
     * Updates status to "rejected" and records reviewer info.
     */
    fun rejectApplication(applicationId: String, reviewedBy: String, schoolId: String) {
        updateStatus(applicationId, "rejected", reviewedBy, schoolId)
    }

    /**
     * Internal method to update application status.
     */
    private fun updateStatus(applicationId: String, status: String, reviewedBy: String, schoolId: String) {
        viewModelScope.launch {
            Log.d(TAG, "🔄 Updating application $applicationId to: $status")
            _statusUpdateState.value = StatusUpdateState.Loading

            val result = repository.updateApplicationStatus(applicationId, status, reviewedBy)

            if (result.isSuccess) {
                Log.d(TAG, "✅ Status updated to: $status")
                _statusUpdateState.value = StatusUpdateState.Success

                // Update the selected application locally for immediate UI feedback
                _selectedApplication.value = _selectedApplication.value?.copy(
                    status = status,
                    reviewedBy = reviewedBy,
                    reviewedAt = System.currentTimeMillis()
                )

                // Refresh the applications list
                loadApplicationsForSchool(schoolId)
            } else {
                val error = result.exceptionOrNull()?.message ?: "Unknown error"
                Log.e(TAG, "❌ Failed to update status: $error")
                _statusUpdateState.value = StatusUpdateState.Error(error)
            }
        }
    }

    /**
     * Select an application for viewing details (from the list).
     */
    fun selectApplication(application: StudentApplication) {
        _selectedApplication.value = application
        _detailState.value = ApplicationDetailState.Success(application)
    }

    /**
     * Clear selection and go back to list view.
     */
    fun clearSelection() {
        _selectedApplication.value = null
        _detailState.value = ApplicationDetailState.Loading
    }

    /**
     * Reset the status update state back to idle.
     */
    fun resetStatusUpdateState() {
        _statusUpdateState.value = StatusUpdateState.Idle
    }

    // Factory for dependency injection
    companion object {
        fun Factory(repository: ViewApplicationRepo) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ViewApplicationViewModel(repository) as T
            }
        }
    }
}

