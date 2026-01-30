package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserLoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    fun onEmailChange(value: String) {
        _email.value = value
        _errorMessage.value = null
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null
    }

    fun onRememberMeChange(value: Boolean) {
        _rememberMe.value = value
    }

    fun login() {
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _errorMessage.value = "Please enter a valid email"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(_email.value.trim(), _password.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val uid = firebaseUser.uid
                        val userRef = db.getReference("Users").child(uid)

                        // CHECK 1: Verify user exists in "Users" node
                        userRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                                if (snapshot.exists()) {
                                    // CORRECT ROLE: User is a Student
                                    val loginData = hashMapOf(
                                        "lastLogin" to System.currentTimeMillis()
                                    )
                                    userRef.updateChildren(loginData as Map<String, Any>)
                                        .addOnCompleteListener {
                                            _isLoading.value = false
                                            _loginSuccess.value = true
                                        }
                                } else {
                                    // WRONG ROLE or NO ACCOUNT
                                    // Check if they are a School or Admin to give better errors
                                    checkOtherRolesAndSignOut(uid)
                                }
                            }

                            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                                _isLoading.value = false
                                _errorMessage.value = "Database error: ${error.message}"
                                auth.signOut()
                            }
                        })
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "Login failed: user is null"
                    }
                } else {
                    _isLoading.value = false
                    val exception = task.exception
                    _errorMessage.value = when {
                        exception?.message?.contains("user-not-found", ignoreCase = true) == true ->
                            "Account not found. This email is not registered."
                        exception?.message?.contains("wrong-password", ignoreCase = true) == true ->
                            "Incorrect password. Please try again."
                        exception?.message?.contains("invalid-credential", ignoreCase = true) == true ->
                            "Invalid credentials. Please check your email and password."
                        exception?.message?.contains("too-many-requests", ignoreCase = true) == true ->
                            "Too many login attempts. Please try again later."
                        else -> "Login failed. ${exception?.message}"
                    }
                }
            }
    }

    private fun checkOtherRolesAndSignOut(uid: String) {
        val schoolsRef = db.getReference("schools").child(uid)
        schoolsRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(schoolSnap: com.google.firebase.database.DataSnapshot) {
                if (schoolSnap.exists()) {
                    _isLoading.value = false
                    _errorMessage.value = "This email is registered as a School account. Please use the School login portal."
                    auth.signOut()
                } else {
                    // Check Admin
                    db.getReference("Admins").child(uid).addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                        override fun onDataChange(adminSnap: com.google.firebase.database.DataSnapshot) {
                            _isLoading.value = false
                            if (adminSnap.exists()) {
                                _errorMessage.value = "This email is registered as an Admin account. Please use the Admin login portal."
                            } else {
                                _errorMessage.value = "Account not found. This email is not registered as a Student."
                            }
                            auth.signOut()
                        }
                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                            _isLoading.value = false
                            _errorMessage.value = "Verification failed."
                            auth.signOut()
                        }
                    })
                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                _isLoading.value = false
                _errorMessage.value = "Verification failed."
                auth.signOut()
            }
        })
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}