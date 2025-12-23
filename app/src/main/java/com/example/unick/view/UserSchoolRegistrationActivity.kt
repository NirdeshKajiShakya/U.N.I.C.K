package com.example.unick.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserSchoolRegistrationActivity : ComponentActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                SchoolRegistrationScreen(auth)
            }
        }
    }
}

data class SchoolRegistrationData(
    val name: String,
    val email: String,
    val location: String
)

@Composable
fun SchoolRegistrationScreen(auth: FirebaseAuth) {
    val context = LocalContext.current
    var schoolName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var schoolLocation by remember { mutableStateOf("") }

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item { Spacer(modifier = Modifier.height(40.dp)) }
            item { SchoolRegistrationHeading() }
            item { SchoolRegistrationSubtitle() }
            item { Spacer(modifier = Modifier.height(32.dp)) }
            item {
                SchoolNameField(schoolName) {
                    schoolName = it
                }
            }
            item {
                SchoolEmailField(email) {
                    email = it
                }
            }
            item {
                SchoolPasswordField(password) {
                    password = it
                }
            }
            item {
                SchoolLocationField(schoolLocation) {
                    schoolLocation = it
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                SchoolRegistrationButton {
                    if (email.isNotBlank() && password.isNotBlank()) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val user = auth.currentUser
                                    val schoolData = SchoolRegistrationData(schoolName, email, schoolLocation)
                                    val database = Firebase.database
                                    val myRef = database.getReference("schools").child(user!!.uid)
                                    myRef.setValue(schoolData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "School registered successfully!", Toast.LENGTH_SHORT).show()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Failed to save school data.", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(context, "Email and password cannot be empty.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { AlreadyHaveAccountLink() }
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
fun SchoolRegistrationHeading() {
    Text(
        text = "School Registration",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0B36F7),
        modifier = Modifier.padding(horizontal = 24.dp)
    )
}

@Composable
fun SchoolRegistrationSubtitle() {
    Text(
        text = "Create a new school account",
        fontSize = 16.sp,
        color = Color.Gray,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun SchoolNameField(schoolName: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "School Name",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = schoolName,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Enter school name", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun SchoolEmailField(email: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "School Email",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("school@example.com", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun SchoolPasswordField(password: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Password",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = password,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Enter password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
    }
}

@Composable
fun SchoolLocationField(location: String, onValueChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
    ) {
        Text(
            text = "School Location",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = location,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = { Text("Enter school location", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun SchoolRegistrationButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B36F7)
            )
        ) {
            Text(
                text = "Register",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun AlreadyHaveAccountLink() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account?",
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = " Login",
            fontSize = 14.sp,
            color = Color(0xFF0B36F7),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { /* Navigate to login */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SchoolRegistrationScreenPreview() {
    UNICKTheme {
        // In the preview, we can't get a real FirebaseAuth instance, so we pass a dummy one.
        // This will not affect the actual app.
        val auth = Firebase.auth
        SchoolRegistrationScreen(auth)
    }
}
