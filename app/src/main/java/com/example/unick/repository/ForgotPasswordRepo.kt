package com.example.unick.repository

import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordRepo {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onError(
                        task.exception?.message
                            ?: "Failed to send reset email"
                    )
                }
            }
    }
}
