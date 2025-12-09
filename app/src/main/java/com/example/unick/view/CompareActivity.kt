package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme

class SchoolCompareActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolCompareScreen()
        }
    }
}

@Composable
fun SchoolCompareScreen() {
    // State management
    var school1 by remember { mutableStateOf(School()) }
    var school2 by remember { mutableStateOf(School()) }
    var show1 by remember { mutableStateOf(false) }
    var show2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { ComparisonTopAppBar() }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { HeadingTextForCompare() }

            item { SearchSection(
                onSchool1Select = { school1 = it; show1 = true },
                onSchool2Select = { school2 = it; show2 = true }
            )}

            if (show1 || show2) {
                item { ComparisonHeader() }

                // Defined fields for structured comparison
                val comparisonFields = getComparisonFields()

                items(comparisonFields) { field ->
                    ComparisonRow(
                        label = field.label,
                        val1 = field.getter(school1),
                        val2 = field.getter(school2),
                        visible1 = show1,
                        visible2 = show2,
                        isHighlight = field.highlight
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
                item { ButtonForClearCompare(
                    onClear = {
                        show1 = false; show2 = false
                        school1 = School(); school2 = School()
                    }
                )}
            } else {
                item { EmptyStateCompare() }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComparisonTopAppBar() {
    TopAppBar(
        title = { Text("School Compare", fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    )
}

@Composable
fun HeadingTextForCompare() {
    Column(modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)) {
        Text("School Comparison", fontWeight = FontWeight.Bold, fontSize = 24.sp)
        Text("Compare key metrics between educational institutions.", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SearchSection(onSchool1Select: (School) -> Unit, onSchool2Select: (School) -> Unit) {
    val samples = getSampleSchools()
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text("School A", fontWeight = FontWeight.SemiBold)
            samples.forEach { school ->
                TextButton(onClick = { onSchool1Select(school) }) { Text(school.name, fontSize = 12.sp) }
            }
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("School B", fontWeight = FontWeight.SemiBold)
            samples.forEach { school ->
                TextButton(onClick = { onSchool2Select(school) }) { Text(school.name, fontSize = 12.sp) }
            }
        }
    }
}

@Composable
fun ComparisonHeader() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 20.dp).padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Attribute", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
        Text("School 1", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Text("School 2", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun ComparisonRow(label: String, val1: String, val2: String, visible1: Boolean, visible2: Boolean, isHighlight: Boolean) {
    val color = if (isHighlight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color.Transparent
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).padding(horizontal = 16.dp).background(color).padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 13.sp)
        Text(if(visible1) val1 else "-", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 13.sp)
        Text(if(visible2) val2 else "-", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 13.sp)
    }
}

@Composable
fun ButtonForClearCompare(onClear: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Button(onClick = onClear) {
            Text("Reset Comparison")
        }
    }
}

@Composable
fun EmptyStateCompare() {
    Box(modifier = Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
        Text("Select schools above to begin comparison", color = Color.Gray, textAlign = TextAlign.Center)
    }
}

// Data models and Helpers
data class School(val name: String = "", val fee: String = "", val rating: String = "", val ratio: String = "")

data class CompField(val label: String, val getter: (School) -> String, val highlight: Boolean = false)

fun getComparisonFields() = listOf(
    CompField("Name", { it.name }),
    CompField("Annual Fee", { it.fee }, true),
    CompField("Rating", { it.rating }, true),
    CompField("Teacher Ratio", { it.ratio })
)

fun getSampleSchools() = listOf(
    School("Springfield", "$0", "4.2", "18:1"),
    School("Oakwood", "$15k", "4.7", "12:1")
)

@Preview
@Composable
fun SchoolComparePreview() {
    SchoolCompareScreen()
}