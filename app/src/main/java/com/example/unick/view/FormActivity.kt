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


class StudentApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                StudentApplicationScreen()
            }
        }
    }
}

@Composable
fun StudentApplicationScreen(){
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
            item { FormStepIndicator() }
            item { PersonalDetailsHeading() }
            item { DividerLine() }
            item { StudentFullNameField() }
            item { LocationField() }
            item { DateOfBirthField() }
            item { AgeField() }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    GenderField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    NationalityField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            item { PlaceOfBirthField() }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    ReligionField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    CasteField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            item{
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    BloodGroupField(
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .padding(end = 8.dp)
                    )
                    AllergiesField(
                        modifier = Modifier
                            .fillMaxWidth()
                    )
                }
            }
            item { InterestsHobbiesField() }
            item { NextButton() }
        }
    }
}

@Composable
fun FormHeaderIcon(){
    Text(
        text = "📋",
        fontSize = 40.sp,
        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
    )
}

@Composable
fun FormTitle(){
    Text(
        text = "Student Application Form",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun FormStepIndicator(){
    Text(
        text = "Step 1 of 4",
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun PersonalDetailsHeading(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "👤 Student's Personal Details",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
    }
}

@Composable
fun DividerLine(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(1.dp)
            .background(Color(0xFFDDDDDD))
    ) {}
}

@Composable
fun StudentFullNameField(){
    var fullName by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Full Name*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun LocationField(){
    var location by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Location", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun DateOfBirthField(){
    var dob by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Date Of Birth", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = dob,
                onValueChange = { dob = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                placeholder = { Text("mm/dd/yyyy", fontSize = 12.sp) },
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun AgeField(){
    var age by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Age", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenderField(modifier: Modifier = Modifier){
    var gender by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val genderOptions = listOf("Female", "Male", "Other")
    Column(modifier = modifier) {
        Text("Gender", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            OutlinedTextField(
                value = gender,
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
                genderOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            gender = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NationalityField(modifier: Modifier = Modifier){
    var nationality by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val nationalityOptions = listOf("Nepali", "Other")
    Column(modifier = modifier) {
        Text("Nationality", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            OutlinedTextField(
                value = nationality,
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
                nationalityOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            nationality = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceOfBirthField(){
    var placeOfBirth by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Place Of Birth", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = placeOfBirth,
                onValueChange = { placeOfBirth = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReligionField(modifier: Modifier = Modifier){
    var religion by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val religionOptions = listOf("Hinduism", "Islam", "Christianity", "Sikhism", "Buddhism", "Jainism", "Other")
    Column(modifier = modifier) {
        Text("Religion", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            OutlinedTextField(
                value = religion,
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
                religionOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            religion = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CasteField(modifier: Modifier = Modifier){
    var caste by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Caste", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = caste,
            onValueChange = { caste = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodGroupField(modifier: Modifier = Modifier){
    var bloodGroup by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val bloodGroupOptions = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    Column(modifier = modifier) {
        Text("Blood Group*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp)
        ) {
            OutlinedTextField(
                value = bloodGroup,
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
                bloodGroupOptions.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 14.sp) },
                        onClick = {
                            bloodGroup = option
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AllergiesField(modifier: Modifier = Modifier){
    var allergies by remember { mutableStateOf("") }
    Column(modifier = modifier) {
        Text("Any Allergies", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = allergies,
            onValueChange = { allergies = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 6.dp),
            textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
        )
    }
}

@Composable
fun InterestsHobbiesField(){
    var interests by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Interests/Hobbies*", fontSize = 12.sp, color = Color(0xFF333333), fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = interests,
                onValueChange = { interests = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp)
            )
        }
    }
}

@Composable
fun NextButton(){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.End
    ) {
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
}

@Preview(showBackground = true)
@Composable
fun StudentApplicationScreenPreview() {
    UNICKTheme {
        StudentApplicationScreen()
    }
}