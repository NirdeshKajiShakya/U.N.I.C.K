package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme
import com.google.firebase.auth.FirebaseAuth

class UserLoginSchoolActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                UserLoginSchoolScreen()
            }
        }
    }
}

@Composable
fun UserLoginSchoolScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    Scaffold {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(it),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            item { SchoolLoginHeadingText() }
            item {
                SchoolLoginInputField(label = "School Email Address", value = email, placeholder = "admin@school.com") {
                    email = it
                }
            }
            item {
                SchoolLoginPasswordField(value = password) {
                    password = it
                }
            }
            item {
                SchoolLoginButton {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        if (!isInPreview) {
                            val auth = FirebaseAuth.getInstance()
                            val db = com.google.firebase.database.FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
                            
                            auth.signInWithEmailAndPassword(email.trim(), password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val firebaseUser = auth.currentUser
                                        if (firebaseUser != null) {
                                            val uid = firebaseUser.uid
                                            val schoolRef = db.getReference("schools").child(uid)

                                            // CHECK 1: Verify user exists in "schools" node
                                            schoolRef.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                                                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                                                    if (snapshot.exists()) {
                                                        Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                                                        val intent = Intent(context, DashboardActivity::class.java)
                                                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                        context.startActivity(intent)
                                                        (context as? ComponentActivity)?.finish()
                                                    } else {
                                                        // WRONG ROLE - Check Users or Admin
                                                        db.getReference("Users").child(uid).addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                                                            override fun onDataChange(userSnap: com.google.firebase.database.DataSnapshot) {
                                                                if (userSnap.exists()) {
                                                                    Toast.makeText(context, "This email is registered as a Student. Please use the Student login portal.", Toast.LENGTH_LONG).show()
                                                                } else {
                                                                    db.getReference("Admins").child(uid).addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                                                                        override fun onDataChange(adminSnap: com.google.firebase.database.DataSnapshot) {
                                                                            if (adminSnap.exists()) {
                                                                                Toast.makeText(context, "This email is registered as an Admin. Please use the Admin login portal.", Toast.LENGTH_LONG).show()
                                                                            } else {
                                                                                Toast.makeText(context, "Account not found.", Toast.LENGTH_SHORT).show()
                                                                            }
                                                                            auth.signOut()
                                                                        }
                                                                        override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                                                                            Toast.makeText(context, "Verification failed.", Toast.LENGTH_SHORT).show()
                                                                            auth.signOut()
                                                                        }
                                                                    })
                                                                }
                                                                if (userSnap.exists()) auth.signOut() // Sign out if student found
                                                            }
                                                            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                                                                Toast.makeText(context, "Verification failed.", Toast.LENGTH_SHORT).show()
                                                                auth.signOut()
                                                            }
                                                        })
                                                    }
                                                }

                                                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                                                    Toast.makeText(context, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                                                    auth.signOut()
                                                }
                                            })
                                        }
                                    } else {
                                        val exception = task.exception
                                        val msg = when {
                                            exception?.message?.contains("user-not-found", ignoreCase = true) == true ->
                                                "Account not found. This email is not registered."
                                            exception?.message?.contains("wrong-password", ignoreCase = true) == true ->
                                                "Incorrect password. Please try again."
                                            exception?.message?.contains("invalid-credential", ignoreCase = true) == true ->
                                                "Invalid credentials."
                                            else -> "Login failed: ${exception?.message}"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            // In preview mode
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            item { NoSchoolAccountLink() }
        }
    }
}

@Composable
fun SchoolLoginHeadingText() {
    Text(
        text = "Login to Your School Account",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 32.dp)
    )
}

@Composable
fun SchoolLoginPasswordField(value: String, onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Password", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text("Enter your password", color = Color.Gray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            singleLine = true
        )
    }
}

@Composable
fun SchoolLoginInputField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text(placeholder, color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun SchoolLoginButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B36F7))
        ) {
            Text(text = "Login", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun NoSchoolAccountLink() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Don't have an account?", fontSize = 14.sp, color = Color.Black)
        Text(
            text = " Sign Up",
            fontSize = 14.sp,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { context.startActivity(Intent(context, UserRegistrationSchoolActivity::class.java)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserLoginSchoolPreview() {
    UNICKTheme {
        UserLoginSchoolScreen()
    }
}
