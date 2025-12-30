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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.RegisterViewModel

class UserRegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UNICKTheme {
                val registerViewModel: RegisterViewModel = viewModel()
                RegisterScreen(
                    registerViewModel = registerViewModel,
                    onNavigateToLogin = {
                        startActivity(Intent(this, UserLoginActivity::class.java))
                        finish()
                    },
                    onNavigateToDashboard = {
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun RegisterScreen(
    registerViewModel: RegisterViewModel,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    RegisterScreenContent(
        fullName = fullName,
        location = location,
        email = email,
        password = password,
        isLoading = isLoading,
        onFullNameChange = { fullName = it },
        onLocationChange = { location = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignUpClick = {
            if (fullName.isBlank() || email.isBlank() || password.isBlank() || location.isBlank()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@RegisterScreenContent
            }

            isLoading = true
            registerViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                location = location,
                onSuccess = {
                    isLoading = false
                    Toast.makeText(context, "Registration Successful!", Toast.LENGTH_LONG).show()
                    onNavigateToDashboard()
                },
                onError = { errorMsg ->
                    isLoading = false
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        },
        onSignInClick = {
            onNavigateToLogin()
        }
    )
}

@Composable
fun RegisterScreenContent(
    fullName: String,
    location: String,
    email: String,
    password: String,
    isLoading: Boolean = false,
    onFullNameChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            item { HeadingTextForRegister() }

            item {
                FullNameLabelAndField(
                    fullName = fullName,
                    onFullNameChange = onFullNameChange
                )
            }

            item {
                LocationLabelAndField(
                    location = location,
                    onLocationChange = onLocationChange
                )
            }

            item {
                EmailLabelAndField(
                    email = email,
                    onEmailChange = onEmailChange
                )
            }

            item {
                PasswordLabelAndField(
                    password = password,
                    onPasswordChange = onPasswordChange
                )
            }

            item {
                SignUpButtonForRegister(
                    onClick = onSignUpClick,
                    isLoading = isLoading
                )
            }

            item {
                AlreadyHaveAccountLinkForRegister(
                    onSignInClick = onSignInClick
                )
            }
        }
    }
}

@Composable
fun HeadingTextForRegister() {
    Text(
        text = "Create an Account",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 24.dp)
    )
}

@Composable
fun FullNameLabelAndField(fullName: String, onFullNameChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Full Name",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your full name", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun LocationLabelAndField(location: String, onLocationChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Location",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your location", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun EmailLabelAndField(email: String, onEmailChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        Text(
            text = "Email address",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter your email", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun PasswordLabelAndField(password: String, onPasswordChange: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        Text(
            text = "Password",
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Enter password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
    }
}

@Composable
fun SignUpButtonForRegister(
    onClick: () -> Unit,
    isLoading: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B36F7)
            )
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Sign Up",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun AlreadyHaveAccountLinkForRegister(onSignInClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 24.dp)
            .clickable { onSignInClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account?",
            fontSize = 14.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "Sign In",
            fontSize = 14.sp,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    UNICKTheme {
        var fullName by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        RegisterScreenContent(
            fullName = fullName,
            location = location,
            email = email,
            password = password,
            onFullNameChange = { fullName = it },
            onLocationChange = { location = it },
            onEmailChange = { email = it },
            onPasswordChange = { password = it },
            onSignUpClick = { /* Preview - no action */ },
            onSignInClick = { /* Preview - no action */ }
        )
    }
}