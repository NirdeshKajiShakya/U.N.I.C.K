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
                onSchoolClick = { schoolId ->
                    navController.navigate("${NavRoutes.DETAIL}/$schoolId")
                }
            )
        }

        composable(
            route = "${NavRoutes.DETAIL}/{schoolId}",
            arguments = listOf(
                navArgument("schoolId") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val schoolId =
                backStackEntry.arguments?.getString("schoolId") ?: ""

            SchoolDetailScreen(
                schoolId = schoolId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
