package com.example.unick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.unick.R

//--------------------------------------------------------------
// 🔥 EDIT PROFILE ACTIVITY
//--------------------------------------------------------------
class EditUserProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EditUserProfileScreen()
        }
    }
}

//--------------------------------------------------------------
// 🔥 MAIN SCREEN
//--------------------------------------------------------------
@Composable
fun EditUserProfileScreen() {

    // Editable user states
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    // Preferences
    var classPref by remember { mutableStateOf("Class") }
    var levelPref by remember { mutableStateOf("Level") }
    var typePref by remember { mutableStateOf("Type") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {

        item { EditProfileHeaderSectionForEditUserProfile(onBackClick = {}) }

        item {
            EditProfileUserCardSectionForEditUserProfile(
                name = fullName.ifEmpty { "User Name" },
                email = email.ifEmpty { "user123@gmail.com" }
            )
        }

        item { EditFieldItemForEditUserProfile("Full Name", fullName) { fullName = it } }
        item { EditFieldItemForEditUserProfile("Email", email) { email = it } }
        item { EditFieldItemForEditUserProfile("Contact", contact) { contact = it } }
        item { EditFieldItemForEditUserProfile("Date Of Birth", dob) { dob = it } }
        item { EditFieldItemForEditUserProfile("Gender", gender) { gender = it } }
        item { EditFieldItemForEditUserProfile("Location", location) { location = it } }

        item {
            PreferencesSectionForEditUserProfile(
                classPref = classPref,
                levelPref = levelPref,
                typePref = typePref,
                onClassClick = {},
                onLevelClick = {},
                onTypeClick = {},
                onEditPrefClick = {}
            )
        }

        item {
            EditActionsButtonsForEditUserProfile(
                onDelete = {},
                onSave = {}
            )
        }

        item { Spacer(modifier = Modifier.height(40.dp)) }
    }
}

//--------------------------------------------------------------
// 🔥 COMPONENTS BELOW
//--------------------------------------------------------------

@Composable
fun EditProfileHeaderSectionForEditUserProfile(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(28.dp).clickable { onBackClick() }
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text("My Profile", fontSize = 26.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EditProfileUserCardSectionForEditUserProfile(name: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F8FC))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color(0xFFE7F2FF)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(id = R.drawable.school_profile),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        Column {
            Text(name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(email, fontSize = 16.sp, color = Color.Gray)
        }
    }
}

@Composable
fun EditFieldItemForEditUserProfile(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

@Composable
fun PreferencesSectionForEditUserProfile(
    classPref: String,
    levelPref: String,
    typePref: String,
    onClassClick: () -> Unit,
    onLevelClick: () -> Unit,
    onTypeClick: () -> Unit,
    onEditPrefClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFFF5F8FC))
            .padding(20.dp)
    ) {

        Text("Your preferences", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(10.dp))

        Row {
            PrefChipForEditUserProfile(classPref, onClassClick)
            Spacer(Modifier.width(10.dp))
            PrefChipForEditUserProfile(levelPref, onLevelClick)
            Spacer(Modifier.width(10.dp))
            PrefChipForEditUserProfile(typePref, onTypeClick)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Edit Preferences",
            color = Color(0xFF3A6DFF),
            modifier = Modifier.clickable { onEditPrefClick() }
        )
    }
}

@Composable
fun PrefChipForEditUserProfile(text: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF7DCDC))
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun EditActionsButtonsForEditUserProfile(onDelete: () -> Unit, onSave: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFFFF4C4C))
                .clickable { onDelete() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Delete Account", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.width(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xFF365CFF))
                .clickable { onSave() }
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

// Preview
@Preview(showBackground = true)
@Composable
fun PreviewEditUserProfileScreen() {
    EditUserProfileScreen()
}
