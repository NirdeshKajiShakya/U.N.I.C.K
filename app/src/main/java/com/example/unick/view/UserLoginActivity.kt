package com.example.unick.view

import android.os.Bundle
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.UserLoginViewModel

class UserLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                UserLoginScreen()
            }
        }
    }
}

@Composable
fun UserLoginScreen(
    viewModel: UserLoginViewModel = viewModel()
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

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
                    onEmailChange = viewModel::onEmailChange
                )
            }

            item {
                UserPasswordField(
                    password = password,
                    onPasswordChange = viewModel::onPasswordChange
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
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            item { RegisterLinkForUserLogin() }
        }
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
    onEmailChange: (String) -> Unit
) {
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        label = { Text("Email") },
        singleLine = true
    )
}

@Composable
fun UserPasswordField(
    password: String,
    onPasswordChange: (String) -> Unit
) {
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 12.dp),
        label = { Text("Password") },
        visualTransformation = PasswordVisualTransformation(),
        singleLine = true
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
        Text(
            text = if (isLoading) "Logging in..." else "Login",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun RegisterLinkForUserLogin() {
    Row(
        modifier = Modifier.padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(text = "Don't have an account? ")
        Text(
            text = "Register",
            color = Color(0xFF2563EB),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { }
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
