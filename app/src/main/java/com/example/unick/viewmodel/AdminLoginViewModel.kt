package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AdminLoginViewModel : ViewModel() {

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
        // Trim inputs to remove whitespace
        val trimmedEmail = _email.value.trim()
        val trimmedPassword = _password.value.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty"
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
            _errorMessage.value = "Please enter a valid email"
            return
        }

        if (trimmedPassword.length < 6) {
            _errorMessage.value = "Password must be at least 6 characters"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        auth.signInWithEmailAndPassword(trimmedEmail, trimmedPassword)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        db.getReference("Admins").child(firebaseUser.uid).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    _isLoading.value = false
                                    _loginSuccess.value = true
                                } else {
                                    _isLoading.value = false
                                    _errorMessage.value = "You are not an admin."
                                    auth.signOut()
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
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
                            "Admin account not found. Check your email address."
                        exception?.message?.contains("wrong-password", ignoreCase = true) == true ->
                            "Incorrect password. Please try again."
                        exception?.message?.contains("too-many-requests", ignoreCase = true) == true ->
                            "Too many login attempts. Please try again later."
                        exception?.message?.contains("malformed", ignoreCase = true) == true ->
                            "Invalid email or password format. Please check and try again."
                        else -> exception?.message ?: "Login failed. Please try again."
                    }
                }
            }
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}