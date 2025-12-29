package com.example.unick.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.unick.model.StudentApplication
import com.example.unick.repo.ApplicationRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class SubmitState {
    object Idle : SubmitState()
    object Loading : SubmitState()
    object Success : SubmitState()
    data class Error(val message: String) : SubmitState()
}

class StudentApplicationViewModel(
    private val repository: ApplicationRepo
) : ViewModel() {

    private val TAG = "StudentApplicationVM"
    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    fun submitApplication(application: StudentApplication) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "📝 Starting application submission for: ${application.fullName}")
                _submitState.value = SubmitState.Loading

                val result = repository.submitApplication(application)

                _submitState.value = if (result.isSuccess) {
                    Log.d(TAG, "✅ Application submitted successfully!")
                    SubmitState.Success
                } else {
                    val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error occurred"
                    Log.e(TAG, "❌ Submission failed: $errorMsg")
                    SubmitState.Error(errorMsg)
                }
            } catch (e: Exception) {
                // Catch any unexpected errors to prevent stuck loading state
                val errorMsg = "Unexpected error: ${e.message}"
                Log.e(TAG, "❌ Unexpected error during submission", e)
                _submitState.value = SubmitState.Error(errorMsg)
            }
        }
    }

    /**
     * Reset the submit state back to Idle.
     * Call this when starting a new application or dismissing error dialogs.
     */
    fun resetState() {
        Log.d(TAG, "🔄 Resetting state to Idle")
        _submitState.value = SubmitState.Idle
    }

    // Factory for dependency injection
    companion object {
        fun Factory(repository: ApplicationRepo) = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return StudentApplicationViewModel(repository) as T
            }
        }
    }
}