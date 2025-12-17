package com.example.unick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

// ---------------- DATA CLASSES (FIREBASE-FRIENDLY) ----------------

data class AcademicCardDataForSchoolDetail(
    val title: String,
    val shortDescription: String,
    val details: List<String>
)

data class ContactInfoForSchoolDetail(
    val email: String,
    val phone: String,
    val address: String,
    val website: String?,
    val facebook: String?,
    val instagram: String?
)

data class ReviewForSchoolDetail(
    val reviewerId: String,
    val rating: Int,
    val comment: String,
    val date: String
)

// ---------------- ACTIVITY ----------------

class SchoolDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolDetailsScreenForSchoolDetail()
        }
    }
}

// ---------------- ROOT SCREEN ----------------

@Composable
fun SchoolDetailsScreenForSchoolDetail() {

    var selectedTab by remember { mutableStateOf("Overview") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        EmptyTopBarSpaceForSchoolDetail()
        BannerSectionForSchoolDetail()
        SchoolHeaderSectionForSchoolDetail()
        SchoolTabRowForSchoolDetail(
            selected = selectedTab,
            onSelect = { selectedTab = it }
        )
        Divider(color = Color.LightGray, thickness = 1.dp)

        when (selectedTab) {
            "Overview" -> OverviewSectionForSchoolDetail()
            "Academics" -> AcademicsSectionForSchoolDetail()
            "Connect" -> ConnectSectionForSchoolDetail()
            "Reviews" -> ReviewsSectionForSchoolDetail()
        }
    }
}

// ---------------- STATIC HEADER PARTS ----------------

@Composable
fun EmptyTopBarSpaceForSchoolDetail() {
    Spacer(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(Color(0xFFDDE8FF))
    )
}

@Composable
fun BannerSectionForSchoolDetail() {
    Image(
        painter = painterResource(id = R.drawable.school_banner),
        contentDescription = "School Banner",
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun SchoolHeaderSectionForSchoolDetail() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Image(
            painter = painterResource(id = R.drawable.school_profile),
            contentDescription = "School Logo",
            modifier = Modifier
                .size(55.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text("School Name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("School Bio", fontSize = 15.sp, color = Color.DarkGray)
        }
    }
}

@Composable
fun SchoolTabRowForSchoolDetail(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabItemForSchoolDetail("Overview", selected == "Overview") { onSelect("Overview") }
        TabItemForSchoolDetail("Academics", selected == "Academics") { onSelect("Academics") }
        TabItemForSchoolDetail("Connect", selected == "Connect") { onSelect("Connect") }
        TabItemForSchoolDetail("Reviews", selected == "Reviews") { onSelect("Reviews") }
    }
}

@Composable
fun TabItemForSchoolDetail(text: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )

        if (selected) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(3.dp)
                    .background(Color.Black)
            )
        }
    }
}

// ---------------- OVERVIEW TAB ----------------

@Composable
fun OverviewSectionForSchoolDetail() {
    Column(modifier = Modifier.padding(16.dp)) {

        OverviewItemForSchoolDetail("About School")
        OverviewItemForSchoolDetail("Curriculum & Streams")
        OverviewItemForSchoolDetail("Teachers & Faculty")
        OverviewItemForSchoolDetail("Student Achievements")
        OverviewItemForSchoolDetail("Scholarships")
        OverviewItemForSchoolDetail("Facilities & Activities")
    }
}

@Composable
fun OverviewItemForSchoolDetail(title: String) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        androidx.compose.material3.Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
    }

    if (expanded) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(16.dp)
        ) {
            Text(
                text = "Short overview about $title.\n\nFor full information, check the Academics, Connect or Reviews tabs.",
                fontSize = 14.sp
            )
        }
    }

    Divider()
}

// ---------------- ACADEMICS TAB (CARD STYLE) ----------------

@Composable
fun AcademicsSectionForSchoolDetail() {

    val sections = remember {
        listOf(
            AcademicCardDataForSchoolDetail(
                title = "Curriculum & Streams",
                shortDescription = "Grade 1–12 curriculum with different +2 streams.",
                details = listOf(
                    "Classes 1–10: English, Nepali, Mathematics, Science, Social, Computer.",
                    "Grade 11–12 Science: Physics, Chemistry, Biology, Mathematics, Computer.",
                    "Grade 11–12 Management: Accounting, Economics, Business Studies, Hotel Management.",
                    "Grade 11–12 Humanities: Sociology, Psychology, Mass Communication, Rural Development."
                )
            ),
            AcademicCardDataForSchoolDetail(
                title = "Teachers & Faculty",
                shortDescription = "Experienced, trained and student-friendly teachers.",
                details = listOf(
                    "Total teachers: 45+ across all grades.",
                    "Primary level: child-friendly trained class teachers.",
                    "Secondary level: subject experts for Math, Science, English and Social.",
                    "+2 level: M.Sc., M.Ed., MBA qualified lecturers.",
                    "Regular teacher-training and workshops every term."
                )
            ),
            AcademicCardDataForSchoolDetail(
                title = "Student Achievements",
                shortDescription = "Strong SEE and +2 results with national-level exposure.",
                details = listOf(
                    "Average SEE GPA above 3.4 in the last 3 years.",
                    "Multiple students scoring GPA 3.8+ every batch.",
                    "Winners of inter-school quiz, debate and science exhibitions.",
                    "Participation in district and national-level sports tournaments."
                )
            ),
            AcademicCardDataForSchoolDetail(
                title = "Scholarships",
                shortDescription = "Merit and need-based scholarships for deserving students.",
                details = listOf(
                    "Entrance-topper scholarship with up to 100% fee waiver.",
                    "SEE GPA-based scholarship for 3.6+ scorers.",
                    "Need-based support for financially weak families.",
                    "Sports & ECA scholarships for district / national players.",
                    "Sibling discount for families with 2+ children in school."
                )
            ),
            AcademicCardDataForSchoolDetail(
                title = "Facilities & Activities",
                shortDescription = "Labs, library, sports, transport and active ECA clubs.",
                details = listOf(
                    "Science and computer labs with modern equipment.",
                    "Library with textbooks, reference books and newspapers.",
                    "Playground for football, basketball and volleyball.",
                    "Transportation facility covering major city routes.",
                    "Music, Dance, Robotics, Literature and Social Service clubs."
                )
            )
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {
        sections.forEach { section ->
            AcademicCardForSchoolDetail(section)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AcademicCardForSchoolDetail(section: AcademicCardDataForSchoolDetail) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF5F8FF))
            .clickable { expanded = !expanded }
            .padding(16.dp)
    ) {

        Text(section.title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(section.shortDescription, fontSize = 14.sp, color = Color.DarkGray)
        Spacer(modifier = Modifier.height(8.dp))

        if (expanded) {
            section.details.forEach { line ->
                Text("• $line", fontSize = 14.sp, modifier = Modifier.padding(vertical = 2.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Show less ▲", fontSize = 13.sp, color = Color(0xFF3A6DFF))
        } else {
            Text("See more ▼", fontSize = 13.sp, color = Color(0xFF3A6DFF))
        }
    }
}

// ---------------- CONNECT TAB ----------------

@Composable
fun ConnectSectionForSchoolDetail() {

    val contactInfo = remember {
        ContactInfoForSchoolDetail(
            email = "info@bscschool.edu.np",
            phone = "+977 9812345678",
            address = "Banasthali, Kathmandu, Nepal",
            website = "https://www.bscschool.edu.np",
            facebook = "facebook.com/bscschool",
            instagram = "@bscschool_official"
        )
    }

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Contact & Location", fontSize = 20.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF5F8FF))
                .padding(16.dp)
        ) {
            Text("Contact Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("Email: ${contactInfo.email}", fontSize = 14.sp)
            Text("Phone: ${contactInfo.phone}", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF5F8FF))
                .padding(16.dp)
        ) {
            Text("Location", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(contactInfo.address, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "View on map (coming soon)",
                fontSize = 13.sp,
                color = Color(0xFF3A6DFF)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFF5F8FF))
                .padding(16.dp)
        ) {
            Text("Social Media", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            contactInfo.facebook?.let { Text("Facebook: $it", fontSize = 14.sp) }
            contactInfo.instagram?.let { Text("Instagram: $it", fontSize = 14.sp) }
            contactInfo.website?.let { Text("Website: $it", fontSize = 14.sp) }
        }
    }
}

// ---------------- REVIEWS TAB ----------------

@Composable
fun ReviewsSectionForSchoolDetail() {

    val reviews = remember {
        mutableStateListOf(
            ReviewForSchoolDetail(
                reviewerId = "student_01",
                rating = 5,
                comment = "Amazing school with supportive teachers and a friendly environment. Labs and library are very good.",
                date = "2024-01-12"
            ),
            ReviewForSchoolDetail(
                reviewerId = "guardian_02",
                rating = 4,
                comment = "Good academics and discipline. Would love to see more focus on sports and outdoor activities.",
                date = "2024-02-03"
            ),
            ReviewForSchoolDetail(
                reviewerId = "alumni_03",
                rating = 5,
                comment = "Studied here till Class 12. The guidance I received for my further studies was extremely helpful.",
                date = "2023-11-25"
            ),
            ReviewForSchoolDetail(
                reviewerId = "student_04",
                rating = 3,
                comment = "Overall good, but can improve canteen facilities and cleanliness during exam times.",
                date = "2024-03-18"
            )
        )
    }

    var showAddReviewDialog by remember { mutableStateOf(false) }

    val totalReviews = reviews.size
    val averageRating = if (totalReviews == 0) 0.0
    else reviews.sumOf { it.rating }.toDouble() / totalReviews.toDouble()

    // Star distribution map for bars
    val ratingDistribution: Map<Int, Int> = (1..5).associateWith { star ->
        reviews.count { it.rating == star }
    }

    Column(modifier = Modifier.padding(16.dp)) {

        ReviewsSummaryForSchoolDetail(
            averageRating = averageRating,
            totalReviews = totalReviews,
            ratingDistribution = ratingDistribution
        )

        Spacer(modifier = Modifier.height(16.dp))

        reviews.sortedByDescending { it.rating }.forEach { review ->
            SingleReviewItemForSchoolDetail(review)
            Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showAddReviewDialog = true },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3A6DFF))
        ) {
            Text("Write a Review", color = Color.White, fontWeight = FontWeight.Bold)
        }

        if (showAddReviewDialog) {
            AddReviewDialogForSchoolDetail(
                onDismiss = { showAddReviewDialog = false },
                onSubmit = { rating, comment ->
                    reviews.add(
                        ReviewForSchoolDetail(
                            reviewerId = "new_user",
                            rating = rating.coerceIn(1, 5),
                            comment = comment,
                            date = "Today"
                        )
                    )
                    showAddReviewDialog = false
                }
            )
        }
    }
}

@Composable
fun ReviewsSummaryForSchoolDetail(
    averageRating: Double,
    totalReviews: Int,
    ratingDistribution: Map<Int, Int>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFF5F8FF))
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "out of 5",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
                StarRatingForSchoolDetail(rating = averageRating.roundToInt())
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                (5 downTo 1).forEach { star ->
                    val count = ratingDistribution[star] ?: 0
                    val total = if (totalReviews == 0) 1 else totalReviews
                    val ratio = count.toFloat() / total.toFloat()

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 2.dp)
                    ) {
                        Text("$star★", fontSize = 12.sp, modifier = Modifier.width(28.dp))
                        Box(
                            modifier = Modifier
                                .width(120.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFE0E0E0))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(ratio)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFFFC107))
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("$count", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$totalReviews reviews",
            fontSize = 13.sp,
            color = Color.DarkGray
        )
    }
}

@Composable
fun StarRatingForSchoolDetail(rating: Int, maxRating: Int = 5) {
    val safeRating = rating.coerceIn(0, maxRating)
    Row {
        repeat(maxRating) { index ->
            Text(
                text = if (index < safeRating) "★" else "☆",
                fontSize = 18.sp,
                color = if (index < safeRating) Color(0xFFFFC107) else Color.Gray
            )
        }
    }
}

@Composable
fun SingleReviewItemForSchoolDetail(review: ReviewForSchoolDetail) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF7F9FF))
            .padding(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(review.reviewerId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(review.date, fontSize = 12.sp, color = Color.Gray)
            }
            StarRatingForSchoolDetail(rating = review.rating)
        }

        Spacer(modifier = Modifier.height(6.dp))

        val previewLength = 90
        val isLong = review.comment.length > previewLength
        val displayText =
            if (!expanded && isLong) review.comment.take(previewLength) + "..." else review.comment

        Text(displayText, fontSize = 14.sp)

        if (isLong) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (expanded) "Show less ▲" else "Read more ▼",
                fontSize = 13.sp,
                color = Color(0xFF3A6DFF),
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
fun AddReviewDialogForSchoolDetail(
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var ratingText by remember { mutableStateOf("5") }
    var commentText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review") },
        text = {
            Column {
                OutlinedTextField(
                    value = ratingText,
                    onValueChange = { ratingText = it },
                    label = { Text("Rating (1–5)") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    label = { Text("Comment") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val rating = ratingText.toIntOrNull() ?: 5
                if (commentText.isNotBlank()) {
                    onSubmit(rating, commentText)
                } else {
                    onDismiss()
                }
            }) {
                Text("Submit")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("Cancel")
            }
        }
    )
}

// ---------------- PREVIEW ----------------

@Preview(showBackground = true)
@Composable
fun SchoolDetailPreviewForSchoolDetail() {
    SchoolDetailsScreenForSchoolDetail()
}
