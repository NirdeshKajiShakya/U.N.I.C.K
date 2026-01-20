package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolViewModel
import com.google.firebase.auth.FirebaseAuth

class SchoolDashboard : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        if (uid != null) {
             viewModel.fetchSchools() 
        }

        setContent {
            UNICKTheme {
                SchoolDashboardScreen(viewModel = viewModel, currentUid = uid)
            }
        }
    }
}

@Composable
fun SchoolDashboardScreen(viewModel: SchoolViewModel, currentUid: String?) {
    val schools by viewModel.schools.collectAsState()
    val isLoading by viewModel.isLoadingSchools.collectAsState()
    
    // Capture LocalContext here in the Composable scope
    val context = LocalContext.current
    
    val mySchool = remember(schools, currentUid) {
        schools.find { it.uid == currentUid }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF2563EB))
        }
    } else {
        if (mySchool != null) {
            // New School Dashboard UI
            Scaffold(
                bottomBar = {
                    UnifiedBottomNavigationBar(
                        currentRoute = BottomNavItem.Home.route,
                        onNavigate = { route ->
                             // Handle Navigation
                             when(route) {
                                 BottomNavItem.Home.route -> {
                                     // Already on Home, maybe scroll to top?
                                 }
                                 BottomNavItem.Profile.route -> {
                                     // Navigate to Profile
                                 }
                                 // Add other routes as needed
                             }
                        },
                        onProfileClick = {
                            // Navigate to Profile
                        }
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8FAFC))
                        .padding(innerPadding) // Respect Scaffolding padding (top/bottom bars)
                        .padding(24.dp) // Keep original padding
                        // If vertical scroll is needed, add it here. The original didn't have verticalScroll on this specific Column, 
                        // but it might be implicitly needed if content overflows. 
                        // Looking at original code, it didn't have verticalScroll on the school dashboard part, 
                        // but it's good practice. I'll stick to original structure unless user asked for scroll.
                        // Actually, I'll add verticalScroll because dashboard content typically grows.
                        .verticalScroll(rememberScrollState()) 
                ) {
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Welcome Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFFE2E8F0), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🏫", fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "Welcome Back,",
                                fontSize = 14.sp,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = mySchool.schoolName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                maxLines = 1
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        "Your Profile Preview",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Add School Button (Moved from DashboardActivity)
                    Button(
                        onClick = {
                            val intent = Intent(context, DataFormAcitivity::class.java)
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(58.dp)
                            .shadow(8.dp, RoundedCornerShape(14.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF10B981)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Add, "Add School", modifier = Modifier.size(22.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text("Add Your School", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Shared School Card
                    SchoolCard(
                        school = mySchool,
                        context = context,
                        showStatus = true, // Enable Status Indicator
                        onClick = {
                            val intent = Intent(context, DashboardCard::class.java)
                            intent.putExtra("school_details", mySchool)
                            intent.putExtra("is_school_view", true) // Flag for navigation
                            context.startActivity(intent)
                        }
                    )
                    
                    // Add extra spacer at bottom to avoid content being too close to navbar if scrolled
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        } else {
            // Dashboard UI for School Admin when no details are submitted yet
            SchoolAdminHome(currentUid = currentUid ?: "")
        }
    }
}

@Composable
fun SchoolAdminHome(currentUid: String) {
    val context = LocalContext.current
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(Color(0xFFE2E8F0), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🏫", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Welcome back,",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
                Text(
                    text = "School Admin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Profile Status", fontSize = 14.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFEF4444), // Red
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Not Submitted",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Your school is not listed on UNICK yet. Complete your profile to start accepting applications.",
                    fontSize = 14.sp,
                    color = Color(0xFF475569),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { 
                        context.startActivity(Intent(context, DataFormAcitivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Complete Profile")
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Quick Actions Grid Title
        Text(
            "Quick Actions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            // Edit Profile (Disabled/Greyed out)
            ActionCard(
                icon = "📝", 
                title = "Edit Profile", 
                modifier = Modifier.weight(1f)
            ) {
                 // Open Edit Profile (if exists) or same form
                 context.startActivity(Intent(context, DataFormAcitivity::class.java))
            }
            // View Stats
            ActionCard(
                icon = "📊", 
                title = "View Stats", 
                modifier = Modifier.weight(1f)
            ) {
                // Placeholder
            }
        }
    }
}

@Composable
fun ActionCard(icon: String, title: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(100.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(icon, fontSize = 28.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF334155))
        }
    }
}
