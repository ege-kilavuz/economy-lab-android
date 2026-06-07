package com.egekilavuz.economylab

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.egekilavuz.economylab.ui.screens.HomeScreen

object Routes {
    const val HOME = "home"
    const val CATEGORY = "category/{id}"
    const val SIMULATION = "simulation/{id}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onCategoryClick = { id ->
                    navController.navigate("category/$id")
                },
                onSimulationClick = { id ->
                    navController.navigate("simulation/$id")
                }
            )
        }
    }
}
