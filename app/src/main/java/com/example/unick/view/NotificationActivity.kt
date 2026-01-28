package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.view.ui.theme.UNICKTheme
import com.example.unick.viewmodel.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

data class Notification(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timestamp: String = "",
    val isRead: Boolean = false,
    // Type: "student" for student notifications, "school" for school notifications
    val type: String = "student",
    // Application ID for navigation (used in school notifications)
    val applicationId: String = "",
    // School ID for context (used in student notifications)
    val schoolId: String = "",
    // Student name for school notifications
    val studentName: String = ""
)

class NotificationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                NotificationScreen(
                    onBackClick = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(onBackClick: () -> Unit = {}) {
    var notifications by remember { mutableStateOf<List<Notification>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var userType by remember { mutableStateOf<UserType>(UserType.Unknown) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val database = FirebaseDatabase.getInstance()
    val context = LocalContext.current

    // Detect user type (student vs school)
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            val usersRef = database.reference.child("Users")
            val schoolsRef = database.reference.child("schools")

            usersRef.child(userId).get()
                .addOnSuccessListener { snapshot ->
                    if (snapshot.exists()) {
                        userType = UserType.Normal
                    } else {
                        // If not in Users, check in schools
                        schoolsRef.child(userId).get()
                            .addOnSuccessListener { schoolSnapshot ->
                                if (schoolSnapshot.exists()) {
                                    userType = UserType.School
                                } else {
                                    userType = UserType.Unknown
                                }
                            }
                    }
                }
        }
    }

    // Fetch notifications from Firebase
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            database.reference
                .child("notifications")
                .child(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val notificationsList = mutableListOf<Notification>()
                    snapshot.children.forEach { child ->
                        val notification = child.getValue(Notification::class.java)
                        if (notification != null) {
                            notificationsList.add(notification)
                        }
                    }
                    notificationsList.sortByDescending { it.timestamp }
                    notifications = notificationsList
                    isLoading = false
                }
                .addOnFailureListener {
                    isLoading = false
                }
        }
    }

    // Filter notifications based on user type
    val filteredNotifications = when (userType) {
        is UserType.School -> notifications.filter { it.type == "school" }
        is UserType.Normal -> notifications.filter { it.type == "student" }
        else -> notifications
    }

    val unreadCount = filteredNotifications.count { !it.isRead }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        if (unreadCount > 0) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE53935)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2563EB),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFFF0F4FF), Color(0xFFFFFFFF))
                    )
                )
                .padding(innerPadding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB).copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Loading notifications...")
                    }
                }
                filteredNotifications.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔔", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No notifications yet",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(filteredNotifications, key = { it.id }) { notification ->
                            NotificationCard(
                                notification = notification,
                                isSchoolNotification = userType is UserType.School,
                                onMarkAsRead = { readNotification ->
                                    // Update in Firebase
                                    database.reference
                                        .child("notifications")
                                        .child(userId)
                                        .child(readNotification.id)
                                        .child("isRead")
                                        .setValue(true)

                                    // Update locally
                                    notifications = notifications.map {
                                        if (it.id == readNotification.id) it.copy(isRead = true) else it
                                    }
                                },
                                onNavigate = { notification ->
                                    // Navigate to ViewApplicationActivity for school notifications
                                    if (userType is UserType.School && notification.applicationId.isNotEmpty()) {
                                        val intent = Intent(context, ViewApplicationActivity::class.java).apply {
                                            putExtra("schoolId", userId)
                                            putExtra("applicationId", notification.applicationId)
                                        }
                                        context.startActivity(intent)
                                    }
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    notification: Notification,
    isSchoolNotification: Boolean = false,
    onMarkAsRead: (Notification) -> Unit = {},
    onNavigate: (Notification) -> Unit = {}
) {
    val cardColor = if (notification.isRead) Color(0xFFF5F5F5) else Color(0xFFE3F2FD)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                if (!notification.isRead) {
                    onMarkAsRead(notification)
                }
                // Navigate for school notifications
                if (isSchoolNotification && notification.applicationId.isNotEmpty()) {
                    onNavigate(notification)
                }
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (notification.isRead) 2.dp else 6.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            if (!notification.isRead) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB))
                )
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) FontWeight.Normal else FontWeight.Bold,
                    color = if (notification.isRead) Color(0xFF757575) else Color(0xFF1E88E5)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notification.isRead) Color(0xFF9E9E9E) else Color(0xFF424242)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "🕐 ${notification.timestamp}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF757575)
                )
            }

            IconButton(
                onClick = { if (!notification.isRead) onMarkAsRead(notification) },
                enabled = !notification.isRead
            ) {
                Icon(
                    imageVector = if (notification.isRead) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                    contentDescription = if (notification.isRead) "Read" else "Mark as Read",
                    tint = if (notification.isRead) Color(0xFF4CAF50) else Color(0xFF9E9E9E),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    UNICKTheme {
        NotificationScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationActivityPreview() {
    UNICKTheme {
        NotificationScreen()
    }
}

