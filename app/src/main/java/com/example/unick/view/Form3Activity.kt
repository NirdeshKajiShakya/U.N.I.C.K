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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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


class Form3Activity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                Form3Screen()
            }
        }
    }
}

@Composable
fun Form3Screen(){
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
            item { Form3StepIndicator() }
            item { Form3ParentsHeading() }
            item { DividerLine() }

            // Father's Details Section
            item { FatherDetailsSubheading() }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Form3FatherNameField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3FatherAgeField(
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
                    Form3FatherQualificationField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3FatherProfessionField(
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
                    Form3FatherAnnualIncomeField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3FatherPhoneNoField(
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
                    Form3FatherAadharNoField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3FatherEmailField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            // Mother's Details Section
            item { MotherDetailsSubheading() }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Form3MotherNameField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3MotherAgeField(
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
                    Form3MotherQualificationField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3MotherProfessionField(
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
                    Form3MotherAnnualIncomeField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3MotherPhoneNoField(
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
                    Form3MotherAadharNoField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    Form3MotherEmailField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }

            // Relationship Status
            item { Form3ParentsRelationshipStatusField() }

            // Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Form3PreviousButton()
                    Form3NextButton()
                }
            }
        }
    }
}

@Composable
fun Form3StepIndicator(){
    Text(
        text = "Step 3 of 4",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun Form3ParentsHeading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "👥 Parents' & Guardian's Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun FatherDetailsSubheading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Father's Details",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun MotherDetailsSubheading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = "Mother's Details",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

// Father's Fields
@Composable
fun Form3FatherNameField(modifier: Modifier = Modifier){
    var fatherName by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Name*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherName,
            onValueChange = { fatherName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherAgeField(modifier: Modifier = Modifier){
    var fatherAge by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Age*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherAge,
            onValueChange = { fatherAge = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherQualificationField(modifier: Modifier = Modifier){
    var fatherQualification by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Qualification*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherQualification,
            onValueChange = { fatherQualification = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherProfessionField(modifier: Modifier = Modifier){
    var fatherProfession by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Profession*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherProfession,
            onValueChange = { fatherProfession = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherAnnualIncomeField(modifier: Modifier = Modifier){
    var fatherIncome by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Annual Income*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherIncome,
            onValueChange = { fatherIncome = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherPhoneNoField(modifier: Modifier = Modifier){
    var fatherPhone by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Phone No*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherPhone,
            onValueChange = { fatherPhone = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherAadharNoField(modifier: Modifier = Modifier){
    var fatherAadhar by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Aadhar No*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherAadhar,
            onValueChange = { fatherAadhar = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3FatherEmailField(modifier: Modifier = Modifier){
    var fatherEmail by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Father's Email*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = fatherEmail,
            onValueChange = { fatherEmail = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

// Mother's Fields
@Composable
fun Form3MotherNameField(modifier: Modifier = Modifier){
    var motherName by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Name*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
             value = motherName,
            onValueChange = { motherName = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherAgeField(modifier: Modifier = Modifier){
    var motherAge by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Age*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherAge,
            onValueChange = { motherAge = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherQualificationField(modifier: Modifier = Modifier){
    var motherQualification by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Qualification*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherQualification,
            onValueChange = { motherQualification = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherProfessionField(modifier: Modifier = Modifier){
    var motherProfession by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Profession*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherProfession,
            onValueChange = { motherProfession = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherAnnualIncomeField(modifier: Modifier = Modifier){
    var motherIncome by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Annual Income*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherIncome,
            onValueChange = { motherIncome = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherPhoneNoField(modifier: Modifier = Modifier){
    var motherPhone by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Phone No*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherPhone,
            onValueChange = { motherPhone = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherAadharNoField(modifier: Modifier = Modifier){
    var motherAadhar by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Aadhar No*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherAadhar,
            onValueChange = { motherAadhar = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun Form3MotherEmailField(modifier: Modifier = Modifier){
    var motherEmail by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Mother's Email*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = motherEmail,
            onValueChange = { motherEmail = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Form3ParentsRelationshipStatusField(){
    var relationshipStatus by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val relationshipOptions = listOf("Married", "Divorced", "Separated", "Widow/Widower", "Single", "Other")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Parents' Relationship Status*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
            ) {
                OutlinedTextField(
                    value = relationshipStatus,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    relationshipOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, fontSize = 14.sp) },
                            onClick = {
                                relationshipStatus = option
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Form3PreviousButton(){
    Button(
        onClick = { /* Handle previous */ },
        modifier = Modifier
            .width(100.dp)
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
fun Form3NextButton(){
    Button(
        onClick = { /* Handle next */ },
        modifier = Modifier
            .width(120.dp)
            .height(50.dp),
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
fun Form3ScreenPreview() {
    UNICKTheme {
        Form3Screen()
    }
}