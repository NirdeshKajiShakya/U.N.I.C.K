package com.example.unick.view

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.unick.viewmodel.SchoolGalleryViewModel
import com.google.firebase.auth.FirebaseAuth

class SchoolGalleryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val schoolId = intent.getStringExtra("schoolId") ?: ""

        setContent {
            val context = LocalContext.current
            val vm = remember { SchoolGalleryViewModel(applicationContext) }

            // load images
            LaunchedEffect(schoolId) {
                if (schoolId.isBlank()) {
                    Toast.makeText(context, "School ID missing!", Toast.LENGTH_SHORT).show()
                } else {
                    vm.loadGallery(schoolId)
                }
            }

            SchoolGalleryScreen(
                vm = vm,
                schoolId = schoolId,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolGalleryScreen(
    vm: SchoolGalleryViewModel,
    schoolId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    // only owner can upload
    val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val isOwner = currentUid.isNotBlank() && currentUid == schoolId

    // Image picker
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            vm.uploadGalleryImage(
                schoolId = schoolId,
                imageUri = uri,
                onMessage = { msg ->
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("School Gallery") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            // ✅ show upload button only for school owner
            if (isOwner) {
                FloatingActionButton(
                    onClick = { pickImageLauncher.launch("image/*") }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Image")
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            when {
                vm.loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                vm.error != null -> {
                    Text(
                        text = vm.error ?: "Error",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Red
                    )
                }

                vm.galleryImageUrls.isEmpty() -> {
                    Text(
                        text = if (isOwner) "No images yet. Tap + to upload." else "No images yet.",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(vm.galleryImageUrls) { url ->
                            GalleryImageCard(url = url)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GalleryImageCard(url: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        AsyncImage(
            model = url,
            contentDescription = "Gallery Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
