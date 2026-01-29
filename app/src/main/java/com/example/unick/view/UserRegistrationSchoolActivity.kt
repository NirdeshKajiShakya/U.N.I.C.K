package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.google.firebase.database.FirebaseDatabase

class UserRegistrationSchoolActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                UserRegistrationSchoolScreen()
            }
        }
    }
}

data class School(
    val name: String = "",
    val location: String = "",
    val email: String = ""
)

@Composable
fun UserRegistrationSchoolScreen() {
    var schoolName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isInPreview = LocalInspectionMode.current

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            item { SchoolHeadingText() }
            item {
                SchoolInputField(label = "School Name", value = schoolName, placeholder = "Enter school name") {
                    schoolName = it
                }
            }
            item {
                SchoolInputField(label = "School Location", value = location, placeholder = "City, State or Address") {
                    location = it
                }
            }
            item {
                SchoolInputField(label = "School Email Address", value = email, placeholder = "admin@school.com") {
                    email = it
                }
            }
            item {
                SchoolPasswordField(value = password) {
                    password = it
                }
            }
            item {
                SchoolSignUpButton {
                    if (email.isNotBlank() && password.isNotBlank() && schoolName.isNotBlank() && location.isNotBlank()) {
                        if (!isInPreview) {
                            val auth = FirebaseAuth.getInstance()
                            val database = FirebaseDatabase.getInstance().getReference("schools")
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        val user = auth.currentUser
                                        user?.let {
                                            val school = School(schoolName, location, email)
                                            database.child(it.uid).setValue(school)
                                                .addOnSuccessListener {
                                                    Log.d("UserRegistrationSchool", "School data saved successfully.")
                                                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                                    val intent = Intent(context, DashboardActivity::class.java)
                                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                                    context.startActivity(intent)
                                                    (context as? ComponentActivity)?.finish()
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w("UserRegistrationSchool", "Error saving school data", e)
                                                    Toast.makeText(context, "Error saving school data.", Toast.LENGTH_SHORT).show()
                                                }
                                        }
                                    } else {
                                        Log.w("UserRegistrationSchool", "createUserWithEmail:failure", task.exception)
                                        Toast.makeText(context, "Registration failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    } else {
                        Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                        Log.w("UserRegistrationSchool", "All fields are required")
                    }
                }
            }
            item { AlreadyHaveSchoolAccountLink() }
        }
    }
}

@Composable
fun SchoolHeadingText() {
    Text(
        text = "Register Your School",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, top = 40.dp, bottom = 32.dp)
    )
}

@Composable
fun SchoolPasswordField(value: String, label: String = "Password", placeholder: String = "Enter password", onValueChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = label, fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text(placeholder, color = Color.Gray) },
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

// Reusable Input Field for consistency
@Composable
fun SchoolInputField(label: String, value: String, placeholder: String, onValueChange: (String) -> Unit) {
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
fun SchoolSignUpButton(onClick: () -> Unit) {
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 32.dp), contentAlignment = Alignment.Center) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B36F7))
        ) {
            Text(text = "Register School", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun AlreadyHaveSchoolAccountLink() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Already registered?", fontSize = 14.sp, color = Color.Black)
        Text(
            text = " Sign In",
            fontSize = 14.sp,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { context.startActivity(Intent(context, UserLoginSchoolActivity::class.java)) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserRegistrationSchoolPreview() {
    UNICKTheme {
        UserRegistrationSchoolScreen()
    }
}
