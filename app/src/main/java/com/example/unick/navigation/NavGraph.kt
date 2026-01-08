package com.example.unick.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unick.view.SchoolDetailScreen
import com.example.unick.view.ShortlistScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SHORTLIST
    ) {

        composable(NavRoutes.SHORTLIST) {
            ShortlistScreen(
                onSchoolClick = { schoolName ->
                    navController.navigate("${NavRoutes.DETAIL}/$schoolName")
                }
            )
        }

        composable(
            route = "${NavRoutes.DETAIL}/{schoolName}",
            arguments = listOf(
                navArgument("schoolName") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val schoolName =
                backStackEntry.arguments?.getString("schoolName") ?: ""

            SchoolDetailScreen(
                schoolName = schoolName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
