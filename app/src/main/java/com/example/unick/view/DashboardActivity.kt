package com.example.unick.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.unick.model.FilterResult
import com.example.unick.model.SchoolForm
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.utils.applyDistanceFilterIfNeeded
import com.example.unick.viewmodel.SchoolViewModel
import com.example.unick.viewmodel.UserType
import com.google.android.gms.location.LocationServices

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
        bottomBar = { BottomNavigationBar(navController = navController, viewModel = viewModel) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationHost(navController = navController, viewModel = viewModel)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: androidx.navigation.NavController, viewModel: SchoolViewModel) {
    val context = LocalContext.current
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
        onProfileClick = { handleProfileClick(userType, context, navController) },
        navItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Search,
            BottomNavItem.AIChat,
            BottomNavItem.Notification,
            BottomNavItem.Profile
        )
    )
}

private fun handleProfileClick(
    userType: UserType,
    context: Context,
    navController: androidx.navigation.NavController
) {
    when (userType) {
        is UserType.Normal -> {
            navController.navigate(BottomNavItem.Profile.route) {
                popUpTo(navController.graph.startDestinationId)
                launchSingleTop = true
            }
        }

        is UserType.School -> {
            val uid = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
            if (uid.isNullOrBlank()) return

            val intent = Intent(context, SchoolDetailActivity::class.java).apply {
                putExtra("uid", uid)
            }
            context.startActivity(intent)
        }

        is UserType.Unknown -> Unit
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

        // ✅ Real search + filter
        composable(BottomNavItem.Search.route) {
            SearchScreen(viewModel = viewModel)
        }

        composable(BottomNavItem.AIChat.route) { AiChatScreen() }
        composable(BottomNavItem.Notification.route) { NotificationScreen() }
        composable(BottomNavItem.Profile.route) { UserProfileScreen(viewModel = null) }
    }
}

/* ----------------------------- HOME DASHBOARD ----------------------------- */

@Composable
fun DashboardScreen(
    schools: List<SchoolForm> = emptyList(),
    isLoading: Boolean = false,
    onRefresh: () -> Unit = {}
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    // ✅ Active filter for HOME too (same as SearchScreen)
    var activeFilter by remember {
        mutableStateOf(
            FilterResult(
                feeRange = "Any",
                location = "Any",
                passRate = "Any",
                levels = emptyList(),
                curriculums = emptyList(),
                facilities = emptyList(),
                radiusKm = null
            )
        )
    }

    // ✅ User location (for distance filter)
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }

    LaunchedEffect(Unit) {
        requestOneTimeLocation(context) { lat, lng ->
            userLat = lat
            userLng = lng
        }
    }

    // ✅ Filter launcher (HOME filter button 🎯)
    val filterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val returned =
                res.data?.getParcelableExtra<FilterResult>(FilterActivity.EXTRA_FILTER_RESULT)
            if (returned != null) activeFilter = returned
        }
    }

    // ✅ Apply SAME filters as SearchScreen (name/location/fee/curriculum/facilities/levels)
    val baseFiltered = remember(searchText, activeFilter, schools) {
        schools
            .asSequence()
            .filter { it.schoolName.contains(searchText, ignoreCase = true) }
            .filter { activeFilter.location == "Any" || it.location.contains(activeFilter.location, ignoreCase = true) }
            .filter { activeFilter.feeRange == "Any" || feeMatches(activeFilter.feeRange, it.tuitionFee) }
            .filter { matchesAllSelected(activeFilter.curriculums, it.curriculum) }
            .filter { matchesAllSelected(activeFilter.facilities, it.facilities) }
            .filter { matchesAnyLevel(activeFilter.levels, it.programsOffered) }
            .toList()
    }

    // ✅ Distance filter last (only if radius + user location available)
    val finalFilteredSchools = remember(baseFiltered, activeFilter.radiusKm, userLat, userLng) {
        val lat = userLat
        val lng = userLng
        if (activeFilter.radiusKm == null || lat == null || lng == null) {
            baseFiltered
        } else {
            applyDistanceFilterIfNeeded(
                userLat = lat,
                userLng = lng,
                radiusKm = activeFilter.radiusKm,
                schools = baseFiltered,
                latOf = { it.latitude },
                lngOf = { it.longitude }
            )
        }
    }

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

        // Home Search UI (NOW FILTERS LIST)
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
                    trailingIcon = {
                        if (searchText.isNotBlank()) {
                            IconButton(onClick = { searchText = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // ✅ Filter button -> opens FilterActivity and updates activeFilter
            OutlinedButton(
                onClick = {
                    filterLauncher.launch(Intent(context, FilterActivity::class.java))
                },
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

        // Optional small filter summary (same style as SearchScreen)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text =
                "Filters: Location=${activeFilter.location}, Fee=${activeFilter.feeRange}, " +
                        "Curriculum=${activeFilter.curriculums.size}, Facilities=${activeFilter.facilities.size}, " +
                        "Radius=${activeFilter.radiusKm?.let { "${it}km" } ?: "Any"}",
            style = MaterialTheme.typography.bodySmall,
            color = Color(0xFF64748B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ✅ Compare Button -> CompareActivity
        OutlinedButton(
            onClick = {
                context.startActivity(Intent(context, CompareActivity::class.java))
            },
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
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
            ) { CircularProgressIndicator(color = Color(0xFF2563EB)) }
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
                    Text(text = "🏫", fontSize = 48.sp)
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
            // ✅ show filtered schools on HOME
            if (finalFilteredSchools.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(22.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No schools match your search/filters.", color = Color(0xFF64748B))
                }
            } else {
                SchoolSection(
                    title = "Recently Added Schools",
                    subtitle = "Newly registered educational institutions",
                    schools = finalFilteredSchools,
                    context = context
                )
            }
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

/* ----------------------------- SEARCH TAB ----------------------------- */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: SchoolViewModel) {
    val context = LocalContext.current

    // load data
    LaunchedEffect(Unit) { viewModel.fetchSchools() }

    val schools by viewModel.schools.collectAsState()
    val verifiedSchools = remember(schools) { schools.filter { it.verified } }

    var query by remember { mutableStateOf("") }

    // IMPORTANT: make sure your FilterResult has default values or create default like this:
    var activeFilter by remember {
        mutableStateOf(
            FilterResult(
                feeRange = "Any",
                location = "Any",
                passRate = "Any",
                levels = emptyList(),
                curriculums = emptyList(),
                facilities = emptyList(),
                radiusKm = null
            )
        )
    }
    var userLat by remember { mutableStateOf<Double?>(null) }
    var userLng by remember { mutableStateOf<Double?>(null) }

    // Get location once when screen opens
    LaunchedEffect(Unit) {
        requestOneTimeLocation(context) { lat, lng ->
            userLat = lat
            userLng = lng
        }
    }

    val filterLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { res ->
        if (res.resultCode == Activity.RESULT_OK) {
            val returned =
                res.data?.getParcelableExtra<FilterResult>(FilterActivity.EXTRA_FILTER_RESULT)
            if (returned != null) activeFilter = returned
        }
    }

    // ✅ Base filters (name/location/fee/curriculum/facilities/levels)
    val baseFiltered = remember(query, activeFilter, verifiedSchools) {
        verifiedSchools
            .asSequence()
            .filter { it.schoolName.contains(query, ignoreCase = true) }
            .filter { activeFilter.location == "Any" || it.location.contains(activeFilter.location, ignoreCase = true) }
            .filter { activeFilter.feeRange == "Any" || feeMatches(activeFilter.feeRange, it.tuitionFee) }
            .filter { matchesAllSelected(activeFilter.curriculums, it.curriculum) }
            .filter { matchesAllSelected(activeFilter.facilities, it.facilities) }
            .filter { matchesAnyLevel(activeFilter.levels, it.programsOffered) }
            .toList()
    }

    // ✅ STEP 4: DISTANCE FILTER (apply last)
    val finalFiltered = remember(baseFiltered, activeFilter.radiusKm, userLat, userLng) {
        val lat = userLat
        val lng = userLng

        // If radius is "Any", or user location not available -> don't filter by distance
        if (activeFilter.radiusKm == null || lat == null || lng == null) {
            baseFiltered
        } else {
            applyDistanceFilterIfNeeded(
                userLat = lat,
                userLng = lng,
                radiusKm = activeFilter.radiusKm,
                schools = baseFiltered,
                latOf = { it.latitude },
                lngOf = { it.longitude }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search Schools") },
                actions = {
                    if (query.isNotBlank()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { filterLauncher.launch(Intent(context, FilterActivity::class.java)) }
            ) { Text("🎯") }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                placeholder = { Text("Search by school name...") },
                singleLine = true
            )

            Spacer(Modifier.height(10.dp))

            // Optional: show radius too
            Text(
                text = "Filters: Location=${activeFilter.location}, Fee=${activeFilter.feeRange}, " +
                        "Curriculum=${activeFilter.curriculums.size}, Facilities=${activeFilter.facilities.size}, " +
                        "Radius=${activeFilter.radiusKm?.let { "${it}km" } ?: "Any"}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(12.dp))

            if (finalFiltered.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No schools match your search/filters.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 90.dp)
                ) {
                    items(finalFiltered) { school ->
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
    }
}

/* ----------------------------- FILTER HELPERS ----------------------------- */

private fun parseFeeToInt(fee: String?): Int? {
    if (fee.isNullOrBlank()) return null
    val digitsOnly = fee.filter { it.isDigit() }
    return digitsOnly.toIntOrNull()
}

private fun feeMatches(selectedRange: String, tuitionFee: String): Boolean {
    val fee = parseFeeToInt(tuitionFee) ?: return false

    return when (selectedRange) {
        "Under NPR 1 Lakh" -> fee < 100_000
        "NPR 1-3 Lakhs" -> fee in 100_000..300_000
        "NPR 3-5 Lakhs" -> fee in 300_000..500_000
        "NPR 5-10 Lakhs" -> fee in 500_000..1_000_000
        "Above NPR 10 Lakhs" -> fee > 1_000_000
        else -> true
    }
}

private fun tokenizeCommaString(raw: String): Set<String> {
    return raw
        .split(",", ";")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .map { it.lowercase() }
        .toSet()
}

/** curriculums/facilities: require ALL selected items to exist in the school's string list */
private fun matchesAllSelected(selected: List<String>, raw: String): Boolean {
    if (selected.isEmpty()) return true
    val tokens = tokenizeCommaString(raw)
    return selected.all { it.lowercase() in tokens }
}

/** levels: best effort match against programsOffered string */
private fun matchesAnyLevel(selectedLevels: List<String>, programsOffered: String): Boolean {
    if (selectedLevels.isEmpty()) return true
    val text = programsOffered.lowercase()
    return selectedLevels.any { lvl -> text.contains(lvl.lowercase()) }
}

@SuppressLint("MissingPermission")
private fun requestOneTimeLocation(
    context: android.content.Context,
    onResult: (Double?, Double?) -> Unit
) {
    val fused = LocationServices.getFusedLocationProviderClient(context)

    // permission check
    val fineGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    val coarseGranted = ActivityCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    if (!fineGranted && !coarseGranted) {
        onResult(null, null)
        return
    }

    fused.lastLocation.addOnSuccessListener { loc ->
        onResult(loc?.latitude, loc?.longitude)
    }.addOnFailureListener {
        onResult(null, null)
    }
}

/* ----------------------------- PLACEHOLDER ----------------------------- */

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
                curriculum = "A-Levels, National Curriculum",
                facilities = "Library, Transportation, Science Labs",
                programsOffered = "+2 Science, A-Levels",
                totalStudents = "1500",
                scholarshipAvailable = true,
                transportFacility = true,
                imageUrl = "https://images.unsplash.com/photo-1562774053-701939374585?w=400",
                verified = true,
                tuitionFee = "200000",
                admissionFee = "50000"
            )
        )
        DashboardScreen(schools = schools, isLoading = false)
    }
}
