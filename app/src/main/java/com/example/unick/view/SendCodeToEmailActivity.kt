package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle.Companion.Italic
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.R
import com.example.unick.ui.theme.Blue
import com.example.unick.ui.theme.LightGray
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.viewmodel.ForgotPasswordViewModel


class SendCodeToEmailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CodeToEmail(
                onNavigateToCodeConfirm = {
                    startActivity(Intent(this, CodeConfirmActivity::class.java))
                },
                onNavigateToLogin = {
                    startActivity(Intent(this, UserLoginActivity::class.java))
                    finish()
                }
            )
        }
    }
}


@Composable
fun CodeToEmail(
    viewModel: ForgotPasswordViewModel = viewModel(),
    onNavigateToCodeConfirm: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val emailState = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.message.value) {
        viewModel.message.value?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
            // Navigate to code confirm after successful email send
            if (it.contains("sent", ignoreCase = true)) {
                kotlinx.coroutines.delay(1000)
                onNavigateToCodeConfirm()
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { Logo() }
            item { HeadingTextForSendCodeToEmail() }
            item { SubTextForSendCodeToEmail() }
            item { EmailInputField(emailState) }
            item { ButtonForOTP(emailState.value, viewModel) }
            item { BackToLoginText(onClick = onNavigateToLogin) }
        }
    }
}


@Composable
fun Logo() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .border(2.dp, Color.Black, CircleShape)
        ){
            Image(
                painter = painterResource(id = R.drawable.unick_logo),
                contentDescription = "App Logo",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun HeadingTextForSendCodeToEmail() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "Forget Your Password?",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold

        )
    }
}

@Composable
fun SubTextForSendCodeToEmail(){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "No worries! Enter your email and we’ll send you a reset link.",
            fontSize = 16.sp,
            color = LightGray,
            textAlign = TextAlign.Center,
            fontFamily = FontFamily.Serif
        )
    }
}

@Composable
fun EmailInputField(emailState: MutableState<String>) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = emailState.value,
            onValueChange = { emailState.value = it },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxSize(0.9f)
        )
    }
}


@Composable
fun ButtonForOTP(
    email: String,
    viewModel: ForgotPasswordViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { viewModel.sendResetLink(email) },
            colors = ButtonDefaults.buttonColors(containerColor = Blue),
            enabled = !viewModel.isLoading.value
        ) {
            if (viewModel.isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
            } else {
                Text(
                    "Send Reset Link",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}


@Composable
fun BackToLoginText(onClick: () -> Unit = {}){
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text("Remembered your password? ", fontSize = 16.sp)
        Text(
            "Back to Login",
            fontSize = 16.sp,
            fontStyle = Italic,
            color = Blue,
            modifier = Modifier
                .clickable { onClick() }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun CodeToEmailPreview() {
    CodeToEmail()
}