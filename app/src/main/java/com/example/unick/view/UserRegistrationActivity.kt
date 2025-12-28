package com.example.unick.view

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
                RegisterScreen(registerViewModel)
            }
        }
    }
}

@Composable
fun RegisterScreen(registerViewModel: RegisterViewModel) {
    var fullName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    RegisterScreenContent(
        fullName = fullName,
        location = location,
        email = email,
        password = password,
        onFullNameChange = { fullName = it },
        onLocationChange = { location = it },
        onEmailChange = { email = it },
        onPasswordChange = { password = it },
        onSignUpClick = {
            registerViewModel.registerUser(
                fullName = fullName,
                email = email,
                password = password,
                location = location,
                onSuccess = {
                    Toast.makeText(
                        context,
                        "Registration Successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    // TODO: Navigate to login screen
                },
                onError = { errorMsg ->
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                }
            )
        },
        onSignInClick = {
            // TODO: Navigate to LoginActivity
        }
    )
}

@Composable
fun RegisterScreenContent(
    fullName: String,
    location: String,
    email: String,
    password: String,
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
                SignUpButtonForRegister(onClick = onSignUpClick)
            }
            item {
                AlreadyHaveAccountLinkForRegister(onSignInClick = onSignInClick)
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
    Text(
        text = "Full Name",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = fullName,
            onValueChange = onFullNameChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            placeholder = { Text("Enter your full name", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun LocationLabelAndField(location: String, onLocationChange: (String) -> Unit) {
    Text(
        text = "Location",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            placeholder = { Text("Enter your location", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun EmailLabelAndField(email: String, onEmailChange: (String) -> Unit) {
    Text(
        text = "Email address",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 16.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            placeholder = { Text("Enter your email", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun PasswordLabelAndField(password: String, onPasswordChange: (String) -> Unit) {
    Text(
        text = "Password",
        fontSize = 14.sp,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            placeholder = { Text("Enter password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
    }
}

@Composable
fun SignUpButtonForRegister(onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            modifier = Modifier.width(300.dp).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0B36F7))
        ) {
            Text(
                text = "Sign Up",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun AlreadyHaveAccountLinkForRegister(onSignInClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account?",
            fontSize = 14.sp,
            color = Color.Black
        )
        Text(
            text = " Sign In",
            fontSize = 14.sp,
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onSignInClick() }
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