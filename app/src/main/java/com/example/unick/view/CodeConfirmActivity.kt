package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.view.ui.theme.UNICKTheme

class CodeConfirmActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                CodeConfirmScreen()
            }
        }
    }
}

@Composable
fun CodeConfirmScreen() {
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { HeadingTextForCodeConfirm() }
            item { SubTextForCodeConfirm() }
            item { Spacer(modifier = Modifier.padding(top = 32.dp))}
            item { OtpCodeInput(length = 6) }
            item { Spacer(modifier = Modifier.padding(top = 32.dp))}
            item { ButtonForOTPConfirm() }
        }
    }
}

@Composable
fun HeadingTextForCodeConfirm() {
    Spacer(
        modifier = Modifier
            .padding(top = 60.dp)
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp)
    ) {
        Text(
            "Verification Code",
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp
        )
    }
}

@Composable
fun SubTextForCodeConfirm() {
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
            "Please enter the 6-digit code sent to your email address.",
            fontSize = 16.sp
        )
    }
}

@Composable
fun OtpCodeInput(length: Int) {
    // Holds the code as a list of single-character strings
    var code by remember { mutableStateOf(List(length) { "" }) }

    // FocusRequesters for each box so we can move focus programmatically
    val focusRequesters = remember {
        List(length) { FocusRequester() }
    }
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 30.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        code.forEachIndexed { index, value ->
            OutlinedTextField(
                value = value,
                onValueChange = { newValue ->
                    // Accept only digits and limit to 1 char per box
                    val filtered = newValue.filter { it.isDigit() }.take(1)

                    val updated = code.toMutableList()
                    updated[index] = filtered
                    code = updated

                    // Move to next box when a digit is entered
                    if (filtered.isNotEmpty() && index < length - 1) {
                        focusRequesters[index + 1].requestFocus()
                    }

                    // If the user clears the field, move focus back
                    if (filtered.isEmpty() && index > 0) {
                        focusManager.moveFocus(FocusDirection.Previous)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .focusRequester(focusRequesters[index]),
                visualTransformation = VisualTransformation.None
            )
        }
    }

    // Automatically focus the first box when this composable appears
    LaunchedEffect(Unit) {
        if (length > 0) {
            focusRequesters[0].requestFocus()
        }
    }
}

@Composable
fun ButtonForOTPConfirm(){
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        androidx.compose.material3.Button(
            onClick = {
                val intent = Intent(context, ResetPasswordActivity::class.java)
                context.startActivity(intent)
            },
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = com.example.unick.ui.theme.Blue)
        ) {
            Text(
                "Confirm Code",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview
@Composable
fun CodeConfirmScreenPreview() {
    UNICKTheme {
        CodeConfirmScreen()
    }
}