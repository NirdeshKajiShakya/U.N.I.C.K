package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//BOTTOM NAVIGATION ITEMS
sealed class BottomNavItem(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomNavItem("Home", Icons.Default.Home)
    object Profile : BottomNavItem("Profile", Icons.Default.Person)
    object Search : BottomNavItem("Search", Icons.Default.Search)
    object AIChat : BottomNavItem("AI Chat", Icons.Default.Chat)
    object Notification : BottomNavItem("Notification", Icons.Default.Notifications)
}

//MAIN ACTIVITY
class BottomBarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BottomBarScreen()
        }
    }
}

//MAIN SCREEN WITH BOTTOM NAV
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBarScreen() {

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.AIChat,
        BottomNavItem.Notification,
        BottomNavItem.Profile
    )

    var selectedIndex by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                navItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = {
                            Text(
                                item.title,
                                maxLines = 1,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            when (selectedIndex) {
                0 -> HomeScreen()
                1 -> SearchScreen()
                2 -> AIChatScreen()
                3 -> NotificationScreen()
                4 -> ProfileScreen()
            }
        }
    }
}


// PLACEHOLDER SCREENS
@Composable
fun HomeScreen() {
    Text(text = "This is Home Screen")
}

@Composable
fun SearchScreen() {
    Text(text = "This is Search Screen")
}

@Composable
fun AIChatScreen() {
    Text(text = "This is AI Chat Screen")
}

@Composable
fun NotificationScreen() {
    Text(text = "This is Notification Screen")
}

@Composable
fun ProfileScreen() {
    Text(text = "This is Profile Screen")
}


@Preview(showBackground = true)
@Composable
fun PreviewBottomBarScreen() {
    BottomBarScreen()
}
