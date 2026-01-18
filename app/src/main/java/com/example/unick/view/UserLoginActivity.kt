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
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.UserLoginViewModel

class UserLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                UserLoginScreen(
                    onLoginSuccess = {
                        // Navigate to Dashboard
                        startActivity(Intent(this, DashboardActivity::class.java))
                        finish() // Close login activity so user can't go back
                    },
                    onNavigateToRegister = {
                        // Navigate to registration
                        startActivity(Intent(this, UserRegistrationActivity::class.java))
                    },
                    onNavigateToForgotPassword = {
                        // Navigate to forgot password
                        startActivity(Intent(this, SendCodeToEmailActivity::class.java))
                    },
                    onNavigateToAdminLogin = {
                        startActivity(Intent(this, AdminLoginActivity::class.java))
                    }
                )
            }
        }
    }
}

@Composable
fun UserLoginScreen(
    viewModel: UserLoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onNavigateToAdminLogin: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Handle successful login
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            showSuccessDialog = true
            viewModel.resetLoginSuccess()
        }
    }

    // Success Dialog
    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ ",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                    Text(
                        text = "Successfully Login",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            },
            text = null,
            confirmButton = { },
            containerColor = Color.White,
            shape = MaterialTheme.shapes.medium
        )

        // Navigate after showing dialog for 1.5 seconds
        LaunchedEffect(Unit) {
            kotlinx.coroutines.delay(1500)
            showSuccessDialog = false
            onLoginSuccess()
        }
    }

// Handle loginSuccess state
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            showSuccessDialog = true
            viewModel.resetLoginSuccess()
        }
    }


    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            item { HeadingTextForUserLogin() }
            item { SubHeadingTextForUserLogin() }

            item {
                UserEmailInputField(
                    email = email,
                    onEmailChange = viewModel::onEmailChange,
                    isError = errorMessage != null
                )
            }

            item {
                UserPasswordField(
                    password = password,
                    onPasswordChange = viewModel::onPasswordChange,
                    isError = errorMessage != null
                )
            }

            item {
                CheckBoxForUserLogin(
                    rememberMe = rememberMe,
                    onCheckedChange = viewModel::onRememberMeChange
                )
            }

            item {
                UserLoginButton(
                    isLoading = isLoading,
                    onClick = { viewModel.login() }
                )
            }

            if (errorMessage != null) {
                item {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 8.dp, start = 25.dp, end = 25.dp)
                    )
                }
            }

            item {
                ForgotPasswordLinkForUserLogin(
                    onClick = onNavigateToForgotPassword
                )
            }

            item {
                RegisterLinkForUserLogin(
                    onClick = onNavigateToRegister
                )
            }

            item {
                AdminLoginLink(onClick = onNavigateToAdminLogin)
            }
        }
    }
}

@Composable
fun AdminLoginLink(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(vertical = 24.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Are you an admin? ",
        )
        Text(
            text = "Login here",
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun HeadingTextForUserLogin() {
    Text(
        text = "Welcome Back",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SubHeadingTextForUserLogin() {
    Text(
        text = "Please enter your details to login",
        fontSize = 14.sp,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
fun UserEmailInputField(
    email: String,
    onEmailChange: (String) -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        label = { Text("Email") },
        singleLine = true,
        isError = isError
    )
}

@Composable
fun UserPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    isError: Boolean = false
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 12.dp),
        label = { Text("Password") },
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        singleLine = true,
        isError = isError
    )
}

@Composable
fun CheckBoxForUserLogin(
    rememberMe: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = rememberMe,
            onCheckedChange = onCheckedChange
        )
        Text(text = "Remember Me")
    }
}

@Composable
fun UserLoginButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .padding(vertical = 20.dp)
            .width(200.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2563EB)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            Text(
                text = "Login",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ForgotPasswordLinkForUserLogin(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 25.dp),
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = "Forgot Password?",
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onClick() }
        )
    }
}

@Composable
fun RegisterLinkForUserLogin(onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .padding(bottom = 24.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Don't have an account? ")
        Text(
            text = "Register",
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLogin() {
    UNICKTheme {
        UserLoginScreen()
    }
}