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
                        // Update last login time in Realtime Database
                        val loginData = hashMapOf(
                            "lastLogin" to System.currentTimeMillis()
                        )

                        db.getReference("Users").child(firebaseUser.uid)
                            .updateChildren(loginData as Map<String, Any>)
                            .addOnSuccessListener {
                                _isLoading.value = false
                                _loginSuccess.value = true
                                android.util.Log.d("LoginDebug", "Login successful and database updated")
                            }
                            .addOnFailureListener { e ->
                                _isLoading.value = false
                                _loginSuccess.value = true // Still allow login even if update fails
                                android.util.Log.e("LoginDebug", "Database update failed: ${e.message}")
                            }
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = "Login failed: user is null"
                    }
                } else {
                    _isLoading.value = false
                    _errorMessage.value = task.exception?.message ?: "Login failed"
                    android.util.Log.e("LoginError", "Login error: ${task.exception?.message}")
                }
            }
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}