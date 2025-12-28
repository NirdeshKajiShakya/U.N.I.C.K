package com.example.unick.viewmodel

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

    private val _submitState = MutableStateFlow<SubmitState>(SubmitState.Idle)
    val submitState: StateFlow<SubmitState> = _submitState

    fun submitApplication(application: StudentApplication) {
        viewModelScope.launch {
            _submitState.value = SubmitState.Loading
            val result = repository.submitApplication(application)
            _submitState.value = if (result.isSuccess) {
                SubmitState.Success
            } else {
                SubmitState.Error(result.exceptionOrNull()?.message ?: "Unknown error occurred")
            }
        }
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