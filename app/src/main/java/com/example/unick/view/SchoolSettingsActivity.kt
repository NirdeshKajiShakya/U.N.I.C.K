package com.example.unick.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

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
                onBack = { finish() },

                onEditProfile = {
                    startActivity(Intent(this, DataFormAcitivity::class.java))
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

                // ✅ Logout -> go to school login screen
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    val i = Intent(this, UserLoginSchoolActivity::class.java).apply {
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
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onGallery: () -> Unit,
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    var isVisible by remember { mutableStateOf(true) }
    var acceptingApplications by remember { mutableStateOf(true) }

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
                .verticalScroll(rememberScrollState())   // ✅ ADD THIS
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

            // ---------- LOGOUT ----------
            SettingsItem(
                text = "Logout",
                icon = Icons.Default.Logout,
                isDanger = true,
                onClick = onLogout
            )

            Spacer(Modifier.height(30.dp)) // ✅ extra space at bottom
        }
    }
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
        modifier = Modifier.fillMaxWidth(),
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
