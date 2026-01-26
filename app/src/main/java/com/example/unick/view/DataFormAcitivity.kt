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
import androidx.compose.material.icons.filled.OpenInNew
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
import com.example.unick.ui.theme.UNICKTheme
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
            context.startActivity(Intent(context, DashboardActivity::class.java))
            (context as? ComponentActivity)?.finish()
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.imageUri = uri
    }

    // ✅ SAFE STATE (THIS FIXES ALL `it` ERRORS)
    var selectedCurriculum by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedPrograms by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFacilities by remember { mutableStateOf<List<String>>(emptyList()) }

    var googleMapUrl by remember { mutableStateOf("") }
    val extractedLatLng = remember(googleMapUrl) {
        extractLatLngFromGoogleMapsUrl(googleMapUrl)
    }

    // Sync chip selections to viewModel (comma-separated)
    LaunchedEffect(selectedCurriculum) {
        viewModel.curriculum = selectedCurriculum.joinToString(", ")
    }
    LaunchedEffect(selectedPrograms) {
        viewModel.programsOffered = selectedPrograms.joinToString(", ")
    }
    LaunchedEffect(selectedFacilities) {
        viewModel.facilities = selectedFacilities.joinToString(", ")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
    ) {
        TopAppBar(
            title = { Text("Add School Information", fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = { (context as ComponentActivity).finish() }) {
                    Icon(Icons.Default.ArrowBack, null)
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

            // Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFE2E8F0))
                    .border(2.dp, Color(0xFF2563EB), RoundedCornerShape(16.dp))
                    .clickable { imagePickerLauncher.launch("image/*") },
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
                    Icon(Icons.Default.CameraAlt, null, tint = Color(0xFF2563EB), modifier = Modifier.size(48.dp))
                }
            }

            Spacer(Modifier.height(24.dp))

            SectionHeader("Basic Info")

            FormTextField(viewModel.schoolName, { viewModel.schoolName = it }, "School Name *", "St. Xavier's")
            FormTextField(viewModel.location, { viewModel.location = it }, "Location *", "Kathmandu")

            // Google Maps URL
            Text("Google Maps Link *", fontWeight = FontWeight.Medium)
            OutlinedTextField(
                value = googleMapUrl,
                onValueChange = { googleMapUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://www.google.com/maps/place/.../@27.xx,85.xx") },
                trailingIcon = {
                    IconButton(onClick = {
                        if (googleMapUrl.isNotBlank()) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(googleMapUrl)))
                        }
                    }) {
                        Icon(Icons.Default.OpenInNew, null)
                    }
                }
            )

            if (extractedLatLng != null) {
                Text("✅ Location detected: ${extractedLatLng.first}, ${extractedLatLng.second}",
                    fontSize = 13.sp, color = Color(0xFF065F46))
            } else if (googleMapUrl.isNotBlank()) {
                Text("⚠️ Use a Google Maps link with @lat,lng",
                    fontSize = 13.sp, color = Color(0xFF92400E))
            }

            Spacer(Modifier.height(24.dp))

            SectionHeader("Curriculum Type")
            MultiSelectChipGroup(
                items = listOf("National Curriculum", "A-Levels",
                    "IB Program", "Montessori", "International"),
                selectedItems = selectedCurriculum,
                onItemsSelected = { newList -> selectedCurriculum = newList }
            )

            Spacer(Modifier.height(16.dp))

            SectionHeader("Programs Offered")
            MultiSelectChipGroup(
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
                onItemsSelected = { newList -> selectedPrograms = newList }
            )

            Spacer(Modifier.height(16.dp))

            SectionHeader("Facilities")
            MultiSelectChipGroup(
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
                onItemsSelected = { newList -> selectedFacilities = newList }
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    // later you will store googleMapUrl + extractedLatLng in ViewModel
                    viewModel.saveSchoolData(context)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Submit School Information", fontWeight = FontWeight.Bold)
            }
        }
    }
}

/* ---------------- COMPONENTS ---------------- */

@Composable
fun SectionHeader(title: String) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String
) {
    Column {
        Text(label, fontWeight = FontWeight.Medium)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) }
        )
        Spacer(Modifier.height(16.dp))
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
                            val newList =
                                if (selected) selectedItems - item else selectedItems + item
                            onItemsSelected(newList)
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

/* -------- MAP URL PARSER -------- */

private fun extractLatLngFromGoogleMapsUrl(url: String): Pair<Double, Double>? {
    val regex = Regex("@(-?\\d+\\.\\d+),(-?\\d+\\.\\d+)")
    val match = regex.find(url) ?: return null
    val lat = match.groupValues[1].toDoubleOrNull()
    val lng = match.groupValues[2].toDoubleOrNull()
    return if (lat != null && lng != null) lat to lng else null
}
