package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.unick.view.ui.theme.UNICKTheme

// Data class for schools
data class SchoolDataShortlist(
    val name: String,
    val type: String,
    val distance: String,
    val rating: String,
    val match: String,
    val imageUrl: String
)

class ShortlistActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFF8F9FA)
                ) {
                    ShortlistScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortlistScreen() {
    // Sample data - replace with actual data from your ViewModel/Repository
    val shortlistedSchools = listOf(
        SchoolDataShortlist("St. Xavier's College", "Maitighar • +2 Science/A-Levels", "0.8 km", "4.9", "98% match", "https://images.unsplash.com/photo-1562774053-701939374585?w=400"),
        SchoolDataShortlist("Budhanilkantha School", "Kathmandu • National Curriculum", "5.4 km", "4.8", "95% match", "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=400"),
        SchoolDataShortlist("Ullens School", "Khumaltar • IB Diploma Program", "3.2 km", "4.7", "92% match", "https://images.unsplash.com/photo-1498243691581-b145c3f54a5a?w=400"),
        SchoolDataShortlist("Little Angels' School", "Hattiban • School to Bachelors", "4.5 km", "4.7", "90% rank", "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=400"),
        SchoolDataShortlist("Trinity International", "Dillibazar • +2 & A-Levels", "1.1 km", "4.6", "88% rank", "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400"),
        SchoolDataShortlist("Rato Bangala School", "Patan • A-Levels Center", "2.9 km", "4.8", "87% rank", "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=400"),
        SchoolDataShortlist("Gandaki Boarding", "Pokhara • National Curriculum", "200 km", "4.7", "Elite", "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400"),
        SchoolDataShortlist("Siddhartha Boarding", "Butwal • Science/Management", "260 km", "4.5", "Top Pick", "https://images.unsplash.com/photo-1571260899304-425eee4c7efc?w=400"),
        SchoolDataShortlist("SOS Hermann Gmeiner", "Pokhara • Science Streams", "198 km", "4.6", "Scholarship", "https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=400")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Perfect Matches",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${shortlistedSchools.size} institutions found",
                            fontSize = 14.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle back navigation */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF0F172A)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF8F9FA)
                )
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(shortlistedSchools) { school ->
                ShortlistSchoolCard(school)
            }
        }
    }
}

@Composable
fun ShortlistSchoolCard(school: SchoolDataShortlist) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFE2E8F0))
            ) {
                // In a real app, use AsyncImage or Coil to load from school.imageUrl
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF2563EB).copy(alpha = 0.1f),
                                    Color(0xFF2563EB).copy(alpha = 0.05f)
                                )
                            )
                        )
                )

                // Match badge
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .background(Color(0xFF2563EB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = school.match,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = school.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = school.type,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 18.sp
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📍", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = school.distance,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = school.rating,
                            fontSize = 13.sp,
                            color = Color(0xFF0F172A),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ShortlistPreview() {
    UNICKTheme {
        ShortlistScreen()
    }
}