package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme
import kotlinx.coroutines.delay

class StudentApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                StudentRegistrationForm()
            }
        }
    }
}

data class FormData(
    val fullName: String = "",
    val location: String = "",
    val dob: String = "",
    val age: String = "",
    val gender: String = "",
    val nationality: String = "",
    val placeOfBirth: String = "",
    val religion: String = "",
    val caste: String = "",
    val bloodGroup: String = "",
    val allergies: String = "",
    val interests: String = "",
    val lastSchoolName: String = "",
    val classCompleted: String = "",
    val lastAcademicYear: String = "",
    val reasonForLeaving: String = "",
    val board: String = "",
    val fatherName: String = "",
    val fatherAge: String = "",
    val fatherQualification: String = "",
    val fatherProfession: String = "",
    val fatherIncome: String = "",
    val fatherPhone: String = "",
    val fatherAadhar: String = "",
    val fatherEmail: String = "",
    val motherName: String = "",
    val motherAge: String = "",
    val motherQualification: String = "",
    val motherProfession: String = "",
    val motherIncome: String = "",
    val motherPhone: String = "",
    val motherAadhar: String = "",
    val motherEmail: String = "",
    val relationshipStatus: String = "",
    val presentAddress: String = "",
    val permanentAddress: String = "",
    val languageSpoken: String = "",
    val schoolBudget: String = ""
)

data class FormErrors(val errors: Map<String, String> = emptyMap()) {
    fun getError(field: String) = errors[field] ?: ""
    fun isEmpty() = errors.isEmpty()
}

sealed class UiState {
    object Idle : UiState()
    object Submitting : UiState()
    object Success : UiState()
}

@Composable
fun StudentRegistrationForm() {
    var currentStep by remember { mutableStateOf(1) }
    var formData by remember { mutableStateOf(FormData()) }
    var formErrors by remember { mutableStateOf(FormErrors()) }
    var uiState by remember { mutableStateOf<UiState>(UiState.Idle) }

    if (uiState is UiState.Success) {
        StudentSuccessScreen { uiState = UiState.Idle }
        return
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    FormHeaderIcon()
                    FormTitle()
                    FormStepIndicator(currentStep)
                    DividerLine()
                }
            }

            when (currentStep) {
                1 -> item { Step1PersonalDetails(formData, formErrors) { formData = it } }
                2 -> item { Step2SchoolDetails(formData, formErrors) { formData = it } }
                3 -> item { Step3ParentsDetails(formData, formErrors) { formData = it } }
                4 -> item { Step4AddressSiblings(formData, formErrors) { formData = it } }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                NavigationButtons(
                    currentStep = currentStep,
                    isSubmitting = (uiState is UiState.Submitting),
                    onPrevious = { if (currentStep > 1) currentStep-- },
                    onNext = {
                        formErrors = validateStep(currentStep, formData)
                        if (formErrors.isEmpty() && currentStep < 4) {
                            currentStep++
                        }
                    },
                    onSubmit = {
                        formErrors = validateStep(currentStep, formData)
                        if (formErrors.isEmpty()) {
                            uiState = UiState.Submitting
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(key1 = uiState) {
        if (uiState is UiState.Submitting) {
            delay(2000)
            uiState = UiState.Success
        }
    }
}

@Composable
private fun FormHeaderIcon() {
    Text(
        text = "📋",
        fontSize = 48.sp,
        modifier = Modifier.padding(bottom = 14.dp)
    )
}

@Composable
private fun FormTitle() {
    Text(
        text = "Student Application Form",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(bottom = 16.dp)
    )
}

@Composable
private fun FormStepIndicator(step: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Step $step of 4",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5B5BFF)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color(0xFFE5E7EB), RoundedCornerShape(2.dp))
        ) {
            repeat(step) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color(0xFF5B5BFF), RoundedCornerShape(2.dp))
                        .padding(2.dp)
                )
            }
            repeat(4 - step) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun DividerLine() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE5E7EB))
            .padding(top = 16.dp)
    )
}

@Composable
fun StudentSuccessScreen(onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F7FA))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✅", fontSize = 80.sp, modifier = Modifier.padding(bottom = 24.dp))
        Text(
            "Application Submitted Successfully",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Text(
            "Your application has been received. We'll contact you soon.",
            fontSize = 15.sp,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Button(
            onClick = onBackClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B5BFF)),
            modifier = Modifier
                .width(200.dp)
                .height(48.dp),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Submit Another", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

fun validateStep(step: Int, data: FormData): FormErrors {
    val errors = mutableMapOf<String, String>()
    when (step) {
        1 -> {
            if (data.fullName.isBlank()) errors["fullName"] = "Full name is required"
            if (data.bloodGroup.isBlank()) errors["bloodGroup"] = "Blood group is required"
            if (data.interests.isBlank()) errors["interests"] = "Interests/hobbies are required"
        }
        2 -> {}
        3 -> {
            if (data.fatherName.isBlank()) errors["fatherName"] = "Father's name is required"
            if (data.fatherAge.isBlank()) errors["fatherAge"] = "Father's age is required"
            if (data.motherName.isBlank()) errors["motherName"] = "Mother's name is required"
            if (data.motherAge.isBlank()) errors["motherAge"] = "Mother's age is required"
            if (data.relationshipStatus.isBlank()) errors["relationshipStatus"] = "Relationship status is required"
        }
        4 -> {
            if (data.presentAddress.isBlank()) errors["presentAddress"] = "Present address is required"
            if (data.permanentAddress.isBlank()) errors["permanentAddress"] = "Permanent address is required"
            if (data.languageSpoken.isBlank()) errors["languageSpoken"] = "Language spoken is required"
            if (data.schoolBudget.isBlank()) errors["schoolBudget"] = "School budget is required"
        }
    }
    return FormErrors(errors)
}

@Composable
fun SectionHeading(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(bottom = 20.dp)
    )
}

@Composable
fun StepContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(24.dp)
    ) {
        content()
    }
}

@Composable
fun Step1PersonalDetails(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("👤 Student's Personal Details")

        TextInputField("Full Name*", formData.fullName, { onDataChange(formData.copy(fullName = it)) }, error = errors.getError("fullName"))
        TextInputField("Location", formData.location, { onDataChange(formData.copy(location = it)) })
        TextInputField("Date Of Birth", formData.dob, { onDataChange(formData.copy(dob = it)) }, placeholder = "mm/dd/yyyy")
        TextInputField("Age", formData.age, { onDataChange(formData.copy(age = it)) })

        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            StudentDropdownField("Gender", formData.gender, listOf("Female", "Male", "Other"), { onDataChange(formData.copy(gender = it)) }, Modifier.weight(1f), allowInput = true)
            StudentDropdownField("Nationality", formData.nationality, listOf("Nepali", "Other"), { onDataChange(formData.copy(nationality = it)) }, Modifier.weight(1f), allowInput = true)
        }

        TextInputField("Place Of Birth", formData.placeOfBirth, { onDataChange(formData.copy(placeOfBirth = it)) })

        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            StudentDropdownField("Religion", formData.religion, listOf("Hinduism", "Islam", "Christianity", "Sikhism", "Buddhism", "Jainism", "Other"), { onDataChange(formData.copy(religion = it)) }, Modifier.weight(1f), allowInput = true)
            TextInputField("Caste", formData.caste, { onDataChange(formData.copy(caste = it)) }, modifier = Modifier.weight(1f))
        }

        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            StudentDropdownField("Blood Group*", formData.bloodGroup, listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"), { onDataChange(formData.copy(bloodGroup = it)) }, Modifier.weight(1f), error = errors.getError("bloodGroup"), allowInput = true)
            TextInputField("Any Allergies", formData.allergies, { onDataChange(formData.copy(allergies = it)) }, modifier = Modifier.weight(1f))
        }

        TextInputField("Interests/Hobbies*", formData.interests, { onDataChange(formData.copy(interests = it)) }, error = errors.getError("interests"))
    }
}

@Composable
fun Step2SchoolDetails(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("📚 Previous School Details")
        TextInputField("Last School Name", formData.lastSchoolName, { onDataChange(formData.copy(lastSchoolName = it)) })
        TextInputField("Class Completed", formData.classCompleted, { onDataChange(formData.copy(classCompleted = it)) })
        TextInputField("Last Academic Year", formData.lastAcademicYear, { onDataChange(formData.copy(lastAcademicYear = it)) }, placeholder = "e.g., 2024")
        TextInputField("Reason For Leaving", formData.reasonForLeaving, { onDataChange(formData.copy(reasonForLeaving = it)) })
        TextInputField("Board", formData.board, { onDataChange(formData.copy(board = it)) }, placeholder = "e.g., CBSE, NEB")
    }
}

@Composable
fun Step3ParentsDetails(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("👥 Parents' & Guardian's Details")

        Text("Father's Details", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Father's Name*", formData.fatherName, { onDataChange(formData.copy(fatherName = it)) }, modifier = Modifier.weight(1f), error = errors.getError("fatherName"))
            TextInputField("Father's Age*", formData.fatherAge, { onDataChange(formData.copy(fatherAge = it)) }, modifier = Modifier.weight(1f), error = errors.getError("fatherAge"))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Father's Qualification*", formData.fatherQualification, { onDataChange(formData.copy(fatherQualification = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Father's Profession*", formData.fatherProfession, { onDataChange(formData.copy(fatherProfession = it)) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Father's Annual Income*", formData.fatherIncome, { onDataChange(formData.copy(fatherIncome = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Father's Phone No*", formData.fatherPhone, { onDataChange(formData.copy(fatherPhone = it)) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Father's Aadhar No*", formData.fatherAadhar, { onDataChange(formData.copy(fatherAadhar = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Father's Email*", formData.fatherEmail, { onDataChange(formData.copy(fatherEmail = it)) }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("Mother's Details", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 16.dp))
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Mother's Name*", formData.motherName, { onDataChange(formData.copy(motherName = it)) }, modifier = Modifier.weight(1f), error = errors.getError("motherName"))
            TextInputField("Mother's Age*", formData.motherAge, { onDataChange(formData.copy(motherAge = it)) }, modifier = Modifier.weight(1f), error = errors.getError("motherAge"))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Mother's Qualification*", formData.motherQualification, { onDataChange(formData.copy(motherQualification = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Mother's Profession*", formData.motherProfession, { onDataChange(formData.copy(motherProfession = it)) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Mother's Annual Income*", formData.motherIncome, { onDataChange(formData.copy(motherIncome = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Mother's Phone No*", formData.motherPhone, { onDataChange(formData.copy(motherPhone = it)) }, modifier = Modifier.weight(1f))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Mother's Aadhar No*", formData.motherAadhar, { onDataChange(formData.copy(motherAadhar = it)) }, modifier = Modifier.weight(1f))
            TextInputField("Mother's Email*", formData.motherEmail, { onDataChange(formData.copy(motherEmail = it)) }, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        StudentDropdownField("Parents' Relationship Status*", formData.relationshipStatus, listOf("Married", "Divorced", "Separated", "Widow/Widower", "Single", "Other"), { onDataChange(formData.copy(relationshipStatus = it)) }, error = errors.getError("relationshipStatus"), allowInput = true)
    }
}

@Composable
fun Step4AddressSiblings(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("📍 Address & Sibling Details")
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Present Address*", formData.presentAddress, { onDataChange(formData.copy(presentAddress = it)) }, modifier = Modifier.weight(1f), error = errors.getError("presentAddress"))
            TextInputField("Permanent Address*", formData.permanentAddress, { onDataChange(formData.copy(permanentAddress = it)) }, modifier = Modifier.weight(1f), error = errors.getError("permanentAddress"))
        }
        Row(modifier = Modifier.fillMaxWidth().gap(12.dp)) {
            TextInputField("Language Spoken at Home*", formData.languageSpoken, { onDataChange(formData.copy(languageSpoken = it)) }, modifier = Modifier.weight(1f), error = errors.getError("languageSpoken"))
            TextInputField("Yearly School Budget (INR)*", formData.schoolBudget, { onDataChange(formData.copy(schoolBudget = it)) }, modifier = Modifier.weight(1f), error = errors.getError("schoolBudget"))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("👨‍👩‍👧‍👦 Sibling Information (if any)", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151), modifier = Modifier.padding(bottom = 8.dp))
        Text("Sibling feature to be implemented", fontSize = 13.sp, color = Color(0xFF9CA3AF))
    }
}

@Composable
fun TextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String = ""
) {
    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, fontSize = 13.sp, color = Color(0xFFD1D5DB)) } } else null,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            isError = error.isNotEmpty(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF5B5BFF),
                unfocusedBorderColor = Color(0xFFE5E7EB),
                errorBorderColor = Color(0xFFEF4444),
                focusedLabelColor = Color(0xFF5B5BFF),
                unfocusedLabelColor = Color(0xFF6B7280)
            )
        )
        if (error.isNotEmpty()) {
            Text(error, fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 6.dp, start = 8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String = "",
    allowInput: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = { if (allowInput) onValueChange(it) },
                label = { Text(label) },
                readOnly = !allowInput,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .menuAnchor(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                isError = error.isNotEmpty(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5B5BFF),
                    unfocusedBorderColor = Color(0xFFE5E7EB),
                    errorBorderColor = Color(0xFFEF4444),
                    focusedLabelColor = Color(0xFF5B5BFF),
                    unfocusedLabelColor = Color(0xFF6B7280)
                )
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 13.sp) },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        }
                    )
                }
            }
        }
        if (error.isNotEmpty()) {
            Text(error, fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 6.dp, start = 8.dp))
        }
    }
}

@Composable
fun NavigationButtons(
    currentStep: Int,
    isSubmitting: Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onSubmit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (currentStep > 1) {
            Button(
                onClick = onPrevious,
                enabled = !isSubmitting,
                modifier = Modifier
                    .height(44.dp)
                    .width(110.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Previous", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF374151))
            }
        }

        if (isSubmitting) {
            CircularProgressIndicator(modifier = Modifier.size(44.dp), color = Color(0xFF5B5BFF))
        } else if (currentStep < 4) {
            Button(
                onClick = onNext,
                modifier = Modifier
                    .height(44.dp)
                    .width(100.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B5BFF)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Next", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        } else {
            Button(
                onClick = onSubmit,
                modifier = Modifier
                    .height(44.dp)
                    .width(160.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Submit", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}

fun Modifier.gap(space: Dp) = this.then(Modifier.padding(end = space))

@Preview(showBackground = true)
@Composable
fun StudentRegistrationFormPreview() {
    UNICKTheme {
        StudentRegistrationForm()
    }
}