package com.example.unick.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.model.SchoolForm
import com.example.unick.repo.CompareSchoolRepo
import com.example.unick.repo.CompareSchoolRepoImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CompareSchoolState {
    object Idle : CompareSchoolState()
    object Loading : CompareSchoolState()
    data class Success(val schools: List<SchoolForm>) : CompareSchoolState()
    data class Error(val message: String) : CompareSchoolState()
}

class CompareSchoolViewModel(
    private val repository: CompareSchoolRepo = CompareSchoolRepoImpl()
) : ViewModel() {

    private val TAG = "CompareSchoolViewModel"

    // All schools list for selection
    private val _schoolsState = MutableStateFlow<CompareSchoolState>(CompareSchoolState.Idle)
    val schoolsState: StateFlow<CompareSchoolState> = _schoolsState.asStateFlow()

    // Selected schools for comparison
    private val _school1 = MutableStateFlow<SchoolForm?>(null)
    val school1: StateFlow<SchoolForm?> = _school1.asStateFlow()

    private val _school2 = MutableStateFlow<SchoolForm?>(null)
    val school2: StateFlow<SchoolForm?> = _school2.asStateFlow()

    init {
        fetchAllSchools()
    }

    fun fetchAllSchools() {
        viewModelScope.launch {
            _schoolsState.value = CompareSchoolState.Loading
            try {
                repository.getAllSchools().fold(
                    onSuccess = { schools ->
                        Log.d(TAG, "✅ Fetched ${schools.size} schools")
                        _schoolsState.value = CompareSchoolState.Success(schools)
                    },
                    onFailure = { error ->
                        Log.e(TAG, "❌ Error fetching schools: ${error.message}")
                        _schoolsState.value = CompareSchoolState.Error(error.message ?: "Unknown error")
                    }
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception fetching schools", e)
                _schoolsState.value = CompareSchoolState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun selectSchool1(school: SchoolForm?) {
        _school1.value = school
        Log.d(TAG, "School 1 selected: ${school?.schoolName}")
    }

    fun selectSchool2(school: SchoolForm?) {
        _school2.value = school
        Log.d(TAG, "School 2 selected: ${school?.schoolName}")
    }

    fun clearSchool1() {
        _school1.value = null
    }

    fun clearSchool2() {
        _school2.value = null
    }

    fun clearAll() {
        _school1.value = null
        _school2.value = null
    }
}

