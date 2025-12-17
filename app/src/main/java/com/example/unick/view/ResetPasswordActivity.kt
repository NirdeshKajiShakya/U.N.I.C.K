package com.example.unick.view

import android.graphics.Outline
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.view.ui.theme.UNICKTheme

class ResetPasswordActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                ResetPassword()
            }
        }
    }
}

@Composable
fun ResetPassword() {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { HeadingTextForResetPassword() }
            item { SubTextForResetPassword() }
            item { Spacer(modifier = Modifier.padding(top = 32.dp))}
            item { NewPasswordInputField() }
            item { Spacer(modifier = Modifier.padding(top = 32.dp))}
            item { ButtonForResetPasswordConfirm() }
        }
    }
}

@Composable
fun HeadingTextForResetPassword() {
    Spacer(
        modifier = Modifier
            .padding(top = 60.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "Reset Password",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Composable
fun SubTextForResetPassword() {
    Spacer(
        modifier = Modifier
            .padding(top = 20.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            "Please enter your new password below.",
            fontSize = 16.sp
        )
    }
}

@Composable
fun NewPasswordInputField(){
    var newPassword by remember { mutableStateOf("") }
    var confirmedPassword by remember { mutableStateOf("") }
    Column(

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            OutlinedTextField(
                value = newPassword,
                onValueChange = {newPassword = it},
                modifier = Modifier.fillMaxWidth(0.9f),
                label = { Text("New Password") }
            )
        }
        Spacer(modifier = Modifier.padding(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
        ) {
            OutlinedTextField(
                value = confirmedPassword,
                onValueChange = {confirmedPassword = it},
                modifier = Modifier.fillMaxWidth(0.9f),
                label = { Text("Confirm Password") }
            )
        }
    }
}

@Composable
fun ButtonForResetPasswordConfirm(){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.example.unick.ui.theme.Blue)
        ) {
            Text("Confirm New Password")
        }
    }
}

@Preview
@Composable
fun ResetPasswordPreview() {
    UNICKTheme {
        ResetPassword()
    }
}