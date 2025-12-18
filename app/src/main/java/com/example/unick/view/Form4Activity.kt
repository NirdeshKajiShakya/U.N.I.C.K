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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme


class Form4Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                AddressSiblingDetailsScreen()
            }
        }
    }
}

@Composable
fun AddressSiblingDetailsScreen(){
    Scaffold {
            padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item { FormHeaderIcon() }
            item { FormTitle() }
            item { AddressSiblingFormStepIndicator() }
            item { AddressSiblingDetailsHeading() }
            item { DividerLine() }

            // Address Section
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    PresentAddressField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    PermanentAddressField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    LanguageSpokenField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    SchoolBudgetField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            // Sibling Section
            item { SiblingInformationHeading() }
            item { AddSiblingButton() }

            // Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PreviousButtonFinal()
                    SubmitButton()
                }
            }
        }
    }
}

@Composable
fun AddressSiblingFormStepIndicator(){
    Text(
        text = "Step 4 of 4",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun AddressSiblingDetailsHeading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🏠 Address & Sibling Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun PresentAddressField(modifier: Modifier = Modifier){
    var presentAddress by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Present Address*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = presentAddress,
            onValueChange = { presentAddress = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun PermanentAddressField(modifier: Modifier = Modifier){
    var permanentAddress by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Permanent Address*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = permanentAddress,
            onValueChange = { permanentAddress = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun LanguageSpokenField(modifier: Modifier = Modifier){
    var languageSpoken by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Language Spoken at Home*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = languageSpoken,
            onValueChange = { languageSpoken = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun SchoolBudgetField(modifier: Modifier = Modifier){
    var schoolBudget by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Yearly School Budget (INR)*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = schoolBudget,
            onValueChange = { schoolBudget = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun SiblingInformationHeading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "👨‍👩‍👧‍👦 Sibling Information (if any)",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun AddSiblingButton(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Sibling",
            tint = Color(0xFF5B5BFF),
            modifier = Modifier
                .clickable { /* Handle add sibling */ }
                .padding(end = 8.dp)
        )
        Text(
            text = "Add Sibling",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF5B5BFF),
            modifier = Modifier.clickable { /* Handle add sibling */ }
        )
    }
}

@Composable
fun PreviousButtonFinal(){
    Button(
        onClick = { /* Handle previous */ },
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE8E8E8)
        )
    ) {
        Text(
            text = "Previous",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )
    }
}

@Composable
fun SubmitButton(){
    Button(
        onClick = { /* Handle submit */ },
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF4CAF50)
        )
    ) {
        Text(
            text = "Submit Application",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddressSiblingDetailsScreenPreview() {
    UNICKTheme {
        AddressSiblingDetailsScreen()
    }
}