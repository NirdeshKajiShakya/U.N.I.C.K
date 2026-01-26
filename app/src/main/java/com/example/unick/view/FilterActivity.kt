package com.example.unick.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.model.FilterResult
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.utils.filterWithinRadius

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
    var selectedFeeRange by remember { mutableStateOf("Any") }
    var selectedLocation by remember { mutableStateOf("Any") }
    var selectedPassRate by remember { mutableStateOf("Any") }
    var selectedCurriculum by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedFacilities by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLevel by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedRadiusLabel by remember { mutableStateOf("Any") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .statusBarsPadding()      // ✅ fixes top overlap
            .navigationBarsPadding()  // ✅ avoids gesture bar overlap
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

        // ✅ IMPORTANT FIX:
        // Use weight(1f) instead of fillMaxSize so bottom buttons ALWAYS show.
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {
            // Distance / Radius
            FilterSection(title = "Distance From You") {
                val radiusOptions = listOf("Any", "2 km", "5 km", "10 km", "20 km")
                FilterChipGroup(
                    items = radiusOptions,
                    selectedItem = selectedRadiusLabel,
                    onItemSelected = { selectedRadiusLabel = it }
                )
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

            // Pass Rate
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

        // Bottom Buttons (Always visible now)
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
                    val radiusKm = radiusLabelToKm(selectedRadiusLabel)

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

fun <T> applyDistanceFilterIfNeeded(
    userLat: Double,
    userLng: Double,
    radiusKm: Double?,
    schools: List<T>,
    latOf: (T) -> Double,
    lngOf: (T) -> Double
): List<T> {
    if (radiusKm == null) return schools

    return filterWithinRadius(
        userLat = userLat,
        userLng = userLng,
        radiusKm = radiusKm,
        items = schools,
        latOf = latOf,
        lngOf = lngOf
    )
}

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

@Preview(showBackground = true)
@Composable
fun FilterScreenPreview() {
    UNICKTheme {
        FilterScreen()
    }
}
