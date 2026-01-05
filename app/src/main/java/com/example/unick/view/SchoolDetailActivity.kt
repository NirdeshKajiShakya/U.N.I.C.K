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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import coil.compose.AsyncImage
import com.example.unick.viewmodel.SchoolDetailViewModel

class SchoolDetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // get schoolId from intent
        var schoolId = intent.getStringExtra("uid") ?: ""
        if (schoolId == "") {
            schoolId = "XcfjtBIHVfdpHeh8QSMy7j3VGiU2"
        }

        setContent {
            val vm = remember { SchoolDetailViewModel() }

            LaunchedEffect(schoolId) {
                vm.loadSchoolDetail(schoolId)
            }

            SchoolDetailScreen(
                vm = vm,
                schoolId = schoolId,
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
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolDetailScreen(
    vm: SchoolDetailViewModel,
    schoolId: String,
    onBack: () -> Unit,
    onOpenGallery: () -> Unit,
    onSchoolSetting: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf("Overview") }

    val profile = vm.schoolProfile
    val gallery = vm.gallery
    val reviews = vm.reviews

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = { Text("School Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = onSchoolSetting) {
                        Text("Settings", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            // ---- Banner + Gallery Button ----
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    AsyncImage(
                        model = profile?.imageUrl,
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentScale = ContentScale.Crop
                    )

                    Button(
                        onClick = onOpenGallery,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Gallery")
                    }
                }
            }

            // ---- Header ----
            item {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = profile?.schoolName ?: "Loading...",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = profile?.location ?: "",
                        color = Color.DarkGray
                    )
                }
            }

            // ---- Tab Row ----
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SchoolTabItem("Overview", selectedTab == "Overview") { selectedTab = "Overview" }
                    SchoolTabItem("Academics", selectedTab == "Academics") { selectedTab = "Academics" }
                    SchoolTabItem("Connect", selectedTab == "Connect") { selectedTab = "Connect" }
                    SchoolTabItem("Reviews", selectedTab == "Reviews") { selectedTab = "Reviews" }
                }
                Divider()
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
                                    data = Uri.parse("mailto:$email")
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
                                    data = Uri.parse("tel:$phone")
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
                                val open = Intent(Intent.ACTION_VIEW, Uri.parse(ensureUrl(url)))
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
                                // store full google maps url later (for now build query)
                                val q = profile?.location ?: profile?.schoolName ?: "School"
                                val mapUri = Uri.parse("geo:0,0?q=${Uri.encode(q)}")
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
                            onWriteReview = {
                                // simple demo dialog below
                            }
                        )
                    }

                    item {
                        ReviewComposer(
                            onSubmit = { rating, comment ->
                                // reviewerUid should come from FirebaseAuth later
                                val demoUserUid = "demo_reviewer_uid"
                                vm.submitReview(
                                    reviewerUid = demoUserUid,
                                    rating = rating,
                                    comment = comment
                                )
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
            .padding(10.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        if (selected) {
            Box(
                Modifier
                    .width(44.dp)
                    .height(3.dp)
                    .background(Color.Black)
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
            Text(String.format("%.1f", avg), fontWeight = FontWeight.Bold)
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
                            .clickable { rating = star }
                    )
                    Spacer(Modifier.width(4.dp))
                }
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Write your review") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { onSubmit(rating, comment) },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit Review")
            }
        }
    }
    Spacer(Modifier.height(12.dp))
}

@Composable
private fun ReviewCard(review: com.example.unick.model.SchoolReviewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Reviewer: ${review.reviewerUid}", fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(review.rating.coerceIn(0, 5)) {
                    Icon(Icons.Filled.Star, null)
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

private fun ensureUrl(url: String): String {
    return if (url.startsWith("http://") || url.startsWith("https://")) url else "https://$url"
}
