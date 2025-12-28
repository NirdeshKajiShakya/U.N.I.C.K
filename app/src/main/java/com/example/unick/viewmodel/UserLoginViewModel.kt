package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserLoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Input States
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _rememberMe = MutableStateFlow(false)
    val rememberMe: StateFlow<Boolean> = _rememberMe.asStateFlow()

    // UI States

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()


    // Update functions
    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onPasswordChange(value: String) {
        _password.value = value
    }

    fun onRememberMeChange(value: Boolean) {
        _rememberMe.value = value
    }


    // Firebase Login
    fun loginUser() {
        val emailValue = _email.value
        val passwordValue = _password.value

        if (emailValue.isBlank() || passwordValue.isBlank()) {
            _errorMessage.value = "Email and Password cannot be empty"
            return
        }

        _isLoading.value = true

        auth.signInWithEmailAndPassword(emailValue, passwordValue)
            .addOnSuccessListener {
                _isLoading.value = false
                _loginSuccess.value = true
            }
            .addOnFailureListener { e ->
                _isLoading.value = false
                _errorMessage.value = e.message ?: "Login failed"
            }
    }


    fun clearError() {
        _errorMessage.value = null
    }

    fun logoutUser() {
        auth.signOut()
        _loginSuccess.value = false
    }
}
