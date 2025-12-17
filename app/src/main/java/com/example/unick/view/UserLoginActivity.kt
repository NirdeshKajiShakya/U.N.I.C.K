package com.example.unick.view

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme

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
fun UserLoginScreen(){
    Scaffold() { padding ->
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
            item { UserEmailInputField() }
            item { UserPasswordField() }
            item { CheckBoxForUserLogin() }
            item { UserLoginButton() }
            item { RegisterLinkForUserLogin() }
        }
    }
}

@Composable
fun HeadingTextForUserLogin(){
    Text(
        text = "Welcome Back",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun SubHeadingTextForUserLogin(){
    Text(
        text = "Please enter your details to login",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 24.dp)
    )
}

@Composable
fun UserEmailInputField(){
    var email by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            singleLine = true
        )
    }
}

@Composable
fun UserPasswordField(){
    var password by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 13.dp)
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
    }
}

@Composable
fun CheckBoxForUserLogin(){
    var rememberMe by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Checkbox(
            checked = rememberMe,
            onCheckedChange = { rememberMe = it }
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
fun UserLoginButton(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 20.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { /* Handle login */ },
            modifier = Modifier
                .width(200.dp)
                .height(50.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2563EB)
            )
        ) {
            Text(
                text = "Login",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RegisterLinkForUserLogin(){
    Row(
        modifier = Modifier
            .padding(bottom = 32.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Don't have an account? ",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
        Text(
            text = "Register",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2563EB),
            modifier = Modifier.clickable { /* Navigate to register screen */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UserLoginScreenPreview() {
    UNICKTheme {
        UserLoginScreen()
    }
}
