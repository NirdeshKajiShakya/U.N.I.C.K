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
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val intent = activity?.intent
    val startDestination = intent?.getStringExtra("start_destination")

    LaunchedEffect(startDestination) {
        if (!startDestination.isNullOrEmpty()) {
            navController.navigate(startDestination) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }
    }

    val userTypeState = viewModel.userType.collectAsState()
    
    Scaffold(
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route,
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.Home.route -> {
                            // If already on home (or any other tab in dashboard), navigate to Home tab
                             navController.navigate(BottomNavItem.Home.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                             }
                        }
                        BottomNavItem.AIChat.route -> {
                            // Navigate to AI Chat tab
                            navController.navigate(BottomNavItem.AIChat.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                        BottomNavItem.Profile.route -> {
                            // Redirect to UserProfileActivity
                            handleProfileClick(userTypeState.value, context, navController)
                        }
                        else -> {
                            // Other tabs (Search, Notification) - navigate normally within NavHost
                             navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                             }
                        }
                    }
                },
                onProfileClick = {
                     handleProfileClick(userTypeState.value, context, navController)
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
        Box(modifier = Modifier.padding(innerPadding)) {
            val userName by viewModel.userName.collectAsState()
            NavigationHost(navController = navController, viewModel = viewModel, userName = userName)
        }
    }
}

private fun handleProfileClick(userType: UserType, context: Context, navController: androidx.navigation.NavController) {
    if (userType is UserType.School) {
        val intent = Intent(context, SchoolDashboard::class.java)
        context.startActivity(intent)
    } else {
        // Normal user -> UserProfileActivity
        val intent = Intent(context, UserProfileActivity::class.java)
        context.startActivity(intent)
    }
}


@Composable
fun NavigationHost(navController: NavHostController, viewModel: SchoolViewModel, userName: String) {
    NavHost(navController = navController, startDestination = BottomNavItem.Home.route) {
        composable(BottomNavItem.Home.route) {
            val schools by viewModel.schools.collectAsState()
            val verifiedSchools = schools.filter { it.verified }
            val isLoading by viewModel.isLoadingSchools.collectAsState()
            DashboardScreen(
                schools = verifiedSchools,
                isLoading = isLoading,
                onRefresh = { viewModel.fetchSchools() },
                onCompareClick = { navController.navigate("compare") },
                userName = userName
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
        composable("compare") {
            SchoolCompareScreen(onBackClick = { navController.popBackStack() })
        }
    }
}


@Composable
fun DashboardScreen(
    schools: List<SchoolForm> = emptyList(),
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {},
    onCompareClick: () -> Unit = {},
    userName: String = "Student"
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
                    text = if (userName.isNotBlank()) userName else "Student",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
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
            onClick = onCompareClick,
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
                SchoolCard(
                    school = school, 
                    context = context,
                    onClick = {
                        val intent = Intent(context, DashboardCard::class.java)
                        intent.putExtra("school_details", school)
                        context.startActivity(intent)
                    }
                )
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
