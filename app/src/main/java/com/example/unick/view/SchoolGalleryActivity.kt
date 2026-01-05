package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.unick.viewmodel.SchoolGalleryViewModel

class SchoolGalleryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val schoolId = intent.getStringExtra("schoolId") ?: ""

        setContent {
            val vm = remember { SchoolGalleryViewModel(applicationContext) }

            LaunchedEffect(schoolId) {
                vm.loadGallery(schoolId)
            }

            SchoolGalleryScreen(
                vm = vm,
                onBack = { finish() }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchoolGalleryScreen(
    vm: SchoolGalleryViewModel,
    onBack: () -> Unit
) {
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
                        text = "No images yet.",
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
