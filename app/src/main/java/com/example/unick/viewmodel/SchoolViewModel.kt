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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserType {
    object Normal : UserType()
    object School : UserType()
    object Unknown : UserType()
}

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

    private val _schools = MutableStateFlow<List<SchoolForm>>(emptyList())
    val schools = _schools.asStateFlow()

    private val _isLoadingSchools = MutableStateFlow(false)
    val isLoadingSchools = _isLoadingSchools.asStateFlow()

    private val _userType = MutableStateFlow<UserType>(UserType.Unknown)
    val userType = _userType.asStateFlow()

    init {
        fetchSchools()
        checkUserType()
    }

    fun checkUserType() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val usersRef = FirebaseDatabase.getInstance().getReference("Users")
        val schoolsRef = FirebaseDatabase.getInstance().getReference("schools")

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _userType.value = UserType.Normal
                } else {
                    // If not in Users, check in schools
                    schoolsRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                _userType.value = UserType.School
                            } else {
                                _userType.value = UserType.Unknown
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            _userType.value = UserType.Unknown
                        }
                    })
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _userType.value = UserType.Unknown
            }
        })
    }

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
                        fetchSchools()
                    }
                }
            }
        }
    }

    fun fetchSchools() {
        viewModelScope.launch {
            _isLoadingSchools.value = true
            repository.fetchAllSchools { schoolsList ->
                _schools.value = schoolsList
                _isLoadingSchools.value = false
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