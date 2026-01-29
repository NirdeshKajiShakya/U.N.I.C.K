package com.example.unick.view

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.model.StudentApplication
import com.example.unick.repo.ApplicationRepoImpl
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.StudentApplicationViewModel
import com.example.unick.viewmodel.SubmitState
import java.util.Calendar

class StudentApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get the schoolId from intent (passed when student clicks "Apply" on a school)
        val schoolId = intent.getStringExtra("schoolId") ?: ""

        // Debug: Show which schoolId will be used
        android.util.Log.d("StudentApplicationActivity", "Applying to schoolId: $schoolId")

        // Validate schoolId - cannot submit application without knowing which school
        if (schoolId.isEmpty()) {
            android.widget.Toast.makeText(
                this,
                "Error: School ID is missing. Please select a school first.",
                android.widget.Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        setContent {
            UNICKTheme {
                StudentRegistrationForm(schoolId = schoolId)
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
    val standard: String = "",
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
    val schoolBudget: String = "",
    val siblingSex: String = "",
    val siblingAge: String = "",
    val siblingName: String = ""
)

data class FormErrors(val errors: Map<String, String> = emptyMap()) {
    fun getError(field: String) = errors[field] ?: ""
    fun isEmpty() = errors.isEmpty()
}

// Mapping function: Convert FormData to StudentApplication
fun FormData.toStudentApplication(schoolId: String, studentId: String): StudentApplication {
    return StudentApplication(
        schoolId = schoolId,
        studentId = studentId,
        fullName = fullName,
        dob = dob,
        gender = gender,
        bloodGroup = bloodGroup,
        interests = interests,
        lastSchoolName = lastSchoolName,
        standard = standard,
        fatherName = fatherName,
        fatherPhone = fatherPhone,
        motherName = motherName,
        motherPhone = motherPhone,
        presentAddress = presentAddress,
        permanentAddress = permanentAddress,
        schoolBudget = schoolBudget,
        status = "pending",
        timestamp = System.currentTimeMillis()
    )
}

@Composable
fun StudentRegistrationForm(schoolId: String = "") {
    // ViewModel integration
    val viewModel = viewModel<StudentApplicationViewModel>(
        factory = StudentApplicationViewModel.Factory(ApplicationRepoImpl())
    )
    val submitState by viewModel.submitState.collectAsState()

    // Get current user ID for studentId
    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var currentStep by remember { mutableStateOf(1) }
    var formData by remember { mutableStateOf(FormData()) }
    var formErrors by remember { mutableStateOf(FormErrors()) }

    // Show success screen
    if (submitState is SubmitState.Success) {
        StudentSuccessScreen {
            currentStep = 1
            formData = FormData()
            formErrors = FormErrors()
            viewModel.resetState()  // Reset state to Idle
        }
        return
    }

    // Show error dialog if submission fails
    if (submitState is SubmitState.Error) {
        ErrorDialog(
            errorMessage = (submitState as SubmitState.Error).message,
            onDismiss = {
                viewModel.resetState()  // Reset to Idle to allow retry
            },
            onRetry = {
                // Retry submission
                viewModel.submitApplication(formData.toStudentApplication(schoolId, currentUserId))
            }
        )
    }

    val context = LocalContext.current

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        contentWindowInsets = WindowInsets.safeDrawing, // Ensure safe drawing insets
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = "",
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.Home.route -> {
                             val intent = Intent(context, DashboardActivity::class.java)
                             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                             context.startActivity(intent)
                        }
                        BottomNavItem.AIChat.route -> {
                             val intent = Intent(context, DashboardActivity::class.java)
                             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                             intent.putExtra("start_destination", BottomNavItem.AIChat.route)
                             context.startActivity(intent)
                        }
                        BottomNavItem.Profile.route -> {
                             val intent = Intent(context, UserProfileActivity::class.java)
                             context.startActivity(intent)
                        }
                        else -> {
                             val intent = Intent(context, DashboardActivity::class.java)
                             intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                             intent.putExtra("start_destination", route)
                             context.startActivity(intent)
                        }
                    }
                },
                onProfileClick = {
                     val intent = Intent(context, UserProfileActivity::class.java)
                     context.startActivity(intent)
                },
                navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Search,
                    BottomNavItem.AIChat,
                    BottomNavItem.Notification,
                    BottomNavItem.Profile
                )
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F7FA)),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 20.dp)
            ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(20.dp),
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
                Spacer(modifier = Modifier.height(20.dp))
                NavigationButtons(
                    currentStep = currentStep,
                    isSubmitting = (submitState is SubmitState.Loading),
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
                            viewModel.submitApplication(formData.toStudentApplication(schoolId, currentUserId))
                        }
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
}

@Composable
private fun FormHeaderIcon() {
    Text(
        text = "📋",
        fontSize = 48.sp,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
private fun FormTitle() {
    Text(
        text = "Student Application Form",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937),
        modifier = Modifier.padding(bottom = 12.dp)
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
        Spacer(modifier = Modifier.height(11.dp))
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
            .padding(top = 12.dp)
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

@Composable
fun ErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text("⚠️", fontSize = 48.sp)
        },
        title = {
            Text(
                "Submission Failed",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFFEF4444)
            )
        },
        text = {
            Column {
                Text(
                    errorMessage,
                    fontSize = 14.sp,
                    color = Color(0xFF374151)
                )

                // Add helpful instructions if it's a permission error
                if (errorMessage.contains("Permission Denied", ignoreCase = true)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "📌 Quick Fix:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF5B5BFF)
                    )
                    Text(
                        "Open Firebase Console → Realtime Database → Rules → Use test mode for development",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B5BFF))
            ) {
                Text("Retry")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF6B7280))
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
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
        fontSize = 17.sp,
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
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(20.dp)
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
        DatePickerField("Date Of Birth", formData.dob, { onDataChange(formData.copy(dob = it)) }, placeholder = "mm/dd/yyyy", error = errors.getError("dob"))
        TextInputField("Age", formData.age, { onDataChange(formData.copy(age = it)) })

        StudentDropdownField("Gender", formData.gender, listOf("Female", "Male", "Other"), { onDataChange(formData.copy(gender = it)) }, error = errors.getError("gender"))
        StudentDropdownField("Nationality", formData.nationality, listOf("Nepali", "Other"), { onDataChange(formData.copy(nationality = it)) })

        TextInputField("Place Of Birth", formData.placeOfBirth, { onDataChange(formData.copy(placeOfBirth = it)) })

        StudentDropdownField("Religion", formData.religion, listOf("Hinduism", "Islam", "Christianity", "Sikhism", "Buddhism", "Jainism", "Other"), { onDataChange(formData.copy(religion = it)) })
        TextInputField("Caste", formData.caste, { onDataChange(formData.copy(caste = it)) })

        StudentDropdownField("Blood Group*", formData.bloodGroup, listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"), { onDataChange(formData.copy(bloodGroup = it)) }, error = errors.getError("bloodGroup"))
        TextInputField("Any Allergies", formData.allergies, { onDataChange(formData.copy(allergies = it)) })
        TextInputField("Interests/Hobbies*", formData.interests, { onDataChange(formData.copy(interests = it)) }, error = errors.getError("interests"))
    }
}

@Composable
fun Step2SchoolDetails(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("📚 Previous School Details")
        TextInputField("Last School Name", formData.lastSchoolName, { onDataChange(formData.copy(lastSchoolName = it)) })
        TextInputField("Class Completed", formData.classCompleted, { onDataChange(formData.copy(classCompleted = it)) })
        StudentDropdownField("Standard", formData.standard, listOf("Nursery", "LKG", "UKG", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9", "Grade 10"), { onDataChange(formData.copy(standard = it)) })
        TextInputField("Last Academic Year", formData.lastAcademicYear, { onDataChange(formData.copy(lastAcademicYear = it)) }, placeholder = "e.g., 2024")
        TextInputField("Reason For Leaving", formData.reasonForLeaving, { onDataChange(formData.copy(reasonForLeaving = it)) })
    }
}

@Composable
fun Step3ParentsDetails(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("👥 Parents' & Guardian's Details")

        Text("Father's Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5B5BFF), modifier = Modifier.padding(bottom = 16.dp))
        TextInputField("Father's Name*", formData.fatherName, { onDataChange(formData.copy(fatherName = it)) }, error = errors.getError("fatherName"))
        TextInputField("Father's Age*", formData.fatherAge, { onDataChange(formData.copy(fatherAge = it)) }, error = errors.getError("fatherAge"))
        TextInputField("Father's Qualification*", formData.fatherQualification, { onDataChange(formData.copy(fatherQualification = it)) })
        TextInputField("Father's Profession*", formData.fatherProfession, { onDataChange(formData.copy(fatherProfession = it)) })
        TextInputField("Father's Annual Income*", formData.fatherIncome, { onDataChange(formData.copy(fatherIncome = it)) })
        TextInputField("Father's Phone No*", formData.fatherPhone, { onDataChange(formData.copy(fatherPhone = it)) })
        TextInputField("Father's CitizenShip No*", formData.fatherAadhar, { onDataChange(formData.copy(fatherAadhar = it)) })
        TextInputField("Father's Email*", formData.fatherEmail, { onDataChange(formData.copy(fatherEmail = it)) })

        Spacer(modifier = Modifier.height(24.dp))
        Text("Mother's Details", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5B5BFF), modifier = Modifier.padding(bottom = 16.dp))
        TextInputField("Mother's Name*", formData.motherName, { onDataChange(formData.copy(motherName = it)) }, error = errors.getError("motherName"))
        TextInputField("Mother's Age*", formData.motherAge, { onDataChange(formData.copy(motherAge = it)) }, error = errors.getError("motherAge"))
        TextInputField("Mother's Qualification*", formData.motherQualification, { onDataChange(formData.copy(motherQualification = it)) })
        TextInputField("Mother's Profession*", formData.motherProfession, { onDataChange(formData.copy(motherProfession = it)) })
        TextInputField("Mother's Annual Income*", formData.motherIncome, { onDataChange(formData.copy(motherIncome = it)) })
        TextInputField("Mother's Phone No*", formData.motherPhone, { onDataChange(formData.copy(motherPhone = it)) })
        TextInputField("Mother's CitizenShip No*", formData.motherAadhar, { onDataChange(formData.copy(motherAadhar = it)) })
        TextInputField("Mother's Email*", formData.motherEmail, { onDataChange(formData.copy(motherEmail = it)) })

        Spacer(modifier = Modifier.height(16.dp))
        StudentDropdownField("Parents' Relationship Status*", formData.relationshipStatus, listOf("Married", "Divorced", "Separated", "Widow/Widower", "Single", "Other"), { onDataChange(formData.copy(relationshipStatus = it)) }, error = errors.getError("relationshipStatus"))
    }
}

@Composable
fun Step4AddressSiblings(formData: FormData, errors: FormErrors, onDataChange: (FormData) -> Unit) {
    StepContainer {
        SectionHeading("📍 Address & Sibling Details")
        TextInputField("Present Address*", formData.presentAddress, { onDataChange(formData.copy(presentAddress = it)) }, error = errors.getError("presentAddress"))
        TextInputField("Permanent Address*", formData.permanentAddress, { onDataChange(formData.copy(permanentAddress = it)) }, error = errors.getError("permanentAddress"))
        TextInputField("Language Spoken at Home*", formData.languageSpoken, { onDataChange(formData.copy(languageSpoken = it)) }, error = errors.getError("languageSpoken"))
        TextInputField("Yearly School Budget (INR)*", formData.schoolBudget, { onDataChange(formData.copy(schoolBudget = it)) }, error = errors.getError("schoolBudget"))

        Spacer(modifier = Modifier.height(24.dp))
        Text("👨‍👩‍👧‍👦 Sibling Information (if any)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF5B5BFF), modifier = Modifier.padding(bottom = 16.dp))
        TextInputField("Sibling Name", formData.siblingName, { onDataChange(formData.copy(siblingName = it)) })
        StudentDropdownField("Sex", formData.siblingSex, listOf("Male", "Female"), { onDataChange(formData.copy(siblingSex = it)) })
        TextInputField("Age", formData.siblingAge, { onDataChange(formData.copy(siblingAge = it)) })
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
    Column(modifier = modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF374151),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, fontSize = 13.sp, color = Color(0xFFD1D5DB)) } } else null,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            isError = error.isNotEmpty(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF5B5BFF),
                unfocusedBorderColor = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFFE5E7EB),
                errorBorderColor = Color(0xFFEF4444),
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            )
        )
        if (error.isNotEmpty()) {
            Text(error, fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 4.dp, start = 4.dp))
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    error: String = ""
) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }
    var openDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF374151),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .clickable { openDialog = true },
            placeholder = if (placeholder.isNotEmpty()) { { Text(placeholder, fontSize = 13.sp, color = Color(0xFFD1D5DB)) } } else null,
            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
            isError = error.isNotEmpty(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { openDialog = true }) {
                    Icon(Icons.Filled.CalendarToday, contentDescription = "Pick date")
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF5B5BFF),
                unfocusedBorderColor = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFFE5E7EB),
                errorBorderColor = Color(0xFFEF4444),
                focusedTextColor = Color(0xFF1F2937),
                unfocusedTextColor = Color(0xFF1F2937)
            )
        )

        if (openDialog) {
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            DatePickerDialog(context, { _, y, m, d ->
                val formatted = String.format("%02d/%02d/%04d", m + 1, d, y)
                onValueChange(formatted)
                openDialog = false
            }, year, month, day).apply {
                setOnCancelListener { openDialog = false }
                show()
            }
        }

        if (error.isNotEmpty()) {
            Text(error, fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 4.dp, start = 4.dp))
        }
    }
}

@Composable
fun StudentDropdownField(
    label: String,
    value: String,
    options: List<String>,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    error: String = ""
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(bottom = 14.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFF374151),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        Box(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(
                        width = 1.dp,
                        color = if (error.isNotEmpty()) Color(0xFFEF4444) else Color(0xFFE5E7EB),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (value.isEmpty()) "Select..." else value,
                        fontSize = 14.sp,
                        color = if (value.isEmpty()) Color(0xFFD1D5DB) else Color(0xFF1F2937),
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp)
                    )
                    Icon(
                        Icons.Filled.ArrowDropDown,
                        contentDescription = null,
                        tint = Color(0xFF6B7280),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.TopStart)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(option, fontSize = 14.sp, color = Color(0xFF1F2937))
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        modifier = Modifier.height(40.dp)
                    )
                }
            }
        }

        if (error.isNotEmpty()) {
            Text(error, fontSize = 11.sp, color = Color(0xFFEF4444), modifier = Modifier.padding(top = 4.dp, start = 4.dp))
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
            .background(Color.White, RoundedCornerShape(12.dp))
            .padding(20.dp),
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

fun Modifier.border(width: Dp, color: Color, shape: RoundedCornerShape): Modifier {
    return this.then(
        Modifier.background(color, shape).padding(width)
    )
}

@Preview(showBackground = true)
@Composable
fun StudentRegistrationFormPreview() {
    UNICKTheme {
        StudentRegistrationForm()
    }
}