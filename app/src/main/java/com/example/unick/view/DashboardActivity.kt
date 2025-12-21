package com.example.unick.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.unick.ui.theme.UNICKTheme

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UNICKTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen()
                }
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    var isSearchActive by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Namaste,",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Student",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .size(52.dp)
                    .shadow(4.dp, CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "User",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Explore the best colleges in Nepal tailored to your SEE/SLC results and interests.",
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Search Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isSearchActive) {
                Button(
                    onClick = { isSearchActive = true },
                    modifier = Modifier
                        .weight(1f)
                        .height(58.dp)
                        .shadow(8.dp, RoundedCornerShape(14.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Search, "Search", modifier = Modifier.size(22.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Search Colleges", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            } else {
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .weight(1f)
                        .shadow(4.dp, RoundedCornerShape(14.dp)),
                    placeholder = { Text("e.g. St. Xavier's, Budhanilkantha...", color = Color(0xFF94A3B8)) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color(0xFF2563EB)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2563EB),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Filter Button
            OutlinedButton(
                onClick = {
                    context.startActivity(Intent(context, ShortlistActivity::class.java))
                },
                modifier = Modifier
                    .size(58.dp)
                    .shadow(4.dp, RoundedCornerShape(14.dp)),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 2.dp,
                    brush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF2563EB)))
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text("🎯", fontSize = 24.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Compare Button
        OutlinedButton(
            onClick = { /* Handle compare */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .shadow(4.dp, RoundedCornerShape(14.dp)),
            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color.White),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                width = 2.dp,
                brush = Brush.linearGradient(listOf(Color(0xFF2563EB), Color(0xFF2563EB)))
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("📊", fontSize = 22.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Text("Compare Academic Programs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Perfect Matches Section
        SchoolSection(
            title = "Perfect Matches",
            subtitle = "Institutions matching your academic profile",
            schools = listOf(
                SchoolData("St. Xavier's College", "Maitighar • +2 Science/A-Levels", "0.8 km", "4.9", "98% match", "https://images.unsplash.com/photo-1562774053-701939374585?w=400"),
                SchoolData("Budhanilkantha School", "Kathmandu • National Curriculum", "5.4 km", "4.8", "95% match", "https://images.unsplash.com/photo-1541339907198-e08756dedf3f?w=400"),
                SchoolData("Ullens School", "Khumaltar • IB Diploma Program", "3.2 km", "4.7", "92% match", "https://images.unsplash.com/photo-1498243691581-b145c3f54a5a?w=400")
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Top Rated in Kathmandu Section
        SchoolSection(
            title = "Top Rated in Kathmandu",
            subtitle = "Highest academic standing in the capital",
            schools = listOf(
                SchoolData("Little Angels' School", "Hattiban • School to Bachelors", "4.5 km", "4.7", "90% rank", "https://images.unsplash.com/photo-1509062522246-3755977927d7?w=400"),
                SchoolData("Trinity International", "Dillibazar • +2 & A-Levels", "1.1 km", "4.6", "88% rank", "https://images.unsplash.com/photo-1523050854058-8df90110c9f1?w=400"),
                SchoolData("Rato Bangala School", "Patan • A-Levels Center", "2.9 km", "4.8", "87% rank", "https://images.unsplash.com/photo-1546410531-bb4caa6b424d?w=400")
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Regional Leaders Section
        SchoolSection(
            title = "Regional Leaders",
            subtitle = "Top schools outside the capital valley",
            schools = listOf(
                SchoolData("Gandaki Boarding", "Pokhara • National Curriculum", "200 km", "4.7", "Elite", "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=400"),
                SchoolData("Siddhartha Boarding", "Butwal • Science/Management", "260 km", "4.5", "Top Pick", "https://images.unsplash.com/photo-1571260899304-425eee4c7efc?w=400"),
                SchoolData("SOS Hermann Gmeiner", "Pokhara • Science Streams", "198 km", "4.6", "Scholarship", "https://images.unsplash.com/photo-1580582932707-520aed937b7b?w=400")
            )
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
fun SchoolSection(title: String, subtitle: String, schools: List<SchoolData>) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 20.sp
                )
            }
            TextButton(onClick = { }) {
                Text(
                    "View all →",
                    color = Color(0xFF2563EB),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(schools) { school -> SchoolCard(school) }
        }
    }
}

@Composable
fun SchoolCard(school: SchoolData) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .shadow(8.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                AsyncImage(
                    model = school.imageUrl,
                    contentDescription = school.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
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
                    .padding(16.dp)
            ) {
                Text(
                    text = school.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = school.details,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📍", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = school.distance,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "⭐", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = school.rating,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

data class SchoolData(
    val name: String,
    val details: String,
    val distance: String,
    val rating: String,
    val match: String,
    val imageUrl: String
)

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    UNICKTheme {
        DashboardScreen()
    }
}
