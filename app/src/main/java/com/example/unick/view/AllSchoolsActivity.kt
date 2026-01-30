package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.ui.theme.UNICKTheme
import com.example.unick.viewmodel.SchoolViewModel

class AllSchoolsActivity : ComponentActivity() {
    private val viewModel: SchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                AllSchoolsScreen(viewModel = viewModel, onBack = { finish() })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllSchoolsScreen(viewModel: SchoolViewModel, onBack: () -> Unit) {
    val schools by viewModel.schools.collectAsState()
    val isLoading by viewModel.isLoadingSchools.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.fetchSchools()
    }

    val verifiedSchools = remember(schools) { schools.filter { it.verified } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Schools", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2563EB)
                )
            } else if (verifiedSchools.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No schools found.", color = Color(0xFF64748B))
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(verifiedSchools) { school ->
                        // Reusing existing SchoolCard but putting it in a vertical list
                        // Note: SchoolCard might be designed for horizontal width, 
                        // but usually Cards adapt to width or have fixed width. 
                        // If it has fixed width, it might look small in vertical list.
                        // However, strictly following request: "displayed properly like how its in Dasboad 
                        // but not like in Dashboard right now it has the cards shown in a horizontal way"
                        // Implies vertical list.
                        
                        SchoolCard(
                            school = school,
                            context = context,
                            onClick = {
                                val intent = Intent(context, DashboardCard::class.java)
                                intent.putExtra("school_details", school)
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}
