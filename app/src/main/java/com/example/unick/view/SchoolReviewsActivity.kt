package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.model.SchoolReviewModel
import com.example.unick.viewmodel.SchoolDetailViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Locale

class SchoolReviewsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val schoolId = intent.getStringExtra("schoolId") ?: ""
        val schoolName = intent.getStringExtra("schoolName") ?: "School"

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

            SchoolReviewsScreen(
                vm = vm,
                schoolId = schoolId,
                schoolName = schoolName,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolReviewsScreen(
    vm: SchoolDetailViewModel,
    @Suppress("UNUSED_PARAMETER") schoolId: String,
    schoolName: String,
    onBack: () -> Unit
) {
    val reviews = vm.reviews
    val avgRating = vm.avgRating
    val totalReviews = vm.totalReviews

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reviews",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF3B82F6),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header with school name and rating summary
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = schoolName,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = String.format(Locale.US, "%.1f", avgRating),
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF3B82F6)
                        )

                        Spacer(Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StarsRow(rating = avgRating)
                        }

                        Spacer(Modifier.height(8.dp))

                        Text(
                            text = "Based on $totalReviews ${if (totalReviews == 1) "review" else "reviews"}",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Review composer
            item {
                ReviewComposerSection(
                    onSubmit = { rating, comment ->
                        val currentUser = FirebaseAuth.getInstance().currentUser
                        if (currentUser != null) {
                            vm.submitReview(
                                reviewerUid = currentUser.uid,
                                rating = rating,
                                comment = comment
                            )
                        } else {
                            android.widget.Toast.makeText(
                                null,
                                "Please login to submit a review",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
            }

            // Reviews list header
            item {
                Text(
                    text = if (reviews.isEmpty()) "No Reviews Yet" else "All Reviews",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = Color(0xFF1E293B)
                )
            }

            // Reviews list
            if (reviews.isEmpty() && !vm.loading) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🌟",
                                fontSize = 48.sp
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = "Be the first to review!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                items(reviews) { review ->
                    ReviewCardDetailed(review = review)
                }
            }

            // Loading indicator
            if (vm.loading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color(0xFF3B82F6)
                        )
                    }
                }
            }

            // Error message
            vm.error?.let { errorMsg ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFEE2E2)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = errorMsg,
                            color = Color(0xFFDC2626),
                            modifier = Modifier.padding(16.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Bottom spacing
            item {
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun ReviewComposerSection(onSubmit: (rating: Int, comment: String) -> Unit) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(
                "Write Your Review",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF1E293B)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Your Rating",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                (1..5).forEach { star ->
                    val filled = star <= rating
                    Icon(
                        imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = null,
                        tint = if (filled) Color(0xFFFBBF24) else Color(0xFFD1D5DB),
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { if (!isSubmitting) rating = star }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { if (!isSubmitting) comment = it },
                label = { Text("Share your experience") },
                placeholder = { Text("What did you think about this school?") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                enabled = !isSubmitting,
                maxLines = 6,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    if (comment.isNotBlank() && !isSubmitting) {
                        isSubmitting = true
                        onSubmit(rating, comment)
                        // Reset form after submission
                        comment = ""
                        rating = 5
                        isSubmitting = false
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    disabledContainerColor = Color(0xFFD1D5DB)
                ),
                enabled = !isSubmitting && comment.isNotBlank()
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("Submitting...", fontWeight = FontWeight.SemiBold)
                } else {
                    Text("Submit Review", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    }
}

@Composable
private fun ReviewCardDetailed(review: SchoolReviewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reviewer info
                Column(modifier = Modifier.weight(1f)) {
                    ReviewerName(review.reviewerUid)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = formatReviewDate(review.createdAt),
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }

                // Rating badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFEF3C7)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${review.rating}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Review comment
            val text = review.comment
            val shouldTruncate = text.length > 200

            Text(
                text = if (expanded || !shouldTruncate) text else text.take(200) + "...",
                color = Color(0xFF475569),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )

            if (shouldTruncate) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (expanded) "Show less" else "Read more",
                    color = Color(0xFF3B82F6),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            }
        }
    }
}

@Composable
private fun StarsRow(rating: Double) {
    val full = rating.toInt().coerceIn(0, 5)
    val empty = (5 - full).coerceIn(0, 5)

    Row {
        repeat(full) {
            Icon(
                Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFBBF24),
                modifier = Modifier.size(24.dp)
            )
        }
        repeat(empty) {
            Icon(
                Icons.Outlined.Star,
                contentDescription = null,
                tint = Color(0xFFD1D5DB),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ReviewerName(reviewerUid: String) {
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
        text = name ?: "Loading...",
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        color = Color(0xFF1E293B)
    )
}

private fun formatReviewDate(timestamp: Long): String {
    if (timestamp == 0L) return "Recently"

    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60000 -> "Just now"
        diff < 3600000 -> "${diff / 60000} minutes ago"
        diff < 86400000 -> "${diff / 3600000} hours ago"
        diff < 604800000 -> "${diff / 86400000} days ago"
        else -> {
            val date = java.util.Date(timestamp)
            java.text.SimpleDateFormat("MMM dd, yyyy", Locale.US).format(date)
        }
    }
}
