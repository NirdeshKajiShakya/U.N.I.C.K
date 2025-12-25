package com.example.unick.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.unick.repository.ForgotPasswordRepo

class ForgotPasswordViewModel : ViewModel() {

    private val repo = ForgotPasswordRepo()

    val isLoading = mutableStateOf(false)
    val message = mutableStateOf<String?>(null)

    fun sendResetLink(email: String) {
        if (email.isBlank()) {
            message.value = "Please enter your email"
            return
        }

        isLoading.value = true

        repo.sendPasswordResetEmail(
            email = email.trim(),
            onSuccess = {
                isLoading.value = false
                message.value = "Reset link sent to your email"
            },
            onError = { error ->
                isLoading.value = false
                message.value = error
            }
        )
    }

    fun clearMessage() {
        message.value = null
    }
}
