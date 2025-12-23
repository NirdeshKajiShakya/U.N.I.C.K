package com.example.unick.view

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance().getReference("schools")

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
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    user?.let {
                                        val school = School(schoolName, location, email)
                                        database.child(it.uid).setValue(school)
                                            .addOnSuccessListener {
                                                Log.d("UserRegistrationSchool", "School data saved successfully.")
                                            }
                                            .addOnFailureListener { e ->
                                                Log.w("UserRegistrationSchool", "Error saving school data", e)
                                            }
                                    }
                                } else {
                                    Log.w("UserRegistrationSchool", "createUserWithEmail:failure", task.exception)
                                }
                            }
                    } else {
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
fun SchoolPasswordField(value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Password", fontSize = 14.sp, color = Color.Black, modifier = Modifier.padding(bottom = 8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text("Create school account password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
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
            modifier = Modifier.clickable { /* Navigate to School Login */ }
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
