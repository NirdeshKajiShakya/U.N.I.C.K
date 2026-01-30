package com.example.unick.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

private val PrimaryBlue = Color(0xFF4A90E2)
private val AccentRed = Color(0xFFEF4444)
private val BorderGray = Color(0xFFE5E7EB)
private val TextPrimary = Color(0xFF1A1A2E)

class SchoolSettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // ✅ receive schoolId from SchoolDetailActivity
        val schoolId = intent.getStringExtra("schoolId")
            ?: intent.getStringExtra("uid")
            ?: FirebaseAuth.getInstance().currentUser?.uid
            ?: ""

        setContent {
            val context = LocalContext.current

            SchoolSettingsScreen(
                schoolId = schoolId,
                onBack = { finish() },

                // ✅ FIX: Navigate to SchoolEditProfileActivity with schoolId
                onEditProfile = {
                    if (schoolId.isBlank()) {
                        android.widget.Toast.makeText(
                            this,
                            "School ID missing!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(
                            Intent(this, SchoolEditProfileActivity::class.java)
                                .putExtra("schoolId", schoolId)
                        )
                    }
                },

                // ✅ FIX: pass schoolId to gallery
                onGallery = {
                    if (schoolId.isBlank()) {
                        android.widget.Toast.makeText(
                            this,
                            "School ID missing!",
                            android.widget.Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        startActivity(
                            Intent(this, SchoolGalleryActivity::class.java)
                                .putExtra("schoolId", schoolId)
                        )
                    }
                },

                // ✅ Change password -> your reset activity
                onChangePassword = {
                    startActivity(Intent(this, SendCodeToEmailActivity::class.java))
                },

                // ✅ Logout -> go to welcome screen
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    val i = Intent(this, WelcomeActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(i)
                    (context as? Activity)?.finish()
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolSettingsScreen(
    schoolId: String,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onGallery: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var acceptingApplications by remember { mutableStateOf(true) }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Delete Account Confirmation Dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete School Account?") },
            text = { Text("This action cannot be undone. Your entire school account, profile, gallery, and all reviews will be permanently deleted. You will not be able to login again.") },
            confirmButton = {
                Button(
                    onClick = {
                        deleteSchoolAccount(context)
                        showDeleteAccountDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete Account")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteAccountDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BorderGray)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        topBar = {
            TopAppBar(
                title = { Text("School Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ---------- PROFILE ----------
            SectionTitle("Profile & Management")

            SettingsItem(
                text = "Edit School Profile",
                icon = Icons.Default.Edit,
                onClick = onEditProfile
            )

            SettingsItem(
                text = "Manage Gallery",
                icon = Icons.Default.PhotoLibrary,
                onClick = onGallery
            )

            Divider()

            // ---------- VISIBILITY ----------
            SectionTitle("Visibility & Applications")

            ToggleItem(
                text = "School Profile Visible",
                icon = Icons.Default.Visibility,
                checked = isVisible,
                onCheckedChange = { isVisible = it }
            )

            ToggleItem(
                text = "Accept Applications",
                icon = Icons.Default.Assignment,
                checked = acceptingApplications,
                onCheckedChange = { acceptingApplications = it }
            )

            Divider()

            // ---------- SECURITY ----------
            SectionTitle("Security")

            SettingsItem(
                text = "Change Password",
                icon = Icons.Default.Lock,
                onClick = onChangePassword
            )

            Divider()

            // ---------- INFO ----------
            SectionTitle("Legal & Info")
            SettingsItem("Privacy Policy", Icons.Default.PrivacyTip) { }
            SettingsItem("Terms & Conditions", Icons.Default.Description) { }

            Divider()

            // ---------- DANGER ZONE ----------
            SectionTitle("Danger Zone")

            SettingsItem(
                text = "Logout",
                icon = Icons.Default.Logout,
                isDanger = true,
                onClick = onLogout
            )

            SettingsItem(
                text = "Delete School Account",
                icon = Icons.Default.DeleteForever,
                isDanger = true,
                onClick = { showDeleteAccountDialog = true }
            )

            Spacer(Modifier.height(30.dp))
        }
    }
}

// ✅ DELETE SCHOOL ACCOUNT FUNCTION
private suspend fun deleteSchoolAccountAsync(schoolId: String) {
    val auth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/").reference

    try {
        // Step 1: Delete school profile from SchoolForm/{schoolId}
        database.child("SchoolForm").child(schoolId).removeValue().await()

        // Step 2: Delete gallery from school_gallery/{schoolId}
        database.child("school_gallery").child(schoolId).removeValue().await()

        // Step 3: Delete reviews from school_reviews/{schoolId}
        database.child("school_reviews").child(schoolId).removeValue().await()

        // Step 4: Delete Firebase Auth account
        auth.currentUser?.delete()?.await()

    } catch (e: Exception) {
        throw e
    }
}

private fun deleteSchoolAccount(context: android.content.Context) {
    val auth = FirebaseAuth.getInstance()
    val schoolId = auth.currentUser?.uid ?: return

    val activity = context as? ComponentActivity ?: return

    // Show loading
    Toast.makeText(context, "Deleting account...", Toast.LENGTH_SHORT).show()

    // Delete in background
    val thread = Thread {
        try {
            // Delete all data from database
            val database = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/").reference

            // Delete school profile
            database.child("SchoolForm").child(schoolId).removeValue().addOnSuccessListener {
                // Delete gallery
                database.child("school_gallery").child(schoolId).removeValue().addOnSuccessListener {
                    // Delete reviews
                    database.child("school_reviews").child(schoolId).removeValue().addOnSuccessListener {
                        // Delete auth account
                        auth.currentUser?.delete()?.addOnSuccessListener {
                            // Success
                            activity.runOnUiThread {
                                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                                val intent = Intent(context, UserLoginSchoolActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }
                                context.startActivity(intent)
                                activity.finish()
                            }
                        }?.addOnFailureListener { e ->
                            activity.runOnUiThread {
                                Toast.makeText(context, "Error deleting auth: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }?.addOnFailureListener { e ->
                        activity.runOnUiThread {
                            Toast.makeText(context, "Error deleting reviews: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }?.addOnFailureListener { e ->
                    activity.runOnUiThread {
                        Toast.makeText(context, "Error deleting gallery: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }?.addOnFailureListener { e ->
                activity.runOnUiThread {
                    Toast.makeText(context, "Error deleting school: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            activity.runOnUiThread {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    thread.start()
}


@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SettingsItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDanger: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
            Text(
                text,
                color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ToggleItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxSize(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(icon, contentDescription = null)
                Text(text)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    }
}