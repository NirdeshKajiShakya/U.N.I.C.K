package com.example.unick.view

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.unick.viewmodel.SchoolEditProfileViewModel

class SchoolEditProfileActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val schoolId = intent.getStringExtra("schoolId") ?: ""

        setContent {
            val vm = remember { SchoolEditProfileViewModel(applicationContext) }

            LaunchedEffect(schoolId) {
                vm.loadSchool(schoolId)
            }

            SchoolEditProfileScreen(
                vm = vm,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolEditProfileScreen(
    vm: SchoolEditProfileViewModel,
    onBack: () -> Unit
) {
    val pickBanner = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { vm.uploadBanner(it) }
    }

    val pickGallery = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) vm.uploadGalleryImages(uris)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit School Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            verticalArrangement = Arrangement.spacedBy(12.dp) // ✅ FIX: no "space ="
        ) {

            if (vm.loading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            OutlinedTextField(
                value = vm.schoolName,
                onValueChange = { vm.schoolName = it },
                label = { Text("School Name") }, // ✅ Works with proper imports
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vm.location,
                onValueChange = { vm.location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vm.email,
                onValueChange = { vm.email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vm.contactNumber,
                onValueChange = { vm.contactNumber = it },
                label = { Text("Phone") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = vm.description,
                onValueChange = { vm.description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { pickBanner.launch("image/*") }) {
                    Text("Change Banner")
                }

                Button(onClick = { pickGallery.launch("image/*") }) {
                    Text("Add Gallery Images")
                }
            }

            Button(
                onClick = { vm.saveChanges() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }

            vm.error?.let {
                Spacer(Modifier.height(6.dp))
                Text(it)
            }
        }
    }
}
