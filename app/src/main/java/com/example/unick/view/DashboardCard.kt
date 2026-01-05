package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import coil.compose.SubcomposeAsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.ui.theme.UNICKTheme

class DashboardCard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val school = IntentCompat.getParcelableExtra(intent, "school_details", SchoolForm::class.java)
        setContent {
            UNICKTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if (school != null) {
                        SchoolDetails(
                            school = school,
                            onBack = { finish() },
                            modifier = Modifier.padding(innerPadding)
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Could not load school details.")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetails(school: SchoolForm, onBack: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
    ) {
        TopAppBar(
            title = { Text(school.schoolName, fontWeight = FontWeight.Bold) },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                }
            }
        )

        if (!school.imageUrl.isNullOrBlank()) {
            SubcomposeAsyncImage(
                model = school.imageUrl,
                contentDescription = school.schoolName,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier.padding(16.dp)) {
            DetailItem("Location", school.location)
            DetailItem("Established Year", school.establishedYear)
            DetailItem("Principal", school.principalName)
            DetailItem("Contact", school.contactNumber)
            DetailItem("Email", school.email)
            DetailItem("Website", school.website)
            DetailItem("Curriculum", school.curriculum)
            DetailItem("Programs Offered", school.programsOffered)
            DetailItem("Facilities", school.facilities)
            DetailItem("Total Students", school.totalStudents)
            DetailItem("Tuition Fee", school.tuitionFee)
            DetailItem("Admission Fee", school.admissionFee)
            DetailItem("Extracurricular Activities", school.extracurricular)
            DetailItem("Scholarship", if(school.scholarshipAvailable) "Yes" else "No")
            DetailItem("Transport", if(school.transportFacility) "Yes" else "No")
            DetailItem("Hostel", if(school.hostelFacility) "Yes" else "No")
            DetailItem("Description", school.description)
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    if (value.isNotBlank()) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
            Text(
                text = label,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.DarkGray
            )
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
