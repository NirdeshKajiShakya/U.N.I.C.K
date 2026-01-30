package com.example.unick.view

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun AdminBottomNavigationBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Profile
    )

    NavigationBar(containerColor = Color.White) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, maxLines = 1, fontSize = 11.sp) }
            )
        }
    }
}
