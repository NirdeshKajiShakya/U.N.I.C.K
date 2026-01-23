package com.example.unick.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.unick.view.SchoolDetailScreen
import com.example.unick.view.ShortlistScreen

object NavRoutes {
     val SHORTLIST = "shortlist"
     val DETAIL = "school_detail"
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SHORTLIST
    ) {
        composable(NavRoutes.SHORTLIST) {
            ShortlistScreen(
                onBackPressed = { navController.popBackStack() },
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
            val schoolId = backStackEntry.arguments?.getString("schoolId") ?: ""

            SchoolDetailScreen(
                schoolId = schoolId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}