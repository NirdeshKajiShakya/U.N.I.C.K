package com.example.unick.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseDatabase = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")

    fun registerUser(
        fullName: String,
        location: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,



        onError: (String) -> Unit
    ) {
        if (fullName.isEmpty() || location.isEmpty() || email.isEmpty() || password.isEmpty()) {
            onError("All fields must be filled.")
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser
                    if (firebaseUser != null) {
                        val user = hashMapOf(
                            "fullName" to fullName,
                            "location" to location,
                            "email" to email
                        )
                        db.getReference("Users").child(firebaseUser.uid).setValue(user)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { e -> onError(e.message ?: "Realtime Database error") }
                    } else {
                        onError("Registration failed: user is null after creation.")
                    }
                } else {
                    onError(task.exception?.message ?: "An unknown authentication error occurred.")
                }
            }
    }
}