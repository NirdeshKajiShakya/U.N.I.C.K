package com.example.unick.view

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.view.ui.theme.UNICKTheme

import androidx.activity.viewModels

class AdminCardsForm : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val schoolForm = intent.getParcelableExtra<SchoolForm>("school_data")

        val viewModel: com.example.unick.viewmodel.SchoolViewModel by viewModels()

        setContent {
            UNICKTheme {
                if (schoolForm != null) {
                    AdminSchoolDetailScreen(
                        school = schoolForm,
                        onBack = { finish() },
                        onConfirmVerify = {
                            viewModel.verifySchool(schoolForm.uid) { success ->
                                if (success) {
                                    android.widget.Toast.makeText(this, "School Verified!", android.widget.Toast.LENGTH_SHORT).show()
                                    finish() // Close activity on success
                                } else {
                                    android.widget.Toast.makeText(this, "Verification Failed", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onReject = {
                            viewModel.rejectSchool(schoolForm.uid) { success ->
                                if (success) {
                                    android.widget.Toast.makeText(this, "School Rejected", android.widget.Toast.LENGTH_SHORT).show()
                                    finish()
                                } else {
                                    android.widget.Toast.makeText(this, "Rejection Failed", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error loading school details")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSchoolDetailScreen(
    school: SchoolForm,
    onBack: () -> Unit,
    onConfirmVerify: () -> Unit,
    onReject: () -> Unit
) {
    var selectedTab by remember { mutableStateOf("Overview") }
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = { Text("School Verification", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reject Button
                Button(
                    onClick = onReject,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFEF4444) // Red for reject
                    )
                ) {
                    Text("Reject", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Confirm Verify Button
                Button(
                    onClick = onConfirmVerify,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF10B981) // Green for verify
                    )
                ) {
                    Text("Verify", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // ---- Banner ----
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = school.imageUrl,
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // ---- Header ----
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = school.schoolName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = school.location,
                        color = Color.DarkGray,
                        fontSize = 16.sp
                    )
                }
            }

            // ---- Tab Row ----
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AdminTabItem("Overview", selectedTab == "Overview") { selectedTab = "Overview" }
                    AdminTabItem("Academics", selectedTab == "Academics") { selectedTab = "Academics" }
                    AdminTabItem("Connect", selectedTab == "Connect") { selectedTab = "Connect" }
                }
                Divider()
            }

            // ---- Tab Content ----
            when (selectedTab) {
                "Overview" -> {
                    item {
                        OverviewCard(title = "About", text = school.description)
                    }
                    item {
                        OverviewCard(title = "Programs", text = school.programsOffered)
                    }
                    item {
                        OverviewCard(title = "Facilities", text = school.facilities)
                    }
                    item {
                        OverviewCard(
                            title = "Scholarship",
                            text = if (school.scholarshipAvailable) "Available" else "Not available"
                        )
                    }
                }

                "Academics" -> {
                    item { SectionTitle("Academics") }
                    item { InfoCard("Curriculum", school.curriculum) }
                    item { InfoCard("Programs", school.programsOffered) }
                    item { InfoCard("Total Students", school.totalStudents) }
                    item { InfoCard("Extracurricular", school.extracurricular) }
                    item { InfoCard("Transport", if (school.transportFacility) "Yes" else "No") }
                    item { InfoCard("Hostel", if (school.hostelFacility) "Yes" else "No") }
                    item { InfoCard("Tuition Fee", school.tuitionFee) }
                    item { InfoCard("Admission Fee", school.admissionFee) }
                    item { InfoCard("Established", school.establishedYear) }
                    item { InfoCard("Principal", school.principalName) }
                }

                "Connect" -> {
                    item { SectionTitle("Contact & Location") }
                    item {
                        ContactRow("Email", school.email) {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:${school.email}")
                            }
                            context.startActivity(intent)
                        }
                    }
                    item {
                        ContactRow("Phone", school.contactNumber) {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${school.contactNumber}")
                            }
                            context.startActivity(intent)
                        }
                    }
                    item {
                        ContactRow("Website", school.website) {
                            var url = school.website
                            if (!url.startsWith("http") && url.isNotEmpty()) url = "https://$url"
                            if (url.isNotEmpty()) {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            }
                        }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun AdminTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(10.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        if (selected) {
            Box(
                Modifier
                    .width(44.dp)
                    .height(3.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun OverviewCard(title: String, text: String) {
    if (text.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text(text, color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    if (value.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 7.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(label, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(value, color = Color.DarkGray)
            }
        }
    }
}

@Composable
private fun ContactRow(label: String, value: String, onClick: () -> Unit) {
    if (value.isNotBlank()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 7.dp)
                .clickable { onClick() },
            shape = RoundedCornerShape(18.dp),
             colors = CardDefaults.cardColors(containerColor = Color.White),
             elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(label, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(value, color = Color.DarkGray)
                }
                Text("↗", fontSize = 18.sp, color = Color.Gray)
            }
        }
    }
}