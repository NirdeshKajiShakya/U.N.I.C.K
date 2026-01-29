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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolViewModel

private val PrimaryBlue = Color(0xFF4A90E2)
private val AccentRed = Color(0xFFEF4444)
private val BorderGray = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1A1A2E)

class DataFormAcitivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        viewModel.fetchSchoolIfExists()

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
    val currentSchool by viewModel.currentSchool.collectAsState()
    val isEditMode = currentSchool != null
    val showDeleteDialog = remember { mutableStateOf(false) }

    // ---------- CHIP STATES ----------
    var selectedCurriculum by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedPrograms by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFacilities by remember { mutableStateOf<List<String>>(emptyList()) }

    // ---------- IMAGE PICKER ----------
    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
    }

    // ---------- PREFILL CHIPS ----------
    LaunchedEffect(currentSchool) {
        currentSchool?.let {
            selectedCurriculum = it.curriculum.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
            selectedPrograms = it.programsOffered.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
            selectedFacilities = it.facilities.split(",").map { s -> s.trim() }.filter { s -> s.isNotEmpty() }
        }
    }

    // ---------- SYNC CHIPS → TEXT ----------
    LaunchedEffect(selectedCurriculum) {
        viewModel.curriculum = selectedCurriculum.joinToString(", ")
    }
    LaunchedEffect(selectedPrograms) {
        viewModel.programsOffered = selectedPrograms.joinToString(", ")
    }
    LaunchedEffect(selectedFacilities) {
        viewModel.facilities = selectedFacilities.joinToString(", ")
    }

    // Delete confirmation dialog
    if (showDeleteDialog.value) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog.value = false },
            title = { Text("Delete School?") },
            text = { Text("This action cannot be undone. All school data including gallery and reviews will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSchool(context)
                        showDeleteDialog.value = false
                        Toast.makeText(context, "School deleted successfully", Toast.LENGTH_SHORT).show()
                        context.startActivity(Intent(context, DashboardActivity::class.java))
                        (context as ComponentActivity).finish()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog.value = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BorderGray)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {

        TopAppBar(
            title = {
                Text(
                    if (isEditMode) "Edit School Information" else "Add School Information",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = { (context as ComponentActivity).finish() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            // ---------- IMAGE ----------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE2E8F0))
                    .border(2.dp, Color(0xFF2563EB), RoundedCornerShape(16.dp))
                    .clickable { imagePicker.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (viewModel.imageUri != null) {
                    AsyncImage(
                        model = viewModel.imageUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(48.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionHeader("Basic Information")
            FormTextField(viewModel.schoolName, { viewModel.schoolName = it }, "School Name *", "St. Xavier's College")
            FormTextField(viewModel.location, { viewModel.location = it }, "Location *", "Kathmandu")
            FormTextField(viewModel.totalStudents, { viewModel.totalStudents = it }, "Total Students", "1500")
            FormTextField(viewModel.establishedYear, { viewModel.establishedYear = it }, "Established Year", "1998")

            SectionHeader("Contact Information")
            FormTextField(viewModel.principalName, { viewModel.principalName = it }, "Principal Name", "Dr. John Doe")
            FormTextField(viewModel.contactNumber, { viewModel.contactNumber = it }, "Contact Number *", "+977-1-XXXXXX")
            FormTextField(viewModel.email, { viewModel.email = it }, "Email", "info@school.edu.np")
            FormTextField(viewModel.website, { viewModel.website = it }, "Website", "https://school.edu.np")

            SectionHeader("Google Maps Location")
            FormTextField(
                viewModel.googleMapUrl,
                { viewModel.googleMapUrl = it },
                "Google Maps URL",
                "https://www.google.com/maps/place/.../@27.xx,85.xx"
            )
            // 🔹 Extract lat/lng whenever URL changes
            val latLng = remember(viewModel.googleMapUrl) {
                extractLatLngFromGoogleMapsUrl(viewModel.googleMapUrl)
            }

// 🔹 Save lat/lng into ViewModel
            LaunchedEffect(latLng) {
                if (latLng != null) {
                    viewModel.latitude = latLng.first
                    viewModel.longitude = latLng.second
                }
            }

// 🔹 User feedback
            if (viewModel.googleMapUrl.isNotBlank()) {
                if (latLng != null) {
                    Text(
                        text = "✅ Location detected (Lat: ${latLng.first}, Lng: ${latLng.second})",
                        fontSize = 13.sp,
                        color = Color(0xFF065F46)
                    )
                } else {
                    Text(
                        text = "⚠️ Invalid link. Please use a Google Maps link with '@lat,lng'",
                        fontSize = 13.sp,
                        color = Color(0xFF92400E)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }


            SectionHeader("Curriculum Type")
            FormTextField(viewModel.curriculum, { viewModel.curriculum = it }, "Curriculum", "National, A-Levels")
            FormMultiSelectChipGroup(
                items = listOf("National Curriculum", "A-Levels", "IB Program", "Montessori", "International"),
                selectedItems = selectedCurriculum,
                onItemsSelected = { selectedCurriculum = it }
            )

            SectionHeader("Programs Offered")
            FormTextField(viewModel.programsOffered, { viewModel.programsOffered = it }, "Programs", "Science, Management")
            FormMultiSelectChipGroup(
                items = listOf(
                    "Nursery - Grade 5",
                    "Grade 6-10 (SEE)",
                    "+2 Science",
                    "+2 Management",
                    "+2 Humanities",
                    "A-Levels",
                    "IB Diploma"
                ),
                selectedItems = selectedPrograms,
                onItemsSelected = { selectedPrograms = it }
            )

            SectionHeader("Facilities")
            FormTextField(viewModel.facilities, { viewModel.facilities = it }, "Facilities", "Library, Labs", multiline = true)
            FormMultiSelectChipGroup(
                items = listOf(
                    "Science Labs",
                    "Computer Labs",
                    "Sports Facilities",
                    "Library",
                    "Transportation",
                    "Hostel/Boarding",
                    "Cafeteria",
                    "Swimming Pool",
                    "Auditorium"
                ),
                selectedItems = selectedFacilities,
                onItemsSelected = { selectedFacilities = it }
            )

            SectionHeader("Facilities Options")
            CheckboxRow("Scholarship Available", viewModel.scholarshipAvailable) {
                viewModel.scholarshipAvailable = it
            }
            CheckboxRow("Transport Facility", viewModel.transportFacility) {
                viewModel.transportFacility = it
            }
            CheckboxRow("Hostel Facility", viewModel.hostelFacility) {
                viewModel.hostelFacility = it
            }

            SectionHeader("Fee Structure")
            FormTextField(viewModel.tuitionFee, { viewModel.tuitionFee = it }, "Annual Tuition Fee", "NPR 500,000")
            FormTextField(viewModel.admissionFee, { viewModel.admissionFee = it }, "Admission Fee", "NPR 50,000")

            SectionHeader("Extracurricular Activities")
            FormTextField(viewModel.extracurricular, { viewModel.extracurricular = it }, "Activities", "Sports, Music")

            SectionHeader("Description")
            FormTextField(
                viewModel.description,
                { viewModel.description = it },
                "School Description",
                "Describe your school...",
                multiline = true,
                minLines = 4
            )

            Spacer(Modifier.height(32.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Delete Button (only show in edit mode)
                if (isEditMode) {
                    Button(
                        onClick = { showDeleteDialog.value = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp)),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                    ) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Delete", fontWeight = FontWeight.SemiBold)
                    }
                }

                // Save Button
                Button(
                    onClick = {
                        viewModel.saveOrUpdateSchool(context)
                        Toast.makeText(
                            context,
                            if (isEditMode) "School updated successfully" else "School added successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        context.startActivity(Intent(context, DashboardActivity::class.java))
                        (context as ComponentActivity).finish()
                    },
                    modifier = Modifier
                        .weight(if (isEditMode) 1f else 1f)
                        .height(56.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp)),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                ) {
                    Text(if (isEditMode) "Update School Information" else "Submit School Information", fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/* ----------------- REUSABLE UI ----------------- */

@Composable
fun SectionHeader(title: String) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(12.dp))
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
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            singleLine = !multiline,
            minLines = minLines
        )
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun CheckboxRow(text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Text(text)
    }
}

@Composable
fun FormMultiSelectChipGroup(
    items: List<String>,
    selectedItems: List<String>,
    onItemsSelected: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { item ->
                    val selected = selectedItems.contains(item)
                    FilterChip(
                        selected = selected,
                        onClick = {
                            val updated =
                                if (selected) selectedItems - item else selectedItems + item
                            onItemsSelected(updated)
                        },
                        label = { Text(item) },
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}
private fun extractLatLngFromGoogleMapsUrl(url: String): Pair<Double, Double>? {
    val regex = Regex("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    val match = regex.find(url) ?: return null
    val lat = match.groupValues[1].toDoubleOrNull()
    val lng = match.groupValues[2].toDoubleOrNull()
    return if (lat != null && lng != null) lat to lng else null
}