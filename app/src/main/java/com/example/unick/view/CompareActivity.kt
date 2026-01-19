package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.unick.model.Schools


class CompareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFF2196F3),
                    secondary = Color(0xFF03DAC5),
                    background = Color(0xFFF5F7FA), // Light grey background
                    surface = Color.White
                )
            ) {
                // Initialize with passed school data
                val schoolForm = androidx.core.content.IntentCompat.getParcelableExtra(intent, "school_details", com.example.unick.model.SchoolForm::class.java)
                SchoolCompareScreen(initialSchool = schoolForm)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolCompareScreen(initialSchool: com.example.unick.model.SchoolForm? = null) {
    // State management
    // Map initialSchool to Schools model if present
    val initialMapped = remember(initialSchool) {
        initialSchool?.let { form ->
            Schools(
                id = form.uid.hashCode(),
                name = form.schoolName,
                fee = form.tuitionFee.ifBlank { "$12,000" }, // Fallback if empty
                rating = if (form.verified) "4.5" else "4.0",
                extraCurricular = form.extracurricular,
                location = form.location,
                distance = "N/A", // Not in SchoolForm
                achievements = listOf(),
                schoolType = "Private", // Default or derive
                genderType = "Co-ed"
            )
        }
    }

    var school1 by remember { mutableStateOf<Schools?>(initialMapped) }
    var school2 by remember { mutableStateOf<Schools?>(null) }

    // Dialog State
    var showDialog by remember { mutableStateOf(false) }
    var activeSlot by remember { mutableStateOf(1) } // 1 or 2 to know which slot is being filled

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Compare Schools", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Selection Area (The "Slots")
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Slot 1
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        school = school1,
                        label = "School A",
                        onAddClick = { activeSlot = 1; showDialog = true },
                        onRemoveClick = { school1 = null }
                    )

                    // Slot 2
                    SelectionCard(
                        modifier = Modifier.weight(1f),
                        school = school2,
                        label = "School B",
                        onAddClick = { activeSlot = 2; showDialog = true },
                        onRemoveClick = { school2 = null }
                    )
                }
            }

            // 2. Comparison Table
            if (school1 != null || school2 != null) {
                item {
                    Text(
                        "Comparison Result",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(0.8f)) // Spacer for icon
                                Text(school1?.name ?: "-", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp, maxLines = 1)
                                Text(school2?.name ?: "-", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, fontSize = 12.sp, maxLines = 1)
                            }

                            // Data Rows
                            val fields = getComparisonFields()
                            fields.forEachIndexed { index, field ->
                                ComparisonRowItem(
                                    field = field,
                                    s1 = school1,
                                    s2 = school2,
                                    isOdd = index % 2 != 0
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty State illustration
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select two schools to compare details", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    // Selection Dialog
    if (showDialog) {
        SchoolSelectionDialog(
            onDismiss = { showDialog = false },
            onSchoolSelected = { selected ->
                if (activeSlot == 1) school1 = selected else school2 = selected
                showDialog = false
            }
        )
    }
}

// --- UI COMPONENTS ---

@Composable
fun SelectionCard(
    modifier: Modifier = Modifier,
    school: Schools?,
    label: String,
    onAddClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(160.dp) // Fixed height for uniformity
            .clickable { if (school == null) onAddClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (school != null) MaterialTheme.colorScheme.primaryContainer else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (school != null) 4.dp else 1.dp),
        border = if (school == null) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
            if (school != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.align(Alignment.Center)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(school.name, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis)
                    Text(school.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
                // Close button
                IconButton(
                    onClick = onRemoveClick,
                    modifier = Modifier.align(Alignment.TopEnd).size(24.dp)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Remove", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Add $label", color = Color.Gray, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ComparisonRowItem(field: CompField, s1: Schools?, s2: Schools?, isOdd: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isOdd) Color(0xFFF5F7FA) else Color.White) // Zebra striping
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Label Column
        Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
            Icon(field.icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text(field.label, fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
        }

        // Value 1
        Text(
            text = if (s1 != null) field.getter(s1) else "-",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = if(field.highlight) FontWeight.Bold else FontWeight.Normal,
            color = if(field.highlight) MaterialTheme.colorScheme.primary else Color.Black
        )

        // Value 2
        Text(
            text = if (s2 != null) field.getter(s2) else "-",
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center,
            fontSize = 13.sp,
            fontWeight = if(field.highlight) FontWeight.Bold else FontWeight.Normal,
            color = if(field.highlight) MaterialTheme.colorScheme.primary else Color.Black
        )
    }
}

@Composable
fun SchoolSelectionDialog(onDismiss: () -> Unit, onSchoolSelected: (Schools) -> Unit) {
    val schools = getSampleSchools()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Select a School", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(schools) { school ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSchoolSelected(school) }
                                .background(Color(0xFFF5F7FA))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(school.name.take(1), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(school.name, fontWeight = FontWeight.Bold)
                                Text(school.location, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                    Text("Cancel")
                }
            }
        }
    }
}

// --- DATA & HELPERS ---

data class CompField(
    val label: String,
    val icon: ImageVector,
    val getter: (Schools) -> String,
    val highlight: Boolean = false
)

fun getComparisonFields() = listOf(
    CompField("Annual Fee", Icons.Default.Info, { it.fee }, true),
    CompField("Rating", Icons.Default.Star, { it.rating }, true),
    CompField("Curriculum", Icons.Default.Edit, { "CBSE" }), // Mock data
    CompField("Activities", Icons.Default.Favorite, { it.extraCurricular }),
    CompField("Location", Icons.Default.Place, { it.location }),
    CompField("Distance", Icons.Default.Settings, { it.distance }),
    CompField("Type", Icons.Default.Check, { it.schoolType }),
    CompField("Gender", Icons.Default.Person, { it.genderType })
)

fun getSampleSchools() = listOf(
    Schools(1, "Sunrise Academy", "$12,000", "4.2", "Football, Art", "Uptown", "8 miles", listOf(), "Public", "Co-ed"),
    Schools(2, "Greenwood High", "$15,000", "4.5", "Basketball, Music", "Downtown", "5 miles", listOf(), "Private", "Co-ed"),
    Schools(3, "Oakridge Int.", "$22,000", "4.8", "Swimming, Coding", "Westside", "12 miles", listOf(), "Intl", "Co-ed"),
    Schools(4, "St. Mary's", "$8,000", "4.0", "Drama, Debate", "Eastside", "3 miles", listOf(), "Private", "Girls")
)

@Preview
@Composable
fun SchoolComparePreview() {
    SchoolCompareScreen()
}