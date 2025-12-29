package com.example.unick.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unick.view.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolViewModel

class DataFormAcitivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    SchoolDataForm(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDataForm(viewModel: SchoolViewModel) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val isDataSaved by viewModel.isDataSaved.collectAsState()

    LaunchedEffect(isDataSaved) {
        if (isDataSaved) {
            Toast.makeText(context, "School data saved successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(context, DashboardActivity::class.java)
            context.startActivity(intent)
            (context as? ComponentActivity)?.finish()
        }
    }

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top App Bar
        TopAppBar(
            title = {
                Text(
                    "Add School Information",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    (context as? ComponentActivity)?.finish()
                }) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color(0xFF0F172A)
            ),
            modifier = Modifier.shadow(4.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Image Upload Section
                Text(
                    text = "School Image",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (viewModel.imageUri == null) {
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFE2E8F0),
                                        Color(0xFFCBD5E1)
                                    )
                                )
                            } else {
                                Brush.linearGradient(colors = listOf(Color.Transparent, Color.Transparent))
                            }
                        )
                        .border(
                            width = 2.dp,
                            color = Color(0xFF2563EB).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(16.dp)
                        )
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.imageUri != null) {
                        AsyncImage(
                            model = viewModel.imageUri,
                            contentDescription = "School Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Upload Image",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Tap to upload school image",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Basic Information Section
                SectionHeader("Basic Information")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.schoolName,
                    onValueChange = { viewModel.schoolName = it },
                    label = "School Name *",
                    placeholder = "e.g., St. Xavier's College"
                )

                FormTextField(
                    value = viewModel.location,
                    onValueChange = { viewModel.location = it },
                    label = "Location *",
                    placeholder = "e.g., Maitighar, Kathmandu"
                )

                FormTextField(
                    value = viewModel.totalStudents,
                    onValueChange = { viewModel.totalStudents = it },
                    label = "Total Students",
                    placeholder = "e.g., 1500"
                )

                FormTextField(
                    value = viewModel.establishedYear,
                    onValueChange = { viewModel.establishedYear = it },
                    label = "Established Year",
                    placeholder = "e.g., 1988"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Contact Information Section
                SectionHeader("Contact Information")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.principalName,
                    onValueChange = { viewModel.principalName = it },
                    label = "Principal Name",
                    placeholder = "e.g., Dr. John Smith"
                )

                FormTextField(
                    value = viewModel.contactNumber,
                    onValueChange = { viewModel.contactNumber = it },
                    label = "Contact Number *",
                    placeholder = "e.g., +977-01-4123456"
                )

                FormTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = "Email",
                    placeholder = "e.g., info@school.edu.np"
                )

                FormTextField(
                    value = viewModel.website,
                    onValueChange = { viewModel.website = it },
                    label = "Website",
                    placeholder = "e.g., www.school.edu.np"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Academic Information Section
                SectionHeader("Academic Information")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.curriculum,
                    onValueChange = { viewModel.curriculum = it },
                    label = "Curriculum Type *",
                    placeholder = "e.g., National, A-Levels, IB"
                )

                FormTextField(
                    value = viewModel.programsOffered,
                    onValueChange = { viewModel.programsOffered = it },
                    label = "Programs Offered",
                    placeholder = "e.g., Science, Management, Humanities",
                    multiline = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Facilities Section
                SectionHeader("Facilities & Amenities")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.facilities,
                    onValueChange = { viewModel.facilities = it },
                    label = "Facilities",
                    placeholder = "e.g., Library, Labs, Sports Ground, Cafeteria",
                    multiline = true
                )

                // Checkboxes for facilities
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.transportFacility,
                        onCheckedChange = { viewModel.transportFacility = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB))
                    )
                    Text(
                        text = "Transport Facility",
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.hostelFacility,
                        onCheckedChange = { viewModel.hostelFacility = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB))
                    )
                    Text(
                        text = "Hostel Facility",
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.scholarshipAvailable,
                        onCheckedChange = { viewModel.scholarshipAvailable = it },
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2563EB))
                    )
                    Text(
                        text = "Scholarship Available",
                        fontSize = 14.sp,
                        color = Color(0xFF0F172A)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.extracurricular,
                    onValueChange = { viewModel.extracurricular = it },
                    label = "Extracurricular Activities",
                    placeholder = "e.g., Sports, Music, Drama, Art",
                    multiline = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Fee Structure Section
                SectionHeader("Fee Structure")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.tuitionFee,
                    onValueChange = { viewModel.tuitionFee = it },
                    label = "Annual Tuition Fee",
                    placeholder = "e.g., NPR 500,000"
                )

                FormTextField(
                    value = viewModel.admissionFee,
                    onValueChange = { viewModel.admissionFee = it },
                    label = "Admission Fee",
                    placeholder = "e.g., NPR 50,000"
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Description Section
                SectionHeader("Description")
                Spacer(modifier = Modifier.height(12.dp))

                FormTextField(
                    value = viewModel.description,
                    onValueChange = { viewModel.description = it },
                    label = "School Description",
                    placeholder = "Provide a brief description about the school, its vision, mission, and unique features...",
                    multiline = true,
                    minLines = 4
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = { viewModel.saveSchoolData(context) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text(
                        "Submit School Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0F172A)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Box(
        modifier = Modifier
            .width(40.dp)
            .height(3.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF2563EB), Color(0xFF3B82F6))
                ),
                RoundedCornerShape(2.dp)
            )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    multiline: Boolean = false,
    minLines: Int = 1
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F172A),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(12.dp)),
            placeholder = {
                Text(
                    text = placeholder,
                    fontSize = 14.sp,
                    color = Color(0xFF94A3B8)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color(0xFFCBD5E1),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = !multiline,
            minLines = if (multiline) minLines else 1
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun SchoolDataFormPreview() {
    UNICKTheme {
        // SchoolDataForm(viewModel = SchoolViewModel()) // This will not work in preview
    }
}
