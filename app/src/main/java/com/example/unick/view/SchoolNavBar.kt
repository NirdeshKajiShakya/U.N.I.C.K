package com.example.unick.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

// SchoolNavBar: Specifically for School Users (No Search Tab)
@Composable
fun SchoolNavBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onProfileClick: () -> Unit
) {
    val schoolNavItems = listOf(
        BottomNavItem.Home,
        // No Search
        BottomNavItem.AIChat, // "same w the notificaion and ai chat"
        BottomNavItem.Notification,
        BottomNavItem.Profile
    )

    NavigationBar(containerColor = Color.White) {
        schoolNavItems.forEach { item ->
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
