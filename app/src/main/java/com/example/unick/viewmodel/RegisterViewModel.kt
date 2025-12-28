package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Function to register a new user
    fun registerUser(
        fullName: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Basic validation
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            onError("All fields are required")
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            onError("Please enter a valid email")
            return
        }

        if (password.length < 6) {
            onError("Password must be at least 6 characters")
            return
        }

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val userId = result.user?.uid
                if (userId == null) {
                    onError("Failed to get user ID")
                    return@addOnSuccessListener
                }

                // Save additional user info in Firestore
                val userData = hashMapOf(
                    "uid" to userId,
                    "fullName" to fullName,
                    "email" to email,
                )

                firestore.collection("users")
                    .document(userId)
                    .set(userData)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Failed to save user data") }

            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Registration failed")
            }
    }
}
