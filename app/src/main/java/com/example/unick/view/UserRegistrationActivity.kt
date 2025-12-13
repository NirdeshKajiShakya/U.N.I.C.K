package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                RegisterScreen()
            }
        }
    }
}

@Composable
fun RegisterScreen() {
    Scaffold() { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            item { HeadingTextForRegister() }
            item { RoleSelectionForRegister() }
            item { FullNameLabelAndField() }
            item { EmailLabelAndField() }
            item { PasswordLabelAndField() }
            item { SignUpButtonForRegister() }
            item { AlreadyHaveAccountLinkForRegister() }
        }
    }
}

@Composable
fun HeadingTextForRegister(){
    Text(
        text = "Create an Account",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier
            .padding(start = 24.dp, top = 24.dp, bottom = 24.dp)
    )
}

@Composable
fun RoleSelectionForRegister(){
    var selectedRole by remember { mutableStateOf("Parent") }

    Text(
        text = "I am a:",
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color.Black,
        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp)
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(start = 24.dp, bottom = 24.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        RadioButton(
            selected = selectedRole == "Parent",
            onClick = { selectedRole = "Parent" }
        )
        Text(
            text = "Parent",
            modifier = Modifier.padding(end = 16.dp)
        )

        RadioButton(
            selected = selectedRole == "Student",
            onClick = { selectedRole = "Student" }
        )
        Text(
            text = "Student",
            modifier = Modifier.padding(end = 16.dp)
        )

        RadioButton(
            selected = selectedRole == "School",
            onClick = { selectedRole = "School" }
        )
        Text(text = "School")
    }
}

@Composable
fun FullNameLabelAndField(){
    var fullName by remember { mutableStateOf("") }
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
            onValueChange = { fullName = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = { Text("Enter your full name", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun EmailLabelAndField(){
    var email by remember { mutableStateOf("") }
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
            onValueChange = { email = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = { Text("Enter your email", color = Color.Gray) },
            singleLine = true
        )
    }
}

@Composable
fun PasswordLabelAndField(){
    var password by remember { mutableStateOf("") }
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
            onValueChange = { password = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            placeholder = { Text("Enter password", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
    }
}

@Composable
fun SignUpButtonForRegister(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { /* Handle sign up */ },
            modifier = Modifier
                .width(300.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B36F7)
            )
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
fun AlreadyHaveAccountLinkForRegister(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 24.dp),
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
            modifier = Modifier.clickable { /* Navigate to login */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    UNICKTheme {
        RegisterScreen()
    }
}
