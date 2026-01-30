package com.example.unick.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.unick.R
import com.example.unick.repo.EditProfileRepoImpl
import com.example.unick.viewmodel.EditUserProfileViewModel

// Colors
private val PrimaryBlue = Color(0xFF4A90E2)
private val PrimaryBlueDark = Color(0xFF357ABD)
private val BackgroundGray = Color(0xFFF8FAFC)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1A2E)
private val TextSecondary = Color(0xFF6B7280)
private val AccentRed = Color(0xFFEF4444)
private val BorderGray = Color(0xFFE5E7EB)

class EditUserProfileActivity : ComponentActivity() {
    private val viewModel: EditUserProfileViewModel by viewModels {
        EditUserProfileViewModel.Factory(EditProfileRepoImpl(), applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditUserProfileScreen(
                viewModel = viewModel,
                onBackClick = { finish() },
                onDeleteSuccess = { finish() }
            )
        }
    }
}

@Composable
fun EditUserProfileScreen(
    viewModel: EditUserProfileViewModel,
    onBackClick: () -> Unit = {},
    onDeleteSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.uploadProfilePicture(it) }
    }

    LaunchedEffect(uiState.saveSuccess, uiState.deleteSuccess, uiState.errorMessage) {
        uiState.saveSuccess?.let { success ->
            val message = if (success) "Profile updated successfully!" else uiState.errorMessage ?: "Failed to update"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearStatus()
        }
        uiState.deleteSuccess?.let { success ->
            if (success) {
                Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                onDeleteSuccess()
            } else {
                Toast.makeText(context, uiState.errorMessage ?: "Failed to delete", Toast.LENGTH_SHORT).show()
            }
            viewModel.clearStatus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryBlue
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.systemBars),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                item { EditProfileHeader(onBackClick = onBackClick) }
                item {
                    ProfileCard(
                        name = uiState.profile.fullName.ifEmpty { "User Name" },
                        email = uiState.profile.email.ifEmpty { "user@gmail.com" },
                        profilePictureUrl = uiState.profile.profilePictureUrl,
                        isUploadingImage = uiState.isUploadingImage,
                        onChangePhoto = { imagePickerLauncher.launch("image/*") }
                    )
                }
                item { PersonalInfoSection(
                    fullName = uiState.profile.fullName,
                    email = uiState.profile.email,
                    contact = uiState.profile.contact,
                    dob = uiState.profile.dob,
                    gender = uiState.profile.gender,
                    location = uiState.profile.location,
                    onFieldChange = { field, value -> viewModel.updateField(field, value) }
                )}
                item { ActionButtons(
                    onDelete = { viewModel.deleteAccount() },
                    onSave = { viewModel.saveUserProfile() }
                )}
            }
        }
    }
}

@Composable
fun EditProfileHeader(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(PrimaryBlue, PrimaryBlueDark)
                )
            )
            .padding(top = 16.dp, bottom = 80.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Edit Profile",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ProfileCard(
    name: String,
    email: String,
    profilePictureUrl: String,
    isUploadingImage: Boolean,
    onChangePhoto: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-50).dp)
            .shadow(8.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFFE0E7FF), Color(0xFFC7D2FE))
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (isUploadingImage) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp),
                        color = PrimaryBlue
                    )
                } else if (profilePictureUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profilePictureUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painterResource(id = R.drawable.school_profile),
                        contentDescription = null,
                        modifier = Modifier.size(50.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                name,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                email,
                fontSize = 14.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onChangePhoto) {
                Icon(
                    Icons.Outlined.CameraAlt,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Change Photo", color = PrimaryBlue, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
fun PersonalInfoSection(
    fullName: String,
    email: String,
    contact: String,
    dob: String,
    gender: String,
    location: String,
    onFieldChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .offset(y = (-30).dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "Personal Information",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(16.dp))

            StyledTextField(
                value = fullName,
                onValueChange = { onFieldChange("fullName", it) },
                label = "Full Name",
                icon = Icons.Outlined.Person
            )
            StyledTextField(
                value = email,
                onValueChange = { onFieldChange("email", it) },
                label = "Email Address",
                icon = Icons.Outlined.Email
            )
            StyledTextField(
                value = contact,
                onValueChange = { onFieldChange("contact", it) },
                label = "Phone Number",
                icon = Icons.Outlined.Phone
            )
            StyledTextField(
                value = dob,
                onValueChange = { onFieldChange("dob", it) },
                label = "Date of Birth",
                icon = Icons.Outlined.CalendarToday
            )
            StyledTextField(
                value = gender,
                onValueChange = { onFieldChange("gender", it) },
                label = "Gender",
                icon = Icons.Outlined.Person
            )
            StyledTextField(
                value = location,
                onValueChange = { onFieldChange("location", it) },
                label = "Location",
                icon = Icons.Outlined.LocationOn,
                isLast = true
            )
        }
    }
}

@Composable
fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    isLast: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(icon, contentDescription = null, tint = PrimaryBlue)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = if (isLast) 0.dp else 12.dp),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = BorderGray,
            focusedLabelColor = PrimaryBlue,
            cursorColor = PrimaryBlue
        ),
        singleLine = true
    )
}


@Composable
fun ActionButtons(onDelete: () -> Unit, onSave: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account?") },
            text = { Text("This action cannot be undone. All your data will be permanently deleted.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = BorderGray)
                ) {
                    Text("Cancel", color = TextPrimary)
                }
            }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccentRed)
        ) {
            Icon(Icons.Outlined.DeleteOutline, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Delete", fontWeight = FontWeight.SemiBold)
        }

        Button(
            onClick = onSave,
            modifier = Modifier
                .weight(1f)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            Icon(Icons.Outlined.Check, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewEditUserProfileScreen() {
    Box(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        LazyColumn {
            item { EditProfileHeader(onBackClick = {}) }
            item { ProfileCard("John Doe", "john@email.com", "", false, {}) }
        }
    }
}