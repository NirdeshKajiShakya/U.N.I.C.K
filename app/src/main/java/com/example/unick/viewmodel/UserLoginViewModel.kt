package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserLoginViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()

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
        _errorMessage.value = null // Clear error when user types
    }

    fun onPasswordChange(value: String) {
        _password.value = value
        _errorMessage.value = null // Clear error when user types
    }

    fun onRememberMeChange(value: Boolean) {
        _rememberMe.value = value
    }

    fun login() {
        // Validate inputs
        if (_email.value.isBlank() || _password.value.isBlank()) {
            _errorMessage.value = "Email and password cannot be empty"
            return
        }

        // Basic email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(_email.value).matches()) {
            _errorMessage.value = "Please enter a valid email"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(
                    _email.value.trim(),
                    _password.value
                ).await()

                // Login successful
                _isLoading.value = false
                _loginSuccess.value = true

            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = when {
                    e.message?.contains("network") == true ->
                        "Network error. Please check your connection"
                    e.message?.contains("password") == true ->
                        "Invalid email or password"
                    e.message?.contains("user") == true ->
                        "No account found with this email"
                    else -> e.message ?: "Login failed. Please try again"
                }
            }
        }
    }

    fun resetLoginSuccess() {
        _loginSuccess.value = false
    }
}