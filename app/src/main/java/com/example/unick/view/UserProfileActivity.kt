package com.example.unick.view

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import com.example.unick.R


// -------------------- DATA MODELS --------------------

data class ShortlistedSchoolForUserProfile(
    val id: String,
    val name: String,
    val location: String
)

data class ApplicationItemForUserProfile(
    val id: String,
    val schoolName: String,
    val status: String,
    val applicationCode: String
)

// -------------------- ROOT SCREEN ---------------------
// The UserProfileActivity class has been removed as this screen is now hosted by NavHost in DashboardActivity.
@Composable
fun UserProfileScreen() {

    // 🔹 SAMPLE DATA (will later be replaced by database values)
    val shortlistedSchools = remember {
        listOf(
            ShortlistedSchoolForUserProfile("1", "BSC College", "Kathmandu"),
            ShortlistedSchoolForUserProfile("2", "ABC Intl School", "Lalitpur"),
            ShortlistedSchoolForUserProfile("3", "Himalayan Academy", "Bhaktapur")
        )
    }

    val applications = remember {
        listOf(
            ApplicationItemForUserProfile("1", "BSC College", "Rejected", "ID123456"),
            ApplicationItemForUserProfile("2", "ABC Intl School", "Pending", "ID987654"),
            ApplicationItemForUserProfile("3", "Himalayan Academy", "Accepted", "ID555888")
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {

            // ------------------ WELCOME HEADER ------------------
            item { WelcomeHeaderSectionForUserProfile() }

            // ------------------ SHORTLISTED SCHOOLS ------------------
            item {
                ShortlistedSchoolsSectionForUserProfile(
                    schools = shortlistedSchools,
                    onSchoolClick = {},
                    onViewAllClick = {}
                )
            }

            // ------------------ APPLICATIONS HEADER ------------------
            item {
                val context = LocalContext.current

                ApplicationsHeaderSectionForUserProfile(
                    onEditProfileClick = {
                        val intent = Intent(context, EditUserProfileActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            // ------------------ APPLICATION LIST ------------------
            item {
                ApplicationsListSectionForUserProfile(
                    applications = applications,
                    onViewSchoolClick = {},
                    onViewPdfClick = {}
                )
            }

            // ------------------ ACCOUNT DETAILS ------------------
            item { AccountDetailsSectionForUserProfile() }

            // ------------------ SPACING BEFORE LOGOUT ------------------
            item { Spacer(modifier = Modifier.height(20.dp)) }

            // ------------------ LOGOUT BUTTON ------------------
            item { LogoutButtonForUserProfile() }

            // ------------------ BOTTOM SAFE SPACING ------------------
            item { Spacer(modifier = Modifier.height(50.dp)) }
        }
    }
}

// -------------------- WELCOME HEADER --------------------

@Composable
fun WelcomeHeaderSectionForUserProfile() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column {
            Text("Welcome, User", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("This is your personalized dashboard.", fontSize = 15.sp, color = Color.Gray)
        }

        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color(0xFFE7F2FF))
        ) {
            Image(
                painterResource(id = R.drawable.school_profile),
                contentDescription = "Profile Icon",
                modifier = Modifier.padding(10.dp)
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Your Shortlisted Schools", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("View all →", color = Color(0xFF3A6DFF), modifier = Modifier.clickable { onViewAllClick() })
    }

    Box(
        modifier = Modifier
            .padding(20.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5FCFF))
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(schools) { school ->
                ShortlistedSchoolCardForUserProfile(school) { onSchoolClick(school) }
            }
        }
    }
}

@Composable
fun ShortlistedSchoolCardForUserProfile(
    school: ShortlistedSchoolForUserProfile,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxHeight()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(school.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(school.location, color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.weight(1f))
        Text("Tap to view details", color = Color(0xFF3A6DFF), fontSize = 12.sp)
    }
}

// ---------------- MY APPLICATIONS -----------------------

@Composable
fun ApplicationsHeaderSectionForUserProfile(
    onEditProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text("My Applications", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Text(
            "Edit Profile",
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFBFC8FF))
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .clickable { onEditProfileClick() },
            color = Color.White,
            fontWeight = FontWeight.Bold
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
    val cardWidth = screenWidth - 40.dp

    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
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
        "accepted" -> Color(0xFF4CAF50)
        "pending" -> Color(0xFFFFC107)
        else -> Color(0xFFFF9494)
    }

    Column(
        modifier = Modifier
            .width(width)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5FCFF))
            .padding(20.dp)
    ) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("School", fontSize = 14.sp, color = Color.Gray)
                Text(application.schoolName, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(badgeColor)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(application.status, color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(application.applicationCode, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(Modifier.height(12.dp))

        Row {
            SmallActionButtonForUserProfile("View School", onClick = onViewSchoolClick)
            Spacer(Modifier.width(10.dp))
            SmallActionButtonForUserProfile("View PDF", onClick = onViewPdfClick)
        }
    }
}

@Composable
fun SmallActionButtonForUserProfile(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFFD6EDFF))
            .padding(horizontal = 18.dp, vertical = 10.dp)
            .clickable(onClick = onClick)
    ) {
        Text(text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
    }
}

// ---------------- ACCOUNT DETAILS -----------------------

@Composable
fun AccountDetailsSectionForUserProfile() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5FCFF))
            .padding(20.dp)
    ) {
        Text("Account Details Summary", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))

        AccountDetailItemForUserProfile("Registered Email", "uvica.shrestha@email.com")
        AccountDetailItemForUserProfile("Phone Number", "+977 9841234567")
        AccountDetailItemForUserProfile("Last Login", "Today, 10:30 AM")
        AccountDetailItemForUserProfile("Joined Date", "Jan 15, 2024")
    }
}

@Composable
fun AccountDetailItemForUserProfile(title: String, value: String) {
    Column(Modifier.padding(vertical = 6.dp)) {
        Text(title, fontSize = 14.sp, color = Color.Gray)
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

// ---------------- LOGOUT BUTTON -------------------------

@Composable
fun LogoutButtonForUserProfile() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFFFF3B30))
            .clickable {}
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("Log Out", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

// ---------------- PREVIEW ------------------------------

@Preview(showBackground = true)
@Composable
fun PreviewUserProfileScreen() {
    UserProfileScreen()
}
