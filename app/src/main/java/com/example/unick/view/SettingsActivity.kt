package com.example.unick.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Colors
private val PrimaryBlue = Color(0xFF4A90E2)
private val PrimaryBlueDark = Color(0xFF357ABD)
private val BackgroundGray = Color(0xFFF8FAFC)
private val BackgroundDark = Color(0xFF121212)
private val CardWhite = Color(0xFFFFFFFF)
private val CardDark = Color(0xFF1E1E1E)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextPrimaryDark = Color(0xFFE0E0E0)
private val TextSecondary = Color(0xFF6B7280)
private val TextSecondaryDark = Color(0xFF9E9E9E)
private val AccentRed = Color(0xFFEF4444)
private val ChipBackground = Color(0xFFEEF2FF)
private val ChipText = Color(0xFF4338CA)

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SettingsScreenForSettings(onBackClick = { finish() })
        }
    }
}

// Helper to get/save dark mode preference
fun getDarkModePref(context: Context): Boolean {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return prefs.getBoolean("dark_mode", false)
}

fun saveDarkModePref(context: Context, enabled: Boolean) {
    val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    prefs.edit().putBoolean("dark_mode", enabled).apply()
}

@Composable
fun SettingsScreenForSettings(onBackClick: () -> Unit = {}) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var darkModeEnabled by remember { mutableStateOf(getDarkModePref(context)) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var isDeleting by remember { mutableStateOf(false) }

    val backgroundColor = if (darkModeEnabled) BackgroundDark else BackgroundGray
    val cardColor = if (darkModeEnabled) CardDark else CardWhite
    val textPrimary = if (darkModeEnabled) TextPrimaryDark else TextPrimary
    val textSecondary = if (darkModeEnabled) TextSecondaryDark else TextSecondary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.systemBars),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            item { SettingsHeaderForSettings(onBackClick, darkModeEnabled) }

            // 🔐 ACCOUNT & SECURITY
            item { SectionTitleForSettings("Account & Security", textSecondary) }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.Person,
                    title = "Edit Profile",
                    subtitle = "Update your personal information",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {
                        val intent = Intent(context, EditUserProfileActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.Lock,
                    title = "Change Password",
                    subtitle = "Update your account password",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {
                        val intent = Intent(context, SendCodeToEmailActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }

            item {
                SettingsItemForSettings(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    title = "Logout",
                    subtitle = "Sign out from this account",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = { showLogoutDialog = true }
                )
            }

            // 🌙 PREFERENCES
            item { SectionTitleForSettings("Preferences", textSecondary) }

            item {
                ToggleItemForSettings(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Reduce eye strain in low light",
                    checked = darkModeEnabled,
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onCheckedChange = {
                        darkModeEnabled = it
                        saveDarkModePref(context, it)
                    }
                )
            }

            // 📜 LEGAL & SUPPORT
            item { SectionTitleForSettings("Legal & Support", textSecondary) }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.PrivacyTip,
                    title = "Privacy Policy",
                    subtitle = "How we handle your data",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {}
                )
            }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.Description,
                    title = "Terms & Conditions",
                    subtitle = "Rules and usage agreement",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {}
                )
            }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.Help,
                    title = "Help & Support",
                    subtitle = "Get help or contact us",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {}
                )
            }

            item {
                SettingsItemForSettings(
                    icon = Icons.Outlined.Info,
                    title = "About App",
                    subtitle = "Version 1.0.0",
                    cardColor = cardColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    onClick = {}
                )
            }

            // ⚠️ DANGER ZONE
            item { SectionTitleForSettings("Danger Zone", AccentRed) }

            item {
                DangerItemForSettings(
                    title = "Delete Account",
                    subtitle = "Permanently delete your account and data",
                    onClick = { showDeleteDialog = true }
                )
            }
        }

        // Loading overlay when deleting
        if (isDeleting) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    }

    // Logout Dialog
    if (showLogoutDialog) {
        LogoutDialogForSettings(
            onConfirm = {
                showLogoutDialog = false
                FirebaseAuth.getInstance().signOut()
                Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(context, UserLoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        DeleteAccountDialogForSettings(
            onConfirm = {
                showDeleteDialog = false
                isDeleting = true

                scope.launch {
                    try {
                        val user = FirebaseAuth.getInstance().currentUser
                        val userId = user?.uid

                        // Delete user data from database
                        if (userId != null) {
                            val database = FirebaseDatabase.getInstance("https://vidyakhoj-927fb-default-rtdb.firebaseio.com/")
                            database.getReference("Users").child(userId).removeValue().await()
                        }

                        // Delete Firebase Auth user
                        user?.delete()?.await()

                        isDeleting = false
                        Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                        val intent = Intent(context, UserLoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        isDeleting = false
                        Toast.makeText(context, "Failed to delete account: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
fun SettingsHeaderForSettings(onBackClick: () -> Unit, isDarkMode: Boolean) {
    val textColor = if (isDarkMode) TextPrimaryDark else TextPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryBlueDark)
                )
            )
            .padding(top = 16.dp, bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onBackClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Settings",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun SectionTitleForSettings(title: String, textColor: Color = TextSecondary) {
    Text(
        text = title,
        fontSize = 13.sp,
        color = textColor,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)
    )
}

@Composable
fun SettingsItemForSettings(
    icon: ImageVector,
    title: String,
    subtitle: String,
    cardColor: Color = CardWhite,
    textPrimary: Color = TextPrimary,
    textSecondary: Color = TextSecondary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChipBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = ChipText,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = textSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun ToggleItemForSettings(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    cardColor: Color = CardWhite,
    textPrimary: Color = TextPrimary,
    textSecondary: Color = TextSecondary,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(ChipBackground),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = ChipText,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = PrimaryBlue,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        }
    }
}

@Composable
fun DangerItemForSettings(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AccentRed.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AccentRed.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = AccentRed
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = AccentRed.copy(alpha = 0.7f)
                )
            }
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = AccentRed,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun LogoutDialogForSettings(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = CardWhite,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                "Logout",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )
        },
        text = {
            Text(
                "Are you sure you want to log out of your account?",
                fontSize = 14.sp,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Logout", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = TextPrimary)
            }
        }
    )
}

@Composable
fun DeleteAccountDialogForSettings(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = CardWhite,
        icon = {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(AccentRed.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Warning,
                    contentDescription = null,
                    tint = AccentRed,
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        title = {
            Text(
                "Delete Account",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = TextPrimary
            )
        },
        text = {
            Text(
                "This action is permanent and cannot be undone. All your data, applications, and shortlisted schools will be permanently deleted.",
                fontSize = 14.sp,
                color = TextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
            ) {
                Text("Delete Account", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cancel", color = TextPrimary)
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun SettingsPreviewForSettings() {
    SettingsScreenForSettings()
}
