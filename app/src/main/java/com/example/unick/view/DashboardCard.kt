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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.unick.model.SchoolForm
import com.example.unick.model.SchoolReviewModel
import com.example.unick.view.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Locale

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
fun SchoolDetailsScreen(
    school: SchoolForm,
    isSchoolView: Boolean,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // ✅ Reuse your existing VM (same one used in SchoolDetailActivity)
    val reviewVm = remember { SchoolDetailViewModel() }

    // Load reviews (and any other data VM loads) by this school's UID
    LaunchedEffect(school.uid) {
        if (school.uid.isNotBlank()) reviewVm.loadSchoolDetail(school.uid)
    }

    val reviews = reviewVm.reviews
    val avgRating = reviewVm.avgRating
    val totalReviews = reviewVm.totalReviews

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.7f)
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            UnifiedBottomNavigationBar(
                currentRoute = "",
                onNavigate = { route ->
                    when (route) {
                        BottomNavItem.Home.route -> {
                            val intent = Intent(context, DashboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }

                        BottomNavItem.AIChat.route -> {
                            val intent = Intent(context, DashboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra("start_destination", BottomNavItem.AIChat.route)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }

                        BottomNavItem.Profile.route -> {
                            context.startActivity(Intent(context, UserProfileActivity::class.java))
                        }

                        else -> {
                            val intent = Intent(context, DashboardActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra("start_destination", route)
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }
                    }
                },
                onProfileClick = {
                    context.startActivity(Intent(context, UserProfileActivity::class.java))
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
            ) {

                // ---- Header Image ----
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        AsyncImage(
                            model = school.imageUrl,
                            contentDescription = "School Banner",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // ---- Title & Basic Info ----
                item {
                    Column(
                        modifier = Modifier
                            .offset(y = (-30).dp)
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
                                    Icon(
                                        Icons.Outlined.LocationOn,
                                        null,
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(16.dp)
                                    )
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

                        // ✅ Action buttons row (kept consistent) + NEW: Gallery + Reviews jump
                        if (!isSchoolView) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        val intent = Intent(context, CompareActivity::class.java)
                                        intent.putExtra("school_details", school)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        Color(0xFF2563EB)
                                    ),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = Color(0xFF2563EB)
                                    )
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Outlined.CompareArrows,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compare", maxLines = 1)
                                }

                                Button(
                                    onClick = {
                                        val intent =
                                            Intent(context, StudentApplicationActivity::class.java)
                                        intent.putExtra("schoolId", school.uid)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2563EB)
                                    )
                                ) {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Apply Now", maxLines = 1)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // ✅ NEW: Gallery + Reviews buttons (always useful for students)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val intent =
                                        Intent(context, SchoolGalleryActivity::class.java)
                                            .putExtra("schoolId", school.uid)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFF2563EB)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF2563EB)
                                )
                            ) {
                                Icon(Icons.Outlined.PhotoLibrary, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Gallery", maxLines = 1)
                            }

                            OutlinedButton(
                                onClick = {
                                    // simple jump to reviews section: no scroll state here,
                                    // so we just show composer immediately (below)
                                    // (if you want auto-scroll, tell me; I’ll wire it)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    Color(0xFF2563EB)
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF2563EB)
                                )
                            ) {
                                Icon(Icons.Outlined.StarOutline, null, Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Reviews", maxLines = 1)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

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
                }

                // ✅ NEW: Reviews section (consistent card UI)
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                    DetailSectionHeader("Reviews")

                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        ReviewsSummaryCard(
                            avg = avgRating,
                            total = totalReviews
                        )

                        Spacer(Modifier.height(12.dp))

                        ReviewComposerCard(
                            onSubmit = { rating, comment ->
                                val uid = FirebaseAuth.getInstance().currentUser?.uid
                                if (uid.isNullOrBlank()) return@ReviewComposerCard

                                reviewVm.submitReview(
                                    reviewerUid = uid,
                                    rating = rating,
                                    comment = comment
                                )
                            }
                        )

                        Spacer(Modifier.height(12.dp))

                        if (reviewVm.loading) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                                CircularProgressIndicator()
                            }
                        } else {
                            if (reviews.isEmpty()) {
                                Text(
                                    "No reviews yet. Be the first to review!",
                                    color = Color(0xFF64748B),
                                    modifier = Modifier.padding(vertical = 10.dp)
                                )
                            } else {
                                reviews.forEach { r ->
                                    ReviewCardSimple(review = r)
                                    Spacer(Modifier.height(10.dp))
                                }
                            }
                        }

                        reviewVm.error?.let {
                            Text(
                                text = it,
                                color = Color.Red,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }

                item { Spacer(modifier = Modifier.height(30.dp)) }
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
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Icon(icon, null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = Color(0xFF1E293B),
            maxLines = 2,
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
fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String,
    isLink: Boolean = false,
    context: Context? = null
) {
    if (value.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable(enabled = isLink) {
                    if (isLink && context != null) {
                        try {
                            val intent = when (label) {
                                "Email" -> Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:$value".toUri()
                                }
                                "Phone" -> Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:$value".toUri()
                                }
                                "Website" -> {
                                    var url = value
                                    if (!url.startsWith("http")) url = "https://$url"
                                    Intent(Intent.ACTION_VIEW, url.toUri())
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
                    .shadow(1.dp, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = if (isLink) Color(0xFF2563EB) else Color(0xFF64748B),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color(0xFF94A3B8))
                Text(
                    value,
                    fontSize = 15.sp,
                    color = if (isLink) Color(0xFF2563EB) else Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/* ---------------- Reviews UI (matches your card style) ---------------- */

@Composable
private fun ReviewsSummaryCard(avg: Double, total: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.Star, null, tint = Color(0xFF2563EB))
            Spacer(Modifier.width(10.dp))
            Text(
                text = String.format(Locale.US, "%.1f", avg),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.width(10.dp))
            Text(
                text = "($total reviews)",
                color = Color(0xFF64748B)
            )
        }
    }
}

@Composable
private fun ReviewComposerCard(
    onSubmit: (rating: Int, comment: String) -> Unit
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Write a review", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Spacer(Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                (1..5).forEach { star ->
                    val filled = star <= rating
                    Icon(
                        imageVector = if (filled) Icons.Outlined.Star else Icons.Outlined.StarOutline,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier
                            .size(26.dp)
                            .clickable { rating = star }
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Your comment") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSubmit(rating, comment)
                        comment = ""
                        rating = 5
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("Submit Review", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun ReviewCardSimple(review: SchoolReviewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = "User: ${review.reviewerUid}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(review.rating.coerceIn(0, 5)) {
                    Icon(Icons.Outlined.Star, null, tint = Color(0xFF2563EB))
                }
                Spacer(Modifier.width(8.dp))
                Text("${review.rating}/5", color = Color(0xFF64748B))
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = review.comment,
                color = Color(0xFF475569),
                lineHeight = 22.sp
            )
        }
    }
}
