package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class SchoolSettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolSettingsScreen(
                onBack = { finish() },
                onEditProfile = {
                    startActivity(Intent(this, DataFormAcitivity::class.java))
                },
                onGallery = {
                    startActivity(Intent(this, SchoolGalleryActivity::class.java))
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
    onGallery: () -> Unit

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

            SettingsItem("Change Password", Icons.Default.Lock) {
                // Navigate later to reset/change password
            }

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
                isDanger = true
            ) {
                // FirebaseAuth.getInstance().signOut() later
            }
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
