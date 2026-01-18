package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.model.School
import com.example.unick.repository.ShortlistRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShortlistViewModel(
    private val repository: ShortlistRepository = ShortlistRepository()
) : ViewModel() {

    private val _schools = MutableStateFlow<List<School>>(emptyList())
    val schools: StateFlow<List<School>> = _schools.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    init {
        loadShortlistedSchools()
    }

    fun loadShortlistedSchools() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.getShortlistedSchools().collect { result ->
                result.onSuccess { schoolList ->
                    _schools.value = schoolList
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message ?: "An error occurred"
                    _isLoading.value = false
                }
            }
        }
    }

    fun addToShortlist(schoolId: String) {
        viewModelScope.launch {
            val result = repository.addToShortlist(schoolId)
            result.onSuccess {
                _successMessage.value = "School added to shortlist"
                loadShortlistedSchools()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to add school"
            }
        }
    }

    fun removeFromShortlist(schoolId: String) {
        viewModelScope.launch {
            val result = repository.removeFromShortlist(schoolId)
            result.onSuccess {
                _successMessage.value = "School removed from shortlist"
                loadShortlistedSchools()
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to remove school"
            }
        }
    }

    fun refresh() {
        loadShortlistedSchools()
    }

    fun clearShortlist() {
        viewModelScope.launch {
            val result = repository.clearShortlist()
            result.onSuccess {
                _schools.value = emptyList()
                _successMessage.value = "Shortlist cleared"
            }.onFailure { exception ->
                _error.value = exception.message ?: "Failed to clear shortlist"
            }
        }
    }

    fun searchSchools(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository.searchSchools(query).collect { result ->
                result.onSuccess { schoolList ->
                    _schools.value = schoolList
                    _isLoading.value = false
                }.onFailure { exception ->
                    _error.value = exception.message ?: "Search failed"
                    _isLoading.value = false
                }
            }
        }
    }
}