package com.example.unick.view

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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.example.unick.viewmodel.SchoolDetailViewModel
import com.example.unick.model.SchoolReviewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SchoolDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Get schoolId from intent - prioritize "uid" then fall back to "schoolId"
        @Suppress("DEPRECATION")
        val schoolId = intent.getStringExtra("uid")
            ?: intent.getStringExtra("schoolId")
            ?: intent.getParcelableExtra<com.example.unick.model.SchoolForm>("school_details")?.uid
            ?: ""

        // Get selected tab from intent (default to "Overview")
        val selectedTabFromIntent = intent.getStringExtra("selectedTab") ?: "Overview"

        if (schoolId.isBlank()) {
            android.widget.Toast.makeText(this, "School ID missing!", android.widget.Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContent {
            val vm = remember { SchoolDetailViewModel() }

            LaunchedEffect(schoolId) {
                if (schoolId.isNotEmpty()) {
                    vm.loadSchoolDetail(schoolId)
                }
            }

            SchoolDetailScreen(
                vm = vm,
                schoolId = schoolId,
                initialTab = selectedTabFromIntent,
                onBack = { finish() },
                onOpenGallery = {
                    startActivity(
                        Intent(this, SchoolGalleryActivity::class.java).putExtra("schoolId", schoolId)
                    )
                },
                onSchoolSetting = {
                    startActivity(
                        Intent(this, SchoolSettingsActivity::class.java).putExtra("schoolId", schoolId)
                    )
                },
                onApplyNow = {
                    startActivity(
                        Intent(this, StudentApplicationActivity::class.java).putExtra("schoolId", schoolId)
                    )
                },
                onViewApplications = {
                    startActivity(
                        Intent(this, ViewApplicationActivity::class.java).putExtra("schoolId", schoolId)
                    )
                }
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailScreen(
    schoolId: String,
    vm: SchoolDetailViewModel = SchoolDetailViewModel(),
    initialTab: String = "Overview",
    onBack: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") onOpenGallery: () -> Unit = {},
    onSchoolSetting: () -> Unit = {},
    onApplyNow: () -> Unit = {},
    onViewApplications: () -> Unit = {}
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(initialTab) }

    val profile = vm.schoolProfile
    @Suppress("UNUSED")
    val gallery = vm.gallery
    val reviews = vm.reviews

    // Check if current user is the school owner/admin
    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isSchoolOwner = currentUserId == schoolId

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = profile?.schoolName?.takeIf { it.isNotBlank() } ?: "School Profile",
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show heart icon only for students (non-school owners)
                    if (!isSchoolOwner) {
                        IconButton(onClick = { vm.toggleShortlist() }) {
                            Icon(
                                imageVector = if (vm.isShortlisted) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = if (vm.isShortlisted) "Remove from shortlist" else "Add to shortlist",
                                tint = if (vm.isShortlisted) Color.Red else Color.Gray
                            )
                        }
                    }
                    // Show Settings button only for school owners
                    if (isSchoolOwner) {
                        TextButton(onClick = onSchoolSetting) {
                            Text("Settings", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            )

        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {

            // ---- Banner with Gradient Overlay + Gallery Button ----
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = profile?.imageUrl,
                        contentDescription = "School Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentScale = ContentScale.Crop
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f)
                                    )
                                )
                            )
                    )

                    Button(
                        onClick = onOpenGallery,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp
                        )
                    ) {
                        Icon(
                            Icons.Outlined.PhotoLibrary,
                            contentDescription = "Gallery",
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("Gallery", color = Color(0xFF8B5CF6), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // ---- Header Card ----
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = profile?.schoolName ?: "Loading...",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(Modifier.height(10.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = Color(0xFF3B82F6),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = profile?.location ?: "",
                                color = Color(0xFF64748B),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.weight(1f)
                            )

                            if (!profile?.location.isNullOrBlank()) {
                                IconButton(
                                    onClick = {
                                        val query = Uri.encode(profile.location)
                                        val uri = "geo:0,0?q=$query".toUri()
                                        val intent = Intent(Intent.ACTION_VIEW, uri)
                                        intent.setPackage("com.google.android.apps.maps")
                                        try {
                                            context.startActivity(intent)
                                        } catch (_: Exception) {
                                            try {
                                                intent.setPackage(null)
                                                context.startActivity(intent)
                                            } catch (_: Exception) {
                                                android.widget.Toast.makeText(context, "No map app found", android.widget.Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = Color(0xFF3B82F6).copy(alpha = 0.1f),
                                        contentColor = Color(0xFF3B82F6)
                                    ),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "View on Map",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Action Buttons - Show based on user role
                        if (isSchoolOwner) {
                            Button(
                                onClick = onViewApplications,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF3B82F6)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 2.dp,
                                    pressedElevation = 6.dp
                                )
                            ) {
                                Icon(
                                    Icons.Outlined.Description,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("View Applications", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            }
                        } else {
                            Button(
                                onClick = onApplyNow,
                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981)
                                ),
                                shape = RoundedCornerShape(14.dp),
                                elevation = ButtonDefaults.buttonElevation(
                                    defaultElevation = 2.dp,
                                    pressedElevation = 6.dp
                                )
                            ) {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("Apply Now", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            // ---- Tab Row ----
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        SchoolTabItem("Overview", selectedTab == "Overview") { selectedTab = "Overview" }
                        SchoolTabItem("Academics", selectedTab == "Academics") { selectedTab = "Academics" }
                        SchoolTabItem("Connect", selectedTab == "Connect") { selectedTab = "Connect" }
                        SchoolTabItem("Reviews", selectedTab == "Reviews") { selectedTab = "Reviews" }
                    }
                }
            }

            // ---- Tab Content ----
            when (selectedTab) {

                "Overview" -> {
                    item {
                        OverviewCard(
                            title = "About",
                            text = profile?.description ?: "No description"
                        )
                    }
                    item {
                        OverviewCard(
                            title = "Programs",
                            text = profile?.programsOffered ?: "Not added",
                            onSeeMore = { selectedTab = "Academics" }
                        )
                    }
                    item {
                        OverviewCard(
                            title = "Facilities",
                            text = profile?.facilities ?: "Not added",
                            onSeeMore = { selectedTab = "Academics" }
                        )
                    }
                    item {
                        OverviewCard(
                            title = "Scholarship",
                            text = if (profile?.scholarshipAvailable == true) "Available" else "Not available",
                            onSeeMore = { selectedTab = "Academics" }
                        )
                    }
                }

                "Academics" -> {
                    item {
                        SectionTitle("Academics")
                    }
                    item {
                        InfoCard("Curriculum", profile?.curriculum ?: "Not added")
                    }
                    item {
                        InfoCard("Programs Offered (Class 1–12)", profile?.programsOffered ?: "Not added")
                    }
                    item {
                        InfoCard("Total Students", profile?.totalStudents ?: "Not added")
                    }
                    item {
                        InfoCard("Extracurricular", profile?.extracurricular ?: "Not added")
                    }
                    item {
                        InfoCard("Transport Facility", if (profile?.transportFacility == true) "Yes" else "No")
                    }
                    item {
                        InfoCard("Hostel Facility", if (profile?.hostelFacility == true) "Yes" else "No")
                    }
                    item {
                        InfoCard("Tuition Fee", profile?.tuitionFee ?: "Not added")
                    }
                    item {
                        InfoCard("Admission Fee", profile?.admissionFee ?: "Not added")
                    }
                    item {
                        InfoCard("Established Year", profile?.establishedYear ?: "Not added")
                    }
                    item {
                        InfoCard("Principal Name", profile?.principalName ?: "Not added")
                    }
                }

                "Connect" -> {
                    item { SectionTitle("Contact & Location") }

                    item {
                        ContactRow(
                            label = "Email",
                            value = profile?.email ?: "Not added",
                            onClick = {
                                val email = profile?.email ?: return@ContactRow
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:$email".toUri()
                                }
                                context.startActivity(intent)
                            }
                        )
                    }

                    item {
                        ContactRow(
                            label = "Phone",
                            value = profile?.contactNumber ?: "Not added",
                            onClick = {
                                val phone = profile?.contactNumber ?: return@ContactRow
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = "tel:$phone".toUri()
                                }
                                context.startActivity(intent)
                            }
                        )
                    }

                    item {
                        ContactRow(
                            label = "Website",
                            value = profile?.website ?: "Not added",
                            onClick = {
                                val url = profile?.website ?: return@ContactRow
                                val open = Intent(Intent.ACTION_VIEW, ensureUrl(url).toUri())
                                context.startActivity(open)
                            }
                        )
                    }

                    item {
                        ContactRow(
                            label = "Map Location",
                            value = "Open in Google Maps",
                            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
                            onClick = {
                                val q = profile?.googleMapUrl ?: profile?.schoolName ?: "School"
                                val mapUri = "geo:0,0?q=${Uri.encode(q)}".toUri()
                                val i = Intent(Intent.ACTION_VIEW, mapUri).apply {
                                    setPackage("com.google.android.apps.maps")
                                }
                                context.startActivity(i)
                            }
                        )
                    }

                    item {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = "Address: ${profile?.location ?: "Not added"}",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color.DarkGray
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                "Reviews" -> {
                    item {
                        ReviewsHeader(
                            avg = vm.avgRating,
                            total = vm.totalReviews,
                            onWriteReview = {}
                        )
                    }

                    item {
                        ReviewComposer(
                            onSubmit = { rating, comment ->
                                val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                                if (currentUser != null) {
                                    vm.submitReview(
                                        reviewerUid = currentUser.uid,
                                        rating = rating,
                                        comment = comment
                                    )
                                } else {
                                    android.widget.Toast.makeText(
                                        context,
                                        "Please login to submit a review",
                                        android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }

                    items(reviews.size) { idx ->
                        ReviewCard(review = reviews[idx])
                    }

                    item { Spacer(Modifier.height(28.dp)) }
                }
            }

            // ---- Loading / Error ----
            item {
                if (vm.loading) {
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        CircularProgressIndicator()
                    }
                    Spacer(Modifier.height(12.dp))
                }

                vm.error?.let {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SchoolTabItem(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp,
            color = if (selected) Color(0xFF3B82F6) else Color(0xFF64748B)
        )
        Spacer(Modifier.height(6.dp))
        if (selected) {
            Box(
                Modifier
                    .width(40.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFF3B82F6))
            )
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
private fun OverviewCard(title: String, text: String, onSeeMore: (() -> Unit)? = null) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(Modifier.height(6.dp))
            Text(text, color = Color.DarkGray)
            if (onSeeMore != null) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = "See more →",
                    color = Color(0xFF1E64FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSeeMore() }
                )
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(value, color = Color.DarkGray)
        }
    }
}

@Composable
private fun ContactRow(
    label: String,
    value: String,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(Modifier.width(10.dp))
            }
            Column(Modifier.weight(1f)) {
                Text(label, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(value, color = Color.DarkGray)
            }
            Text("↗", fontSize = 18.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ReviewsHeader(avg: Double, total: Int, onWriteReview: () -> Unit) {
    Column(Modifier.padding(16.dp)) {
        Text("Reviews", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            StarsRow(rating = avg)
            Spacer(Modifier.width(10.dp))
            Text(String.format(Locale.US, "%.1f", avg), fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(10.dp))
            Text("($total reviews)", color = Color.DarkGray)
        }
        Spacer(Modifier.height(10.dp))
        Button(onClick = onWriteReview, shape = RoundedCornerShape(12.dp)) {
            Text("Write a Review")
        }
    }
}

@Composable
private fun StarsRow(rating: Double) {
    val full = rating.toInt().coerceIn(0, 5)
    val empty = (5 - full).coerceIn(0, 5)

    Row {
        repeat(full) {
            Icon(Icons.Filled.Star, contentDescription = null)
        }
        repeat(empty) {
            Icon(Icons.Outlined.Star, contentDescription = null)
        }
    }
}

@Composable
private fun ReviewComposer(onSubmit: (rating: Int, comment: String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Your Rating", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row {
                (1..5).forEach { star ->
                    val filled = star <= rating
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier
                            .size(30.dp)
                            .clickable { if (!isSubmitting) rating = star }
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { if (!isSubmitting) comment = it },
                label = { Text("Write your review") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        isSubmitting = true
                        onSubmit(rating, comment)
                        // Reset form after submission
                        comment = ""
                        rating = 5
                        isSubmitting = false
                    }
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSubmitting && comment.isNotBlank()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting...")
                } else {
                    Text("Submit Review")
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ReviewCard(review: SchoolReviewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {

            // ✅ show Full Name instead of UID
            ReviewerNameText(review.reviewerUid)

            Spacer(Modifier.height(6.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(review.rating.coerceIn(0, 5)) {
                    Icon(Icons.Filled.Star, contentDescription = null)
                }
                Spacer(Modifier.width(8.dp))
                Text("${review.rating}/5", color = Color.DarkGray)
            }

            Spacer(Modifier.height(8.dp))

            val text = review.comment
            Text(
                text = if (expanded || text.length < 120) text else text.take(120) + "...",
                color = Color.DarkGray
            )

            if (text.length >= 120) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (expanded) "Show less" else "Read more",
                    color = Color(0xFF1E64FF),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}


@Composable
fun ReviewerNameText(reviewerUid: String) {
    var name by remember(reviewerUid) { mutableStateOf<String?>(null) }

    LaunchedEffect(reviewerUid) {
        val db = FirebaseDatabase
            .getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
            .reference

        db.child("Users").child(reviewerUid).child("fullName")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullName = snapshot.getValue(String::class.java)?.trim()
                    name = if (fullName.isNullOrBlank()) "Anonymous" else fullName
                }
                override fun onCancelled(error: DatabaseError) {
                    name = "Anonymous"
                }
            })
    }

    Text(
        text = "Reviewer: ${name ?: "Loading..."}",
        fontWeight = FontWeight.Bold
    )
}


private fun ensureUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
}