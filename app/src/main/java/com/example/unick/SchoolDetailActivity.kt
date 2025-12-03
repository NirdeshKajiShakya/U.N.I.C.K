package com.example.unick

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class SchoolDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SchoolDetailsScreen()
        }
    }
}

@Composable
fun SchoolDetailsScreen() {

    var selectedTab by remember { mutableStateOf("Overview") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // -------------------------- TOP BAR --------------------------
        // -------------------------- TOP BAR REMOVED --------------------------
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color(0xFFDDE8FF))     // keep the same background colour
        )


        // -------------------------- BANNER IMAGE --------------------------
        Image(
            painter = painterResource(id = R.drawable.school_banner),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )

        // -------------------------- SCHOOL NAME + BIO --------------------------
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // School Logo (circular)
            Image(
                painter = painterResource(id = R.drawable.school_logo),  // <-- put your logo here
                contentDescription = "School Logo",
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text("School Name", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text("School Bio", fontSize = 15.sp)
            }
        }


        // -------------------------- TAB ROW --------------------------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            TabButton("Overview", selectedTab) { selectedTab = "Overview" }
            TabButton("Academics", selectedTab) { selectedTab = "Academics" }
            TabButton("Connect", selectedTab) { selectedTab = "Connect" }
            TabButton("Reviews", selectedTab) { selectedTab = "Reviews" }
        }

        Divider(color = Color.LightGray, thickness = 1.dp)

        // -------------------------- CONTENT BASED ON TAB --------------------------
        when (selectedTab) {
            "Overview" -> OverviewSection()
            "Academics" -> SimpleColorPage(Color(0xFFFFE0B2), "Academics Page")
            "Connect" -> SimpleColorPage(Color(0xFFBBDEFB), "Connect Page")
            "Reviews" -> SimpleColorPage(Color(0xFFC8E6C9), "Reviews Page")
        }
    }
}

@Composable
fun TabButton(text: String, selected: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text,
            fontSize = 15.sp,
            fontWeight = if (selected == text) FontWeight.Bold else FontWeight.Normal
        )

        if (selected == text) {
            Box(
                modifier = Modifier
                    .width(50.dp)
                    .height(3.dp)
                    .background(Color.Black)
            )
        }
    }
}

@Composable
fun OverviewSection() {
    Column(modifier = Modifier.padding(16.dp)) {

        DropSection("About")
        DropSection("Available Programs")
        DropSection("Graduate Students")
        DropSection("Teachers / Professors")
        DropSection("Scholarship")
    }
}

@Composable
fun DropSection(title: String) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Icon(
            imageVector = Icons.Default.ArrowDropDown,
            contentDescription = null,
            modifier = Modifier.size(30.dp)
        )
    }

    if (expanded) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(16.dp)
        ) {
            Text("Content for $title")
        }
    }

    Divider()
}

@Composable
fun SimpleColorPage(bg: Color, text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(bg)
            .padding(20.dp),
        contentAlignment = Alignment.TopStart
    ) {
        Text(text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
    }
}

@Preview
@Composable
fun SchoolDetailPreview(){
    SchoolDetailsScreen()
}
