package com.example.unick.view

import android.content.Context
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.IntentCompat
import coil.compose.AsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.view.ui.theme.UNICKTheme

class DashboardCard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val school = IntentCompat.getParcelableExtra(intent, "school_details", SchoolForm::class.java)
        val isSchoolView = intent.getBooleanExtra("is_school_view", false)
        
        setContent {
            UNICKTheme {
                if (school != null) {
                    SchoolDetailsScreen(
                        school = school,
                        isSchoolView = isSchoolView,
                        onBack = { finish() }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailsScreen(school: SchoolForm, isSchoolView: Boolean, onBack: () -> Unit) {
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = BottomNavItem.Home.route,
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.Home.route -> {
                             // Navigate back to dashboard home
                             (context as? ComponentActivity)?.finish()
                        }
                        BottomNavItem.AIChat.route -> {
                             val intent = Intent(context, DashboardActivity::class.java)
                             intent.putExtra("start_destination", BottomNavItem.AIChat.route)
                             context.startActivity(intent)
                             (context as? ComponentActivity)?.finish()
                        }
                         BottomNavItem.Profile.route -> {
                             val intent = Intent(context, UserProfileActivity::class.java)
                             context.startActivity(intent)
                        }
                        else -> {
                             val intent = Intent(context, DashboardActivity::class.java)
                             intent.putExtra("start_destination", route)
                             context.startActivity(intent)
                             (context as? ComponentActivity)?.finish()
                        }
                    }
                },
                onProfileClick = {
                     val intent = Intent(context, UserProfileActivity::class.java)
                     context.startActivity(intent)
                },
                navItems = listOf(
                    BottomNavItem.Home,
                    BottomNavItem.Search,
                    BottomNavItem.AIChat,
                    BottomNavItem.Notification,
                    BottomNavItem.Profile
                )
            )
        }
    ) { innerPadding ->
        // We use a Box with fillMaxSize to contain the LazyColumn
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()) // Only pad bottom to allow top image overlap
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
            ) {
            // ---- Header Image ----
            item {
                Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
                    AsyncImage(
                        model = school.imageUrl,
                        contentDescription = "School Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Gradient overlay could be added here for text readability if title was here
                }
            }

            // ---- Title & Basic Info ----
            item {
                Column(
                    modifier = Modifier
                        .offset(y = (-30).dp) // Overlap effect
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                        .background(Color.White)
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = school.schoolName,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B),
                                lineHeight = 32.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.LocationOn, null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = school.location,
                                    fontSize = 14.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        if (school.verified) {
                            Icon(
                                Icons.Outlined.Verified,
                                contentDescription = "Verified",
                                tint = Color(0xFF2563EB),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }


                    
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isSchoolView) {
                        // Compare Button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Compare Button
                            OutlinedButton(
                                onClick = {
                                    val intent = Intent(context, CompareActivity::class.java)
                                    intent.putExtra("school_details", school)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Outlined.CompareArrows, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Compare", maxLines = 1)
                            }

                            // Apply Now Button
                            Button(
                                onClick = {
                                    val intent = Intent(context, StudentApplicationActivity::class.java)
                                    intent.putExtra("schoolId", school.uid)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Apply Now", maxLines = 1)
                            }
                        }
                    }
                    
                    // Quick Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickStat(
                            icon = Icons.Outlined.School, 
                            label = "Curriculum", 
                            value = school.curriculum, 
                            modifier = Modifier.weight(1f)
                        )
                        QuickStat(
                            icon = Icons.Outlined.Groups, 
                            label = "Students", 
                            value = school.totalStudents,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStat(
                            icon = Icons.Outlined.CalendarToday, 
                            label = "Est.", 
                            value = school.establishedYear,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // ---- About Section ----
            item {
                DetailSectionHeader("About School")
                Text(
                    text = school.description.ifBlank { "No description available." },
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                    color = Color(0xFF475569),
                    fontSize = 15.sp,
                    lineHeight = 24.sp
                )
            }

            // ---- Details Cards ----
            item {
                Spacer(modifier = Modifier.height(16.dp))
                DetailSectionHeader("Academic Info")
                Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                    DetailRow(Icons.Outlined.Book, "Programs", school.programsOffered)
                    DetailRow(Icons.Outlined.SportsSoccer, "Facilities", school.facilities)
                    DetailRow(Icons.Outlined.LocalActivity, "Activities", school.extracurricular)
                    DetailRow(Icons.Outlined.Payments, "Tuition Fee", school.tuitionFee)
                }
            }
            
            item {
                 Spacer(modifier = Modifier.height(16.dp))
                 DetailSectionHeader("Contact & Access")
                 Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                     DetailRow(Icons.Outlined.Person, "Principal", school.principalName)
                     DetailRow(Icons.Outlined.Email, "Email", school.email, isLink = true, context = context)
                     DetailRow(Icons.Outlined.Phone, "Phone", school.contactNumber, isLink = true, context = context)
                     DetailRow(Icons.Outlined.Language, "Website", school.website, isLink = true, context = context)
                 }
                 Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
}

@Composable
fun QuickStat(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .background(Color(0xFFF1F5F9), RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp) // Reduced horizontal padding slightly 
    ) {
        Icon(icon, null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value, 
            fontWeight = FontWeight.Bold, 
            fontSize = 13.sp, // Slightly smaller to fit
            color = Color(0xFF1E293B), 
            maxLines = 2, // Allow 2 lines
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 16.sp
        )
        Text(label, fontSize = 11.sp, color = Color(0xFF64748B))
    }
}

@Composable
fun DetailSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B),
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun DetailRow(icon: ImageVector, label: String, value: String, isLink: Boolean = false, context: Context? = null) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(enabled = isLink) {
                    if (isLink && context != null) {
                        try {
                            val intent = when (label) {
                                "Email" -> Intent(Intent.ACTION_SENDTO).apply { data = Uri.parse("mailto:$value") }
                                "Phone" -> Intent(Intent.ACTION_DIAL).apply { data = Uri.parse("tel:$value") }
                                "Website" -> {
                                    var url = value
                                    if (!url.startsWith("http")) url = "https://$url"
                                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                }
                                else -> null
                            }
                            intent?.let { context.startActivity(it) }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White, RoundedCornerShape(10.dp))
                    .shadow(1.dp, RoundedCornerShape(10.dp)), // Subtle shadow
                contentAlignment = Alignment.Center
            ) {
                 Icon(icon, null, tint = if(isLink) Color(0xFF2563EB) else Color(0xFF64748B), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8))
                Text(
                    value, 
                    fontSize = 15.sp, 
                    color = if(isLink) Color(0xFF2563EB) else Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
