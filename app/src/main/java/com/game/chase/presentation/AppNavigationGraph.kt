package com.game.chase.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.game.chase.presentation.home.HomeScreen

@Composable
fun AppNavigationGraph() {
    val navController = rememberNavController()
    // Set up the navigation graph
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(route = Screen.Home.route) { HomeScreen(navController) }
//        composable(
//            route = Screen.Game.route,
//            arguments = listOf(
//                navArgument("repoOwner") { type = NavType.StringType },
//                navArgument("repoName") { type = NavType.StringType },
//            )
//        ) { backStackEntry ->
//            GameScreen(
//                navController,
//                backStackEntry.arguments?.getString("repoOwner") ?: "",
//                backStackEntry.arguments?.getString("repoName") ?: "",
//            )
//        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game")
}
