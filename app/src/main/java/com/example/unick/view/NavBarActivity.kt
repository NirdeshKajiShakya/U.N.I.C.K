package com.example.unick.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp

// --- Shared Navbar Components (Moved from UnifiedNavBar.kt) ---

sealed class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItem("Home", Icons.Default.Home, "home")
    object Profile : BottomNavItem("Profile", Icons.Default.Person, "profile")
    object Search : BottomNavItem("Search", Icons.Default.Search, "search")
    object AIChat : BottomNavItem("AI Chat", Icons.Default.Chat, "aichat")
    object Notification : BottomNavItem("Notification", Icons.Default.Notifications, "notification")
}

@Composable
fun UnifiedBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onProfileClick: () -> Unit,
    navItems: List<BottomNavItem> = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.AIChat,
        BottomNavItem.Notification,
        BottomNavItem.Profile
    )
) {
    NavigationBar(containerColor = Color.White) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == "profile") {
                        onProfileClick()
                    } else {
                        onNavigate(item.route)
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, maxLines = 1, fontSize = 11.sp) }
            )
        }
    }
}

// --- End Shared Components ---

// Preserving existing Activity class
class NavBarActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
             // Example usage / Placeholder
            UnifiedBottomNavigationBar(
                currentRoute = "home",
                onNavigate = {},
                onProfileClick = {}
            )
        }
    }
}
