package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.unick.viewmodel.AdminLoginViewModel


class AdminLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                AdminLoginScreen(
                    onLoginSuccess = {
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                    },
                    onBackToUserLogin = {
                        startActivity(Intent(this, UserLoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AdminLoginScreen(
    viewModel: AdminLoginViewModel = viewModel(),
    onLoginSuccess: () -> Unit = {},
    onBackToUserLogin: () -> Unit = {}
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val rememberMe by viewModel.rememberMe.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val loginSuccess by viewModel.loginSuccess.collectAsState()

    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginSuccess()
        }
    }

    Scaffold {
        padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            item { HeadingTextForAdminLogin() }
            item { SubHeadingTextForAdminLogin() }
            item { AdminEmailInputField(email, viewModel::onEmailChange) }
            item { AdminPasswordField(password, viewModel::onPasswordChange) }
            item { CheckBoxForAdminLogin(rememberMe, viewModel::onRememberMeChange) }
            item { AdminLoginButton(isLoading, viewModel::login) }
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
            item { RequestAdminAccessLinkForAdminLogin() }
            item { BackToUserLoginLinkForAdminLogin(onBackToUserLogin) }
        }
    }
}

@Composable
fun HeadingTextForAdminLogin(){
    Text(
        text = "Admin Portal",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SubHeadingTextForAdminLogin(){
    Text(
        text = "Sign in to access the admin dashboard",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
fun AdminEmailInputField(email: String, onEmailChange: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            modifier = Modifier
                .fillMaxSize(),
            label = { Text("Admin Email") }
        )
    }
}

@Composable
fun AdminPasswordField(password: String, onPasswordChange: (String) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 13.dp)
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier
                .fillMaxSize(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation()
        )
    }
}

@Composable
fun CheckBoxForAdminLogin(rememberMe: Boolean, onCheckedChange: (Boolean) -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = rememberMe,
            onCheckedChange = onCheckedChange
        )
        Text(
            text = "Remember Me",
            fontSize = 14.sp,
            color = Color(0xFF333333),
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun AdminLoginButton(isLoading: Boolean, onClick: () -> Unit){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onClick,
            enabled = !isLoading,
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E40AF)
            )
        ) {
            Text(
                text = "Sign In as Admin",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RequestAdminAccessLinkForAdminLogin(){
    Row(
        modifier = Modifier
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Need admin access? ",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
        Text(
            text = "Request Admin Account",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { /* Handle request access */ }
        )
    }
}

@Composable
fun BackToUserLoginLinkForAdminLogin(onClick: () -> Unit){
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Back to User Login",
            fontSize = 14.sp,
            color = Color(0xFF2563EB)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AdminLoginScreenPreview() {
    UNICKTheme {
        AdminLoginScreen()
    }
}
