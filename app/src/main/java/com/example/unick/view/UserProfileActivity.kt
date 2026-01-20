package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
 import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.unick.R
import com.example.unick.viewmodel.UserProfileState
import com.example.unick.viewmodel.UserProfileViewModel
import com.google.firebase.auth.FirebaseAuth

// -------------------- COLORS --------------------
private val PrimaryBlue = Color(0xFF4A90E2)
private val PrimaryBlueDark = Color(0xFF357ABD)
private val BackgroundGray = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val AccentGreen = Color(0xFF10B981)
private val AccentOrange = Color(0xFFF59E0B)
private val AccentRed = Color(0xFFEF4444)
private val ChipBackground = Color(0xFFEEF2FF)
private val ChipText = Color(0xFF4338CA)


// -------------------- DATA MODELS --------------------

data class ShortlistedSchoolForUserProfile(
    val id: String = "",
    val name: String = "",
    val location: String = ""
)

data class ApplicationItemForUserProfile(
    val id: String = "",
    val schoolName: String = "",
    val status: String = "",
    val applicationCode: String = ""
)

data class UserProfileModel(
    val fullName: String = "",
    val email: String = "",
    val location: String = "",
    val schoolId: String = "",
    val role: String = ""
)

// -------------------- ACTIVITY ------------------------

class UserProfileActivity : ComponentActivity() {
    private lateinit var viewModel: UserProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize Repo and ViewModel
        val repo = com.example.unick.repo.UserProfileRepoImpl()
        viewModel = UserProfileViewModel.Factory(repo)
            .create(UserProfileViewModel::class.java)

        // Load current user's data
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            viewModel.loadUserData(currentUserId)
        } else {
            // User not logged in, redirect to login or show error
            finish()
            return
        }

        setContent {
            UserProfileScreen(viewModel = viewModel)
        }
    }
}

@Composable
fun UserProfileScreen(viewModel: UserProfileViewModel?) {
    val userProfileState by viewModel?.userProfile?.collectAsState() ?: remember { mutableStateOf(UserProfileState.Idle) }
    val shortlistedSchoolsState by viewModel?.shortlistedSchools?.collectAsState() ?: remember { mutableStateOf(UserProfileState.Idle) }
    val applicationsState by viewModel?.applications?.collectAsState() ?: remember { mutableStateOf(UserProfileState.Idle) }
    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = BottomNavItem.Profile.route,
                onNavigate = { route ->
                    // Navigate back to DashboardActivity
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    context.startActivity(intent)
                    (context as? ComponentActivity)?.finish()
                },
                onProfileClick = {
                    // Already on profile screen, do nothing
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ---------------- PROFILE HEADER WITH GRADIENT ----------------
            item {
                when (userProfileState) {
                    is UserProfileState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = PrimaryBlue)
                        }
                    }
                    is UserProfileState.Error -> Text(
                        "Error: ${(userProfileState as UserProfileState.Error).message}",
                        Modifier.padding(20.dp),
                        color = AccentRed
                    )
                    is UserProfileState.Success -> {
                        val user = (userProfileState as UserProfileState.Success<UserProfileModel>).data
                        ProfileHeaderSection(
                            userName = user.fullName,
                            email = user.email,
                            onEditProfileClick = {
                                val intent = Intent(context, EditUserProfileActivity::class.java)
                                context.startActivity(intent)
                            }
                        )
                    }
                    else -> {}
                }
            }

            // ---------------- QUICK STATS ----------------
            item {
                val applicationsCount = when (applicationsState) {
                    is UserProfileState.Success -> (applicationsState as UserProfileState.Success<List<ApplicationItemForUserProfile>>).data.size
                    else -> 0
                }
                val schoolsCount = when (shortlistedSchoolsState) {
                    is UserProfileState.Success -> (shortlistedSchoolsState as UserProfileState.Success<List<ShortlistedSchoolForUserProfile>>).data.size
                    else -> 0
                }
                QuickStatsSection(applicationsCount = applicationsCount, shortlistedCount = schoolsCount)
            }

            // ---------------- SHORTLISTED SCHOOLS ----------------
            item {
                when (shortlistedSchoolsState) {
                    is UserProfileState.Loading -> {}
                    is UserProfileState.Error -> Text(
                        "Error loading schools",
                        Modifier.padding(20.dp),
                        color = AccentRed
                    )
                    is UserProfileState.Success -> {
                        val schools = (shortlistedSchoolsState as UserProfileState.Success<List<ShortlistedSchoolForUserProfile>>).data
                        ShortlistedSchoolsSectionForUserProfile(
                            schools = schools,
                            onSchoolClick = {},
                            onViewAllClick = {}
                        )
                    }
                    else -> {}
                }
            }

            // ---------------- APPLICATIONS HEADER ----------------
            item {
                ApplicationsHeaderSectionForUserProfile()
            }

            // ---------------- APPLICATION LIST ----------------
            item {
                when (applicationsState) {
                    is UserProfileState.Loading -> {}
                    is UserProfileState.Error -> Text(
                        "Error loading applications",
                        Modifier.padding(20.dp),
                        color = AccentRed
                    )
                    is UserProfileState.Success -> {
                        val applications = (applicationsState as UserProfileState.Success<List<ApplicationItemForUserProfile>>).data
                        if (applications.isEmpty()) {
                            EmptyStateCard(
                                icon = Icons.Outlined.Description,
                                title = "No Applications Yet",
                                subtitle = "Start applying to schools to see your applications here"
                            )
                        } else {
                            ApplicationsListSectionForUserProfile(
                                applications = applications,
                                onViewSchoolClick = {},
                                onViewPdfClick = {}
                            )
                        }
                    }
                    else -> {}
                }
            }

            // ---------------- ACCOUNT DETAILS ----------------
            item {
                val userProfile = when (userProfileState) {
                    is UserProfileState.Success -> (userProfileState as UserProfileState.Success<UserProfileModel>).data
                    else -> null
                }
                AccountDetailsSectionForUserProfile(userProfile)
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { LogoutButtonForUserProfile() }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}


// -------------------- PROFILE HEADER WITH GRADIENT --------------------
@Composable
fun ProfileHeaderSection(
    userName: String,
    email: String,
    onEditProfileClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryBlueDark)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 100.dp)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "My Profile",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }

    // Profile Card overlapping the header
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-60).dp)
            .shadow(12.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(6.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE))
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painterResource(id = R.drawable.school_profile),
                    contentDescription = null,
                    modifier = Modifier.size(45.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                userName.ifEmpty { "User Name" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                email.ifEmpty { "user@email.com" },
                fontSize = 14.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Edit Profile Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(PrimaryBlue, PrimaryBlueDark)
                        )
                    )
                    .clickable { onEditProfileClick() }
                    .padding(horizontal = 28.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.Edit,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Edit Profile",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

// -------------------- QUICK STATS SECTION --------------------
@Composable
fun QuickStatsSection(applicationsCount: Int, shortlistedCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-40).dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Outlined.Description,
            count = applicationsCount.toString(),
            label = "Applications",
            color = PrimaryBlue,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Outlined.Favorite,
            count = shortlistedCount.toString(),
            label = "Shortlisted",
            color = AccentRed,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(
    icon: ImageVector,
    count: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    count,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    label,
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }
    }
}

// -------------------- EMPTY STATE CARD --------------------
@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(ChipBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = ChipText,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(Modifier.height(4.dp))
            Text(
                subtitle,
                fontSize = 13.sp,
                color = TextSecondary
            )
        }
    }
}

// -------------------- LEGACY WELCOME HEADER (DEPRECATED) --------------------
@Composable
fun WelcomeHeaderSectionForUserProfile(userName: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Welcome back,",
                fontSize = 16.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                userName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )
            Text(
                "Manage your applications here",
                fontSize = 14.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.15f))
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.school_profile),
                contentDescription = "Profile Icon",
                modifier = Modifier.size(32.dp)
            )
        }
    }
}


// ---------------- SHORTLISTED SCHOOLS -------------------

@Composable
fun ShortlistedSchoolsSectionForUserProfile(
    schools: List<ShortlistedSchoolForUserProfile>,
    onSchoolClick: (ShortlistedSchoolForUserProfile) -> Unit,
    onViewAllClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-20).dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Favorite,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Shortlisted Schools",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Text(
                "View all →",
                color = PrimaryBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onViewAllClick() }
            )
        }

        if (schools.isEmpty()) {
            EmptyStateCard(
                icon = Icons.Outlined.FavoriteBorder,
                title = "No Shortlisted Schools",
                subtitle = "Save schools you're interested in to see them here"
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(schools) { school ->
                    ShortlistedSchoolCardForUserProfile(school) { onSchoolClick(school) }
                }
            }
        }
    }
}

@Composable
fun ShortlistedSchoolCardForUserProfile(
    school: ShortlistedSchoolForUserProfile,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(140.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ChipBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.School,
                    contentDescription = null,
                    tint = ChipText,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                school.name,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = TextPrimary,
                maxLines = 2
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    school.location,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    maxLines = 1
                )
            }
        }
    }
}

// ---------------- MY APPLICATIONS -----------------------

@Composable
fun ApplicationsHeaderSectionForUserProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Description,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "My Applications",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
fun ApplicationsListSectionForUserProfile(
    applications: List<ApplicationItemForUserProfile>,
    onViewSchoolClick: (ApplicationItemForUserProfile) -> Unit,
    onViewPdfClick: (ApplicationItemForUserProfile) -> Unit
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val cardWidth = screenWidth - 48.dp

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(applications) { app ->
            ApplicationCardForUserProfile(
                application = app,
                width = cardWidth,
                onViewSchoolClick = { onViewSchoolClick(app) },
                onViewPdfClick = { onViewPdfClick(app) }
            )
        }
    }
}

@Composable
fun ApplicationCardForUserProfile(
    application: ApplicationItemForUserProfile,
    width: Dp,
    onViewSchoolClick: () -> Unit,
    onViewPdfClick: () -> Unit
) {
    val badgeColor = when (application.status.lowercase()) {
        "accepted" -> AccentGreen
        "pending" -> AccentOrange
        else -> AccentRed
    }

    Card(
        modifier = Modifier
            .width(width)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "School",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        application.schoolName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        application.status,
                        color = badgeColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.Tag,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    application.applicationCode,
                    fontSize = 13.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SmallActionButtonForUserProfile(
                    text = "View School",
                    icon = Icons.Outlined.School,
                    onClick = onViewSchoolClick,
                    modifier = Modifier.weight(1f)
                )
                SmallActionButtonForUserProfile(
                    text = "View PDF",
                    icon = Icons.Outlined.PictureAsPdf,
                    onClick = onViewPdfClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun SmallActionButtonForUserProfile(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ChipBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                tint = ChipText,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = ChipText
            )
        }
    }
}

// ---------------- ACCOUNT DETAILS -----------------------

@Composable
fun AccountDetailsSectionForUserProfile(userProfile: UserProfileModel?) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 24.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    "Account Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(20.dp))

            AccountDetailItemForUserProfile(
                icon = Icons.Outlined.Person,
                title = "Full Name",
                value = userProfile?.fullName ?: "N/A"
            )
            AccountDetailItemForUserProfile(
                icon = Icons.Outlined.Email,
                title = "Email",
                value = userProfile?.email ?: "N/A"
            )
            AccountDetailItemForUserProfile(
                icon = Icons.Outlined.LocationOn,
                title = "Location",
                value = userProfile?.location ?: "N/A"
            )
            AccountDetailItemForUserProfile(
                icon = Icons.Outlined.Badge,
                title = "Role",
                value = userProfile?.role?.takeIf { it.isNotEmpty() } ?: "Student",
                isLast = true
            )
        }
    }
}

@Composable
fun AccountDetailItemForUserProfile(
    icon: ImageVector,
    title: String,
    value: String,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(ChipBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = ChipText,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Medium
            )
            Text(
                value,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
    if (!isLast) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFF3F4F6))
        )
    }
}

// ---------------- LOGOUT BUTTON -------------------------

@Composable
fun LogoutButtonForUserProfile() {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable {
                FirebaseAuth.getInstance().signOut()
                // Navigate to login screen
                val intent = Intent(context, UserLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentRed)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                "Log Out",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// ---------------- PREVIEW ------------------------------
@Preview(showBackground = true)
@Composable
fun PreviewUserProfileScreen() {
    UserProfileScreen(
        viewModel = null
    )
}