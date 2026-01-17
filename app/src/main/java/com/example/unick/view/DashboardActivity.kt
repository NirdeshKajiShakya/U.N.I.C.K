package com.example.unick.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.SubcomposeAsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolViewModel
import com.example.unick.viewmodel.UserType

// BottomNavItem moved to UnifiedNavBar.kt


class DashboardActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: SchoolViewModel) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, viewModel = viewModel)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationHost(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, viewModel: SchoolViewModel) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val userType by viewModel.userType.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    UnifiedBottomNavigationBar(
        currentRoute = currentRoute,
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        },
        onProfileClick = {
            handleProfileClick(userType, context, navController)
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

private fun handleProfileClick(userType: UserType, context: Context, navController: androidx.navigation.NavController) {
    when (userType) {
        is UserType.Normal -> {
            navController.navigate(BottomNavItem.Profile.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
        is UserType.School -> {
            val intent = Intent(context, SchoolDetailActivity::class.java)
            context.startActivity(intent)
        }
        is UserType.Unknown -> {
            // Optional: Show a toast or do nothing while user type is being determined
        }
    }
}


@Composable
fun NavigationHost(navController: NavHostController, viewModel: SchoolViewModel) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            val schools by viewModel.schools.collectAsState()
            val verifiedSchools = schools.filter { it.verified }
            val isLoading by viewModel.isLoadingSchools.collectAsState()
            DashboardScreen(
                schools = verifiedSchools,
                isLoading = isLoading,
                onRefresh = { viewModel.fetchSchools() }
            )
        }
        composable(BottomNavItem.Search.route) {
            SearchScreen()
        }
        composable(BottomNavItem.AIChat.route) {
            AiChatScreen()
        }
        composable(BottomNavItem.Notification.route) {
            NotificationScreen()
        }
        composable(BottomNavItem.Profile.route) {
            UserProfileScreen(viewModel = null)
        }
    }
}


@Composable
fun DashboardScreen(
    schools: List<SchoolForm> = emptyList(),
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Namaste,",
                    fontSize = 16.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Student",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        CircleShape
                    )
                    .clickable {
                        val intent = Intent(context, DataFormAcitivity::class.java)
                        context.startActivity(intent)
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore the best colleges in Nepal tailored to your SEE/SLC results and interests.",
            fontSize = 15.sp,
            color = Color(0xFF64748B),
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isSearchActive) {
                Button(
                    onClick = { isSearchActive = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Search, "Search", modifier = Modifier.size(22.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Search Colleges", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(14.dp)),
                    placeholder = { Text("e.g. St. Xavier's, Budhanilkantha...", color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2563EB)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Filter Button
            OutlinedButton(
                onClick = { /* Handle filter */ },
                modifier = Modifier
                    .size(58.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = BorderStroke(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF2563EB)))
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🎯", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Compare Button
        OutlinedButton(
            onClick = { /* Handle compare */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF2563EB)))
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("📊", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Compare Academic Programs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Add School Button
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

        Spacer(modifier = Modifier.height(32.dp))

        // Schools Section
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2563EB))
            }
        } else if (schools.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🏫",
                        fontSize = 48.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No schools added yet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Be the first to add your school!",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        } else {
            SchoolSection(
                title = "Recently Added Schools",
                subtitle = "Newly registered educational institutions",
                schools = schools,
                context = context
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SchoolSection(title: String, subtitle: String, schools: List<SchoolForm>, context: Context) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(schools) { school ->
                SchoolCard(school = school, context = context)
            }
        }
    }
}

@Composable
fun SchoolCard(school: SchoolForm, context: Context) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(260.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable {
                val intent = Intent(context, DashboardCard::class.java)
                intent.putExtra("school_details", school)
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
            ) {
                if (!school.imageUrl.isNullOrBlank()) {
                    SubcomposeAsyncImage(
                        model = school.imageUrl,
                        contentDescription = school.schoolName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = Color(0xFF2563EB))
                            }
                        },
                        error = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.BrokenImage,
                                    contentDescription = "Image failed to load",
                                    tint = Color.Gray
                                )
                            }
                        }
                    )
                } else {
                    // Fallback gradient if no image
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF2563EB).copy(alpha = 0.3f),
                                        Color(0xFF2563EB).copy(alpha = 0.1f)
                                    )
                                )
                            )
                    )
                }

                // Rating badge (5 stars for all)
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color(0xFF2563EB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "⭐ 5.0",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = school.schoolName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp,
                        maxLines = 2
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${school.location} • ${school.curriculum}",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp,
                        maxLines = 1
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (school.totalStudents.isNotEmpty()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👥", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${school.totalStudents} students",
                                fontSize = 13.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (school.scholarshipAvailable) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🎓", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Scholarship",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (school.transportFacility) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🚌", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Transport",
                                    fontSize = 12.sp,
                                    color = Color(0xFF2563EB),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// Placeholder screens
@Composable
fun SearchScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "This is Search Screen")
    }
}

@Composable
fun NotificationScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "This is Notification Screen")
    }
}


@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    UNICKTheme {
        val schools = listOf(
            SchoolForm(
                uid = "1",
                schoolName = "St. Xavier's College",
                location = "Maitighar, Kathmandu",
                curriculum = "A-Levels, National",
                totalStudents = "1500",
                scholarshipAvailable = true,
                transportFacility = true,
                imageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?w=400"
            )
        )
        DashboardScreen(schools = schools, isLoading = false)
    }
}
