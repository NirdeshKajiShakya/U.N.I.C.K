package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.model.SchoolForm
import com.example.unick.repository.SchoolRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SchoolViewModel : ViewModel() {

    private val repository = SchoolRepository()

    var schoolName by mutableStateOf("")
    var location by mutableStateOf("")
    var totalStudents by mutableStateOf("")
    var establishedYear by mutableStateOf("")
    var principalName by mutableStateOf("")
    var contactNumber by mutableStateOf("")
    var email by mutableStateOf("")
    var website by mutableStateOf("")
    var curriculum by mutableStateOf("")
    var programsOffered by mutableStateOf("")
    var facilities by mutableStateOf("")
    var tuitionFee by mutableStateOf("")
    var admissionFee by mutableStateOf("")
    var scholarshipAvailable by mutableStateOf(false)
    var transportFacility by mutableStateOf(false)
    var hostelFacility by mutableStateOf(false)
    var extracurricular by mutableStateOf("")
    var description by mutableStateOf("")
    var imageUri by mutableStateOf<Uri?>(null)

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDataSaved = MutableStateFlow(false)
    val isDataSaved = _isDataSaved.asStateFlow()

    fun saveSchoolData(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid != null) {
                val school = createSchool(uid)
                repository.saveSchool(school, imageUri, context) { success ->
                    _isLoading.value = false
                    if (success) {
                        _isDataSaved.value = true
                    }
                }
            }
        }
    }

    private fun createSchool(uid: String): SchoolForm {
        return SchoolForm(
            uid = uid,
            imageUrl = null, // Will be set in the repository
            schoolName = schoolName,
            location = location,
            totalStudents = totalStudents,
            establishedYear = establishedYear,
            principalName = principalName,
            contactNumber = contactNumber,
            email = email,
            website = website,
            curriculum = curriculum,
            programsOffered = programsOffered,
            facilities = facilities,
            tuitionFee = tuitionFee,
            admissionFee = admissionFee,
            scholarshipAvailable = scholarshipAvailable,
            transportFacility = transportFacility,
            hostelFacility = hostelFacility,
            extracurricular = extracurricular,
            description = description
        )
    }
}
