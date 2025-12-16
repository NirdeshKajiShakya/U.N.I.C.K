package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsScreenForSettings()
        }
    }
}

@Composable
fun SettingsScreenForSettings() {

    var darkModeEnabled by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        item { SettingsHeaderForSettings() }

        // 🔐 ACCOUNT & SECURITY
        item { SectionTitleForSettings("Account & Security") }

        item {
            SettingsItemForSettings(
                title = "Edit Profile",
                subtitle = "Update your personal information",
                onClick = {
                    // TODO: navigate to EditUserProfileActivity
                }
            )
        }

        item {
            SettingsItemForSettings(
                title = "Change Password",
                subtitle = "Update your account password",
                onClick = {
                    // TODO: navigate to ChangePasswordActivity
                }
            )
        }

        item {
            SettingsItemForSettings(
                title = "Logout",
                subtitle = "Sign out from this account",
                onClick = {
                    // TODO: FirebaseAuth.getInstance().signOut()
                }
            )
        }

        // 🌙 PREFERENCES
        item { SectionTitleForSettings("Preferences") }

        item {
            ToggleItemForSettings(
                title = "Dark Mode",
                subtitle = "Reduce eye strain in low light",
                checked = darkModeEnabled,
                onCheckedChange = { darkModeEnabled = it }
            )
        }

        // 📜 LEGAL & SUPPORT
        item { SectionTitleForSettings("Legal & Support") }

        item {
            SettingsItemForSettings(
                title = "Privacy Policy",
                subtitle = "How we handle your data",
                onClick = {}
            )
        }

        item {
            SettingsItemForSettings(
                title = "Terms & Conditions",
                subtitle = "Rules and usage agreement",
                onClick = {}
            )
        }

        item {
            SettingsItemForSettings(
                title = "Help & Support",
                subtitle = "Get help or contact us",
                onClick = {}
            )
        }

        item {
            SettingsItemForSettings(
                title = "About App",
                subtitle = "Version 1.0.0",
                onClick = {}
            )
        }

        // ⚠️ DANGER ZONE
        item { SectionTitleForSettings("Danger Zone") }

        item {
            DangerItemForSettings(
                title = "Delete Account",
                onClick = { showDeleteDialog = true }
            )
        }
    }

    if (showDeleteDialog) {
        DeleteAccountDialogForSettings(
            onConfirm = {
                showDeleteDialog = false
                // TODO: Firebase delete user
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun SettingsHeaderForSettings() {
    Text(
        text = "Settings",
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(20.dp)
    )
}

@Composable
fun SectionTitleForSettings(title: String) {
    Text(
        text = title,
        fontSize = 14.sp,
        color = Color.Gray,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItemForSettings(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Spacer(modifier = Modifier.height(2.dp))
        Text(subtitle, fontSize = 13.sp, color = Color.Gray)
    }
}

@Composable
fun ToggleItemForSettings(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(subtitle, fontSize = 13.sp, color = Color.Gray)
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun DangerItemForSettings(
    title: String,
    onClick: () -> Unit
) {
    Text(
        text = title,
        fontSize = 16.sp,
        color = Color.Red,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
fun DeleteAccountDialogForSettings(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Account") },
        text = {
            Text("This action is permanent. Your account and data will be deleted.")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreviewForSettings() {
    SettingsScreenForSettings()
}
