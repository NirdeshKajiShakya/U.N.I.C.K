package com.example.unick.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.unick.model.SchoolForm
import com.example.unick.repository.SchoolRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
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
    private val auth = FirebaseAuth.getInstance()
    private val database =
        FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")

    /* ---------------- FORM STATES ---------------- */

    var schoolName by mutableStateOf("")
    var location by mutableStateOf("")
    var googleMapUrl by mutableStateOf("")
    var latitude by mutableStateOf(0.0)
    var longitude by mutableStateOf(0.0)

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

    /* ---------------- UI STATES ---------------- */

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

    /* ---------------- EDIT MODE STATE ---------------- */

    private val _currentSchool = MutableStateFlow<SchoolForm?>(null)
    val currentSchool = _currentSchool.asStateFlow()

    init {
        fetchSchools()
        checkUserType()
    }

    /* ---------------- USER TYPE ---------------- */

    private fun checkUserType() {
        val uid = auth.currentUser?.uid ?: return
        val usersRef = database.getReference("Users")
        val schoolsRef = database.getReference("schools")

        usersRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    _userType.value = UserType.Normal
                } else {
                    schoolsRef.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            _userType.value =
                                if (snapshot.exists()) UserType.School else UserType.Unknown
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

    /* ---------------- FETCH SCHOOL FOR EDIT ---------------- */

    fun fetchSchoolIfExists() {
        val uid = auth.currentUser?.uid ?: return

        // ✅ IMPORTANT: read from SchoolForm node (not schools)
        val ref = database.getReference("SchoolForm").child(uid)

        _isLoading.value = true

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _isLoading.value = false

                if (snapshot.exists()) {
                    val school = snapshot.getValue(SchoolForm::class.java)
                    _currentSchool.value = school
                    school?.let { populateForm(it) }
                } else {
                    // no form data exists yet -> keep empty form
                    _currentSchool.value = null
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }


    private fun populateForm(school: SchoolForm) {
        schoolName = school.schoolName
        location = school.location
        totalStudents = school.totalStudents
        establishedYear = school.establishedYear
        principalName = school.principalName
        contactNumber = school.contactNumber
        email = school.email
        website = school.website
        curriculum = school.curriculum
        programsOffered = school.programsOffered
        facilities = school.facilities
        tuitionFee = school.tuitionFee
        admissionFee = school.admissionFee
        scholarshipAvailable = school.scholarshipAvailable
        transportFacility = school.transportFacility
        hostelFacility = school.hostelFacility
        extracurricular = school.extracurricular
        description = school.description
        googleMapUrl = school.googleMapUrl
        latitude = school.latitude
        longitude = school.longitude

    }

    /* ---------------- SAVE OR UPDATE ---------------- */

    fun saveOrUpdateSchool(context: Context) {
        val uid = auth.currentUser?.uid ?: return

        if (schoolName.isBlank() || location.isBlank() || contactNumber.isBlank()) {
            Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true

            val school = SchoolForm(
                uid = uid,
                imageUrl = null,
                schoolName = schoolName,
                location = location,
                googleMapUrl = googleMapUrl,
                latitude = latitude,
                longitude = longitude,
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

            repository.saveSchool(school, imageUri, context) { success ->
                _isLoading.value = false
                if (success) {
                    _isDataSaved.value = true
                    fetchSchools()
                }
            }
        }
    }

    /* ---------------- FETCH ALL SCHOOLS ---------------- */

    fun fetchSchools() {
        viewModelScope.launch {
            _isLoadingSchools.value = true
            repository.fetchAllSchools { schoolsList ->
                _schools.value = schoolsList
                _isLoadingSchools.value = false
            }
        }
    }

    /* ---------------- ADMIN ACTIONS ---------------- */

    fun verifySchool(uid: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.updateSchoolVerificationStatus(uid, true) {
                fetchSchools()
                onResult(it)
            }
        }
    }

    fun rejectSchool(uid: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            repository.updateSchoolRejectionStatus(uid, true) {
                fetchSchools()
                onResult(it)
            }
        }
    }
}
