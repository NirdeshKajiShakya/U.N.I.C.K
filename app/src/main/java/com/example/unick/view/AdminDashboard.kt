package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.activity.viewModels
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import coil.compose.AsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.viewmodel.SchoolViewModel

// -------------------- COLORS --------------------
private val PrimaryBlue = Color(0xFF4A90E2)
private val PrimaryBlueDark = Color(0xFF357ABD)
private val BackgroundGray = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val AccentGreen = Color(0xFF10B981)
private val AccentOrange = Color(0xFFF59E0B)
private val ChipBackground = Color(0xFFEEF2FF)
private val ChipText = Color(0xFF4338CA)

// -------------------- DATA MODEL --------------------
// Replaced local SchoolSubmission with shared SchoolForm model

// -------------------- ACTIVITY ------------------------

class AdminDashboardActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            // Observe the list of schools from the ViewModel
            val schools by viewModel.schools.collectAsState()
            
            // Trigger fetch if empty (optional, init block in VM handles it usually)
            LaunchedEffect(Unit) {
                if (schools.isEmpty()) {
                    viewModel.fetchSchools()
                }
            }

            AdminDashboardScreen(schoolSubmissions = schools)
        }
    }
}

@Composable
fun AdminDashboardScreen(schoolSubmissions: List<SchoolForm>) {
    val context = LocalContext.current
    val navController = androidx.navigation.compose.rememberNavController()

    androidx.compose.material3.Scaffold(
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = BottomNavItem.Home.route,
                onNavigate = { route ->
                    if (route == BottomNavItem.Home.route) {
                         // Already on home
                    } else if (route == "notification") {
                         // Handle notification click if needed
                         android.widget.Toast.makeText(context, "Notifications", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                onProfileClick = {
                    val intent = Intent(context, AdminProfileActivity::class.java)
                    context.startActivity(intent)
                },
                navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Notification,
                    BottomNavItem.Profile
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
    
                // ---------------- DASHBOARD HEADER WITH GRADIENT ----------------
                item {
                    DashboardHeaderSection()
                }
    
                item { Spacer(modifier = Modifier.height(24.dp)) }
    
                // ---------------- PENDING VERIFICATIONS SECTION ----------------
                item {
                    PendingVerificationsHeader(count = schoolSubmissions.size)
                }
    
                item { Spacer(modifier = Modifier.height(16.dp)) }
    
                // ---------------- SCHOOL SUBMISSION CARDS ----------------
                items(schoolSubmissions) { submission ->
                    SchoolSubmissionCard(
                        submission = submission,
                        onVerifyClick = {
                            val intent = Intent(context, AdminCardsForm::class.java)
                            intent.putExtra("school_data", submission)
                            context.startActivity(intent)
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

// -------------------- DASHBOARD HEADER --------------------
@Composable
fun DashboardHeaderSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryBlueDark)
                )
            )
            .padding(vertical = 40.dp, horizontal = 24.dp)
    ) {
        Column {
            Text(
                "Admin Dashboard",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Review and verify school submissions",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

// -------------------- PENDING VERIFICATIONS HEADER --------------------
@Composable
fun PendingVerificationsHeader(count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Pending,
            contentDescription = null,
            tint = AccentOrange,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "Pending Verifications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .background(AccentOrange.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                count.toString(),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AccentOrange
            )
        }
    }
}

// -------------------- SCHOOL SUBMISSION CARD --------------------
@Composable
fun SchoolSubmissionCard(
    submission: SchoolForm,
    onVerifyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // School Image (if available)
            if (!submission.imageUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .padding(bottom = 16.dp)
                ) {
                    AsyncImage(
                        model = submission.imageUrl,
                        contentDescription = "School Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // School Name
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        submission.schoolName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .background(AccentOrange.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        "Pending", // Status placeholder
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AccentOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            SchoolDetailRow(
                icon = Icons.Outlined.LocationOn,
                label = "Location",
                value = submission.location
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Submitted Date
            SchoolDetailRow(
                icon = Icons.Outlined.Schedule,
                label = "Submitted",
                value = "Recently" // Placeholder as date isn't in SchoolForm yet
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Verify Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueDark)
                        ),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onVerifyClick() }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.VerifiedUser,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Verify School",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

// -------------------- SCHOOL DETAIL ROW --------------------
@Composable
fun SchoolDetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ChipBackground, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = ChipText,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                label,
                fontSize = 11.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}

// ---------------- PREVIEW ------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewAdminDashboardScreen() {
    val sampleData = listOf(
        SchoolForm(
            schoolName = "Sunrise International School",
            location = "Kathmandu, Nepal"
        ),
        SchoolForm(
            schoolName = "Mountain View Academy",
            location = "Pokhara, Nepal"
        ),
        SchoolForm(
            schoolName = "Valley High School",
            location = "Lalitpur, Nepal"
        )
    )
    AdminDashboardScreen(schoolSubmissions = sampleData)
}