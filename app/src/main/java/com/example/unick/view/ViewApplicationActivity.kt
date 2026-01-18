package com.example.unick.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.unick.model.StudentApplication
import com.example.unick.repo.ViewApplicationRepoImpl
import com.example.unick.view.ui.theme.UNICKTheme
import com.example.unick.viewmodel.ApplicationDetailState
import com.example.unick.viewmodel.ApplicationsListState
import com.example.unick.viewmodel.StatusUpdateState
import com.example.unick.viewmodel.ViewApplicationViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class ViewApplicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get schoolId from intent or use current user's UID as school identifier
        val schoolId = intent.getStringExtra("schoolId")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: ""

        // Read optional application id (or legacy numeric index key) from intent
        val applicationId = intent.getStringExtra("applicationId")
            ?: intent.getStringExtra("application_index") // fallback for older intents

        // If we don't have a valid school id, avoid attempting to fetch and inform the user
        if (schoolId.isEmpty()) {
            Toast.makeText(this, "School ID not provided. Cannot load applications.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        // Debug: Show which schoolId is being used
        android.util.Log.d("ViewApplicationActivity", "Loading applications for schoolId: $schoolId")
        Toast.makeText(this, "Loading for school: ${schoolId.take(8)}...", Toast.LENGTH_SHORT).show()

        setContent {
            UNICKTheme {
                val repository = remember { ViewApplicationRepoImpl() }
                val viewModel: ViewApplicationViewModel = viewModel(
                    factory = ViewApplicationViewModel.Factory(repository)
                )

                ViewApplicationScreen(
                    viewModel = viewModel,
                    schoolId = schoolId,
                    applicationId = applicationId, // pass through
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewApplicationScreen(
    viewModel: ViewApplicationViewModel,
    schoolId: String,
    applicationId: String?, // added optional id param
    onBackPressed: () -> Unit
) {
    val applicationsState by viewModel.applicationsState.collectAsState()
    val selectedApplication by viewModel.selectedApplication.collectAsState()
    val statusUpdateState by viewModel.statusUpdateState.collectAsState()
    val context = LocalContext.current

    // Load applications when screen opens
    LaunchedEffect(schoolId) {
        if (schoolId.isNotEmpty()) {
            viewModel.loadApplicationsForSchool(schoolId)
        }
    }

    // Auto-select application if an applicationId was supplied via intent
    LaunchedEffect(applicationsState, applicationId) {
        if (!applicationId.isNullOrBlank() && selectedApplication == null) {
            if (applicationsState is ApplicationsListState.Success) {
                val apps = (applicationsState as ApplicationsListState.Success).applications
                val match = apps.find { it.applicationId == applicationId }
                if (match != null) {
                    viewModel.selectApplication(match)
                } else {
                    Toast.makeText(context, "Application not found for id: $applicationId", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Handle status update feedback
    LaunchedEffect(statusUpdateState) {
        when (statusUpdateState) {
            is StatusUpdateState.Success -> {
                Toast.makeText(context, "Application updated successfully!", Toast.LENGTH_SHORT).show()
                viewModel.resetStatusUpdateState()
            }
            is StatusUpdateState.Error -> {
                Toast.makeText(context, (statusUpdateState as StatusUpdateState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.resetStatusUpdateState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (selectedApplication != null) "Application Details"
                        else "Student Applications"
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (selectedApplication != null) {
                            viewModel.clearSelection()
                        } else {
                            onBackPressed()
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedApplication == null) {
                        IconButton(onClick = { viewModel.loadApplicationsForSchool(schoolId) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (selectedApplication != null) {
                // Show application detail view
                ApplicationDetailView(
                    application = selectedApplication!!,
                    viewModel = viewModel,
                    schoolId = schoolId,
                    isUpdating = statusUpdateState is StatusUpdateState.Loading
                )
            } else {
                // Show applications list
                when (applicationsState) {
                    is ApplicationsListState.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    is ApplicationsListState.Success -> {
                        val applications = (applicationsState as ApplicationsListState.Success).applications

                        // DEBUG: Always show test data for now - REMOVE AFTER TESTING
                        val showTestData = true // Set to false to use real Firebase data

                        if (showTestData || applications.isEmpty()) {
                            // TODO: Remove this test data after testing - use real data from Firebase
                            val testApplications = listOf(
                                StudentApplication(
                                    applicationId = "test_app_1",
                                    schoolId = schoolId,
                                    studentId = "student_123",
                                    fullName = "Aarav Sharma",
                                    dob = "2012-05-15",
                                    gender = "Male",
                                    bloodGroup = "O+",
                                    interests = "Football, Music, Science",
                                    lastSchoolName = "Sunrise Academy",
                                    standard = "Grade 6",
                                    fatherName = "Rajesh Sharma",
                                    fatherPhone = "9841234567",
                                    motherName = "Sunita Sharma",
                                    motherPhone = "9851234567",
                                    presentAddress = "Kathmandu, Nepal",
                                    permanentAddress = "Lalitpur, Nepal",
                                    schoolBudget = "50000",
                                    status = "pending",
                                    timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
                                ),
                                StudentApplication(
                                    applicationId = "test_app_2",
                                    schoolId = schoolId,
                                    studentId = "student_456",
                                    fullName = "Priya Thapa",
                                    dob = "2011-08-22",
                                    gender = "Female",
                                    bloodGroup = "A+",
                                    interests = "Reading, Dance, Art",
                                    lastSchoolName = "Little Angels School",
                                    standard = "Grade 7",
                                    fatherName = "Bikram Thapa",
                                    fatherPhone = "9801234567",
                                    motherName = "Anita Thapa",
                                    motherPhone = "9811234567",
                                    presentAddress = "Bhaktapur, Nepal",
                                    permanentAddress = "Bhaktapur, Nepal",
                                    schoolBudget = "60000",
                                    status = "pending",
                                    timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
                                ),
                                StudentApplication(
                                    applicationId = "test_app_3",
                                    schoolId = schoolId,
                                    studentId = "student_789",
                                    fullName = "Rohan Gurung",
                                    dob = "2013-01-10",
                                    gender = "Male",
                                    bloodGroup = "B+",
                                    interests = "Cricket, Chess",
                                    lastSchoolName = "Everest Public School",
                                    standard = "Grade 5",
                                    fatherName = "Deepak Gurung",
                                    fatherPhone = "9821234567",
                                    motherName = "Maya Gurung",
                                    motherPhone = "9831234567",
                                    presentAddress = "Pokhara, Nepal",
                                    permanentAddress = "Pokhara, Nepal",
                                    schoolBudget = "45000",
                                    status = "accepted",
                                    reviewedBy = "school_admin",
                                    reviewedAt = System.currentTimeMillis() - 43200000, // 12 hours ago
                                    timestamp = System.currentTimeMillis() - 259200000 // 3 days ago
                                )
                            )
                            ApplicationsList(
                                applications = testApplications,
                                onApplicationClick = { viewModel.selectApplication(it) }
                            )
                        } else {
                            ApplicationsList(
                                applications = applications,
                                onApplicationClick = { viewModel.selectApplication(it) }
                            )
                        }
                    }
                    is ApplicationsListState.Error -> {
                        ErrorView(
                            message = (applicationsState as ApplicationsListState.Error).message,
                            onRetry = { viewModel.loadApplicationsForSchool(schoolId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ApplicationsList(
    applications: List<StudentApplication>,
    onApplicationClick: (StudentApplication) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(applications) { application ->
            ApplicationListItem(
                application = application,
                onClick = { onApplicationClick(application) }
            )
        }
    }
}

@Composable
fun ApplicationListItem(
    application: StudentApplication,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Student avatar/icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Student info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = application.fullName.ifEmpty { "Unknown Student" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Applied: ${formatDate(application.timestamp)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "Grade: ${application.standard.ifEmpty { "N/A" }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Status chip
            StatusChip(status = application.status)
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val (backgroundColor, textColor, displayText) = when (status.lowercase()) {
        "accepted" -> Triple(Color(0xFF4CAF50), Color.White, "Accepted")
        "rejected" -> Triple(Color(0xFFF44336), Color.White, "Rejected")
        else -> Triple(Color(0xFFFFC107), Color.Black, "Pending")
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor
    ) {
        Text(
            text = displayText,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = textColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun ApplicationDetailView(
    application: StudentApplication,
    viewModel: ViewApplicationViewModel,
    schoolId: String,
    isUpdating: Boolean
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showRejectDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = when (application.status.lowercase()) {
                    "accepted" -> Color(0xFFE8F5E9)
                    "rejected" -> Color(0xFFFFEBEE)
                    else -> Color(0xFFFFF8E1)
                }
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Application Status",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = application.status.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                StatusChip(status = application.status)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Information Section
        SectionCard(title = "Student Information") {
            DetailRow("Full Name", application.fullName)
            DetailRow("Date of Birth", application.dob)
            DetailRow("Gender", application.gender)
            DetailRow("Blood Group", application.bloodGroup)
            DetailRow("Interests", application.interests)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Academic Information Section
        SectionCard(title = "Academic Information") {
            DetailRow("Previous School", application.lastSchoolName)
            DetailRow("Grade/Standard", application.standard)
            DetailRow("School Budget", application.schoolBudget)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Parent Information Section
        SectionCard(title = "Parent/Guardian Information") {
            DetailRow("Father's Name", application.fatherName)
            DetailRow("Father's Phone", application.fatherPhone)
            DetailRow("Mother's Name", application.motherName)
            DetailRow("Mother's Phone", application.motherPhone)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Address Section
        SectionCard(title = "Address") {
            DetailRow("Present Address", application.presentAddress)
            DetailRow("Permanent Address", application.permanentAddress)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Application Meta Section
        SectionCard(title = "Application Details") {
            DetailRow("Application ID", application.applicationId.take(8) + "...")
            DetailRow("Submitted On", formatDate(application.timestamp))
            if (application.reviewedAt > 0) {
                DetailRow("Reviewed On", formatDate(application.reviewedAt))
                DetailRow("Reviewed By", application.reviewedBy.ifEmpty { "N/A" })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons (only show if status is pending)
        if (application.status.lowercase() == "pending") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reject Button
                OutlinedButton(
                    onClick = { showRejectDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFF44336)
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Reject")
                    }
                }

                // Accept Button
                Button(
                    onClick = { showAcceptDialog = true },
                    modifier = Modifier.weight(1f),
                    enabled = !isUpdating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    )
                ) {
                    if (isUpdating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Accept")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Accept Confirmation Dialog
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Accept Application") },
            text = { Text("Are you sure you want to accept ${application.fullName}'s application?") },
            confirmButton = {
                Button(
                    onClick = {
                        showAcceptDialog = false
                        viewModel.acceptApplication(application.applicationId, currentUserId, schoolId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAcceptDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Reject Confirmation Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Application") },
            text = { Text("Are you sure you want to reject ${application.fullName}'s application?") },
            confirmButton = {
                Button(
                    onClick = {
                        showRejectDialog = false
                        viewModel.rejectApplication(application.applicationId, currentUserId, schoolId)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))
                ) {
                    Text("Reject")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value.ifEmpty { "N/A" },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun EmptyApplicationsView(schoolId: String = "") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Applications Yet",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Applications from students will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (schoolId.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "School ID: ${schoolId.take(12)}...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
fun ErrorView(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

/**
 * Format timestamp to readable date string.
 */
fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown date"
    }
}