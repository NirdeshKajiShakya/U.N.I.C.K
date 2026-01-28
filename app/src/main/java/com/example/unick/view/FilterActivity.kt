package com.example.unick.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.unick.model.FilterResult
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.utils.filterWithinRadius
import com.google.android.gms.location.LocationServices

class FilterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UNICKTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    FilterScreen(
                        onBack = { finish() },
                        onApply = { result ->
                            val data = Intent().putExtra(EXTRA_FILTER_RESULT, result)
                            setResult(Activity.RESULT_OK, data)
                            finish()
                        }
                    )
                }
            }
        }
    }

    companion object {
        const val EXTRA_FILTER_RESULT = "extra_filter_result"
    }
}

@Composable
fun FilterScreen(
    onBack: () -> Unit = {},
    onApply: (FilterResult) -> Unit = {}
) {
    val context = LocalContext.current

    var selectedFeeRange by remember { mutableStateOf("Any") }
    var selectedLocation by remember { mutableStateOf("Any") }
    var selectedPassRate by remember { mutableStateOf("Any") }
    var selectedCurriculum by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFacilities by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLevel by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedRadiusLabel by remember { mutableStateOf("Any") }

    // ---------- Location status ----------
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }
    var permissionGranted by remember { mutableStateOf(false) }
    var locationTried by remember { mutableStateOf(false) } // to show better messages

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        permissionGranted = fine || coarse
        // After permission, try location again
        if (permissionGranted) {
            fetchLastLocation(context) { lat, lng ->
                userLat = lat
                userLng = lng
                locationTried = true
            }
        } else {
            locationTried = true
        }
    }

    // Initial permission check + fetch
    LaunchedEffect(Unit) {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        permissionGranted = fine || coarse

        if (permissionGranted) {
            fetchLastLocation(context) { lat, lng ->
                userLat = lat
                userLng = lng
                locationTried = true
            }
        } else {
            locationTried = true
        }
    }

    val radiusKm = remember(selectedRadiusLabel) { radiusLabelToKm(selectedRadiusLabel) }
    val wantsDistanceFilter = radiusKm != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color(0xFF0F172A)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Filters",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            TextButton(onClick = {
                selectedFeeRange = "Any"
                selectedLocation = "Any"
                selectedPassRate = "Any"
                selectedCurriculum = emptyList()
                selectedFacilities = emptyList()
                selectedLevel = emptyList()
                selectedRadiusLabel = "Any"
            }) {
                Text(
                    "Reset",
                    color = Color(0xFF2563EB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // ---------- Distance / Radius ----------
            FilterSection(title = "Distance From You") {
                val radiusOptions = listOf("Any", "2 km", "5 km", "10 km", "20 km")
                FilterChipGroup(
                    items = radiusOptions,
                    selectedItem = selectedRadiusLabel,
                    onItemSelected = { selectedRadiusLabel = it }
                )

                Spacer(Modifier.height(10.dp))

                // ✅ Status messages / guidance
                when {
                    !wantsDistanceFilter -> {
                        InfoPill("ℹ️ Distance filter is OFF (Any).")
                    }

                    wantsDistanceFilter && !permissionGranted -> {
                        WarningPill("⚠️ Location permission is not granted. Distance filter may not work.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                permissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            }
                        ) {
                            Text("Grant Location Permission")
                        }
                    }

                    wantsDistanceFilter && permissionGranted && (userLat == null || userLng == null) -> {
                        WarningPill("⚠️ Location not available right now. Turn ON GPS / try again.")
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = {
                                fetchLastLocation(context) { lat, lng ->
                                    userLat = lat
                                    userLng = lng
                                    locationTried = true
                                }
                            }
                        ) {
                            Text("Retry Location")
                        }
                    }

                    wantsDistanceFilter && permissionGranted && userLat != null && userLng != null -> {
                        SuccessPill("✅ Location ready. Distance filter will apply.")
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Extra guidance
                if (wantsDistanceFilter) {
                    InfoPill("ℹ️ Note: Schools without lat/lng (0.0) can’t be distance-filtered.")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Fee Structure
            FilterSection(title = "Annual Fee Range") {
                val feeRanges = listOf(
                    "Any",
                    "Under NPR 1 Lakh",
                    "NPR 1-3 Lakhs",
                    "NPR 3-5 Lakhs",
                    "NPR 5-10 Lakhs",
                    "Above NPR 10 Lakhs"
                )
                FilterChipGroup(
                    items = feeRanges,
                    selectedItem = selectedFeeRange,
                    onItemSelected = { selectedFeeRange = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location
            FilterSection(title = "Location") {
                val locations = listOf(
                    "Any",
                    "Kathmandu",
                    "Lalitpur",
                    "Bhaktapur",
                    "Pokhara",
                    "Butwal",
                    "Other Cities"
                )
                FilterChipGroup(
                    items = locations,
                    selectedItem = selectedLocation,
                    onItemSelected = { selectedLocation = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pass Rate (still UI only unless you add field later)
            FilterSection(title = "Student Pass Rate") {
                val passRates = listOf(
                    "Any",
                    "90%+ Pass Rate",
                    "80-90% Pass Rate",
                    "70-80% Pass Rate",
                    "Below 70%"
                )
                FilterChipGroup(
                    items = passRates,
                    selectedItem = selectedPassRate,
                    onItemSelected = { selectedPassRate = it }
                )
                Spacer(Modifier.height(8.dp))
                InfoPill("ℹ️ Pass rate filter needs a field in SchoolForm to work.")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Education Level
            FilterSection(title = "Education Level") {
                val levels = listOf(
                    "Nursery - Grade 5",
                    "Grade 6-10 (SEE)",
                    "+2 Science",
                    "+2 Management",
                    "+2 Humanities",
                    "A-Levels",
                    "IB Diploma"
                )
                MultiSelectChipGroup(
                    items = levels,
                    selectedItems = selectedLevel,
                    onItemsSelected = { selectedLevel = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Curriculum Type
            FilterSection(title = "Curriculum Type") {
                val curriculums = listOf(
                    "National Curriculum",
                    "A-Levels",
                    "IB Program",
                    "Montessori",
                    "International"
                )
                MultiSelectChipGroup(
                    items = curriculums,
                    selectedItems = selectedCurriculum,
                    onItemsSelected = { selectedCurriculum = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Facilities
            FilterSection(title = "Facilities") {
                val facilities = listOf(
                    "Science Labs",
                    "Computer Labs",
                    "Sports Facilities",
                    "Library",
                    "Transportation",
                    "Hostel/Boarding",
                    "Cafeteria",
                    "Swimming Pool",
                    "Auditorium"
                )
                MultiSelectChipGroup(
                    items = facilities,
                    selectedItems = selectedFacilities,
                    onItemsSelected = { selectedFacilities = it }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp)
            ) {
                Text(
                    "Cancel",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
            }

            Button(
                onClick = {
                    onApply(
                        FilterResult(
                            feeRange = selectedFeeRange,
                            location = selectedLocation,
                            passRate = selectedPassRate,
                            levels = selectedLevel,
                            curriculums = selectedCurriculum,
                            facilities = selectedFacilities,
                            radiusKm = radiusKm
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    "Apply Filters",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/** Gets last known location (simple + fast). */
@SuppressLint("MissingPermission")
private fun fetchLastLocation(
    context: android.content.Context,
    onResult: (Double?, Double?) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)
    fused.lastLocation
        .addOnSuccessListener { loc -> onResult(loc?.latitude, loc?.longitude) }
        .addOnFailureListener { onResult(null, null) }
}

/** Converts UI chip label -> KM value. null means "Any". */
private fun radiusLabelToKm(label: String): Double? {
    return when (label.trim()) {
        "2 km" -> 2.0
        "5 km" -> 5.0
        "10 km" -> 10.0
        "20 km" -> 20.0
        else -> null
    }
}

// --- Pills for messages ---

@Composable
private fun InfoPill(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFF0F172A),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEFF6FF), RoundedCornerShape(10.dp))
            .padding(10.dp)
    )
}

@Composable
private fun WarningPill(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFF7C2D12),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFEDD5), RoundedCornerShape(10.dp))
            .padding(10.dp)
    )
}

@Composable
private fun SuccessPill(text: String) {
    Text(
        text = text,
        fontSize = 13.sp,
        color = Color(0xFF065F46),
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFDCFCE7), RoundedCornerShape(10.dp))
            .padding(10.dp)
    )
}

/* ------------------ YOUR EXISTING UI COMPONENTS BELOW (UNCHANGED) ------------------ */

@Composable
fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Spacer(modifier = Modifier.height(12.dp))
        content()
    }
}

@Composable
fun FilterChipGroup(
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    FilterChip(
                        selected = selectedItem == item,
                        onClick = { onItemSelected(item) },
                        label = {
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                fontWeight = if (selectedItem == item) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = selectedItem == item,
                            borderColor = Color(0xFFE2E8F0),
                            selectedBorderColor = Color(0xFF2563EB)
                        )
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MultiSelectChipGroup(
    items: List<String>,
    selectedItems: List<String>,
    onItemsSelected: (List<String>) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    val isSelected = selectedItems.contains(item)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            val newSelection =
                                if (isSelected) selectedItems - item else selectedItems + item
                            onItemsSelected(newSelection)
                        },
                        label = {
                            Text(
                                text = item,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF64748B)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Color(0xFFE2E8F0),
                            selectedBorderColor = Color(0xFF2563EB)
                        )
                    )
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
