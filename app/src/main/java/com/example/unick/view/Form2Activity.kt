package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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


class PreviousSchoolDetailsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                PreviousSchoolDetailsScreen()
            }
        }
    }
}

@Composable
fun PreviousSchoolDetailsScreen(){
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
            item { PreviousSchoolFormStepIndicator() }
            item { PreviousSchoolDetailsHeading() }
            item { DividerLine() }
            item { LastSchoolNameField() }
            item { ClassCompletedField() }
            item { LastAcademicYearField() }
            item { ReasonForLeavingField() }
            item { BoardField() }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PreviousButtonStep2()
                    NextButtonStep2()
                }
            }
        }
    }
}

@Composable
fun PreviousSchoolFormStepIndicator(){
    Text(
        text = "Step 2 of 4",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun PreviousSchoolDetailsHeading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "📚 Previous School Details (if any)",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun LastSchoolNameField(){
    var lastSchoolName by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Last School Name", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = lastSchoolName,
                onValueChange = { lastSchoolName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun ClassCompletedField(){
    var classCompleted by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Class Completed", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = classCompleted,
                onValueChange = { classCompleted = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun LastAcademicYearField(){
    var lastAcademicYear by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Last Academic Year", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = lastAcademicYear,
                onValueChange = { lastAcademicYear = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                placeholder = { Text("e.g., 2024", fontSize = 12.sp) },
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun ReasonForLeavingField(){
    var reasonForLeaving by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Reason For Leaving", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = reasonForLeaving,
                onValueChange = { reasonForLeaving = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun BoardField(){
    var board by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Board", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = board,
                onValueChange = { board = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                placeholder = { Text("e.g., CBSE, NEB", fontSize = 12.sp) },
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun PreviousButtonStep2(){
    Button(
        onClick = { /* Handle previous */ },
        modifier = Modifier
            .width(110.dp)
            .height(40.dp),
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
fun NextButtonStep2(){
    Button(
        onClick = { /* Handle next */ },
        modifier = Modifier
            .width(100.dp)
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF5B5BFF)
        )
    ) {
        Text(
            text = "Next",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviousSchoolDetailsScreenPreview() {
    UNICKTheme {
        PreviousSchoolDetailsScreen()
    }
}