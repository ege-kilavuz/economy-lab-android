package com.egekilavuz.economylab

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.egekilavuz.economylab.data.ContentLoader
import com.egekilavuz.economylab.data.GameViewModel
import com.egekilavuz.economylab.data.ProgressManager
import com.egekilavuz.economylab.data.SoundManager
import com.egekilavuz.economylab.data.XpManager
import com.egekilavuz.economylab.ui.screens.*
import com.egekilavuz.economylab.ui.simulations.*

sealed class BottomTab(val route: String, val title: String, val icon: ImageVector) {
    data object Learn : BottomTab("learn", "Öğren", Icons.Default.School)
    data object Simulations : BottomTab("simulations", "Keşfet", Icons.Default.Computer)
    data object Game : BottomTab("game", "Oyun", Icons.Default.SportsEsports)
    data object Leaderboard : BottomTab("leaderboard", "Liderlik", Icons.Default.Star)
}

private val tabs = listOf(BottomTab.Learn, BottomTab.Simulations, BottomTab.Game, BottomTab.Leaderboard)

const val ROUTE_CATEGORY = "category/{catId}"
const val ROUTE_QUIZ = "quiz/{catId}"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val progressManager = remember { ProgressManager(context) }
    val xpManager = remember { XpManager(context) }
    val soundManager = remember { SoundManager(context) }
    val content = remember { ContentLoader.load(context) }

    // Content update check
    val snackbarHostState = remember { SnackbarHostState() }
    var updateMessage by remember { mutableStateOf("") }
    var showUpdate by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(!progressManager.hasUserName) }

    LaunchedEffect(Unit) {
        ContentLoader.checkForUpdate(context) { success, msg ->
            updateMessage = msg
            if (success) {
                showUpdate = true
            }
        }
    }

    LaunchedEffect(showUpdate) {
        if (showUpdate && updateMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(updateMessage)
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isSubScreen = currentDestination?.route?.startsWith("category") == true ||
                      currentDestination?.route?.startsWith("quiz") == true ||
                      currentDestination?.route == "stats"

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isSubScreen) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                ) {
                    tabs.forEach { tab ->
                        val selected = currentDestination?.hierarchy?.any { it.route == tab.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(imageVector = tab.icon, contentDescription = tab.title) },
                            label = {
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomTab.Learn.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomTab.Learn.route) {
                Column {
                    XpBar(xpManager = xpManager)
                    HomeScreen(
                        content = content,
                        onCategoryClick = { navController.navigate("category/$it") },
                        onDailyQuizClick = { navController.navigate("daily-quiz") },
                        onStatsClick = { navController.navigate("stats") }
                    )
                }
            }
            composable(BottomTab.Simulations.route) {
                Column {
                    XpBar(xpManager = xpManager)
                    SimulationsScreen(
                        content = content,
                        onSimulationClick = { id ->
                            if (id.startsWith("sim-")) navController.navigate("simulation/$id")
                            else navController.navigate("minigame/$id")
                        }
                    )
                }
            }
            composable(BottomTab.Game.route) {
                Column {
                    XpBar(xpManager = xpManager)
                    val gameVm: GameViewModel = viewModel()
                    GameScreen(gameVm)
                }
            }
            composable(route = "simulation/{simId}") { entry ->
                val simId = entry.arguments?.getString("simId") ?: ""
                when (simId) {
                    "sim-0" -> InflationSimulation { navController.popBackStack() }
                    "sim-1" -> InterestSimulation { navController.popBackStack() }
                    "sim-2" -> CentralBankSimulation { navController.popBackStack() }
                    "sim-3" -> BESSimulation { navController.popBackStack() }
                    else -> InflationSimulation { navController.popBackStack() }
                }
            }
            composable(route = "minigame/{gameId}") { entry ->
                val gid = entry.arguments?.getString("gameId") ?: ""
                when (gid) {
                    "candle" -> CandleGame { navController.popBackStack() }
                    "card-escape" -> CardEscapeGame { navController.popBackStack() }
                    "budget-tetris" -> BudgetTetrisGame { navController.popBackStack() }
                    "news-noise" -> NewsNoiseGame { navController.popBackStack() }
                    "scam-radar" -> ScamRadarGame { navController.popBackStack() }
                    else -> CandleGame { navController.popBackStack() }
                }
            }
            composable(BottomTab.Leaderboard.route) {
                Column {
                    XpBar(xpManager = xpManager)
                    LeaderboardScreen()
                }
            }

            // Daily quiz route
            composable(route = "daily-quiz") {
                DailyQuizScreen(
                    contentLoader = ContentLoader,
                    xpManager = xpManager,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = ROUTE_CATEGORY,
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getString("catId") ?: return@composable
                val category = content.categories.find { it.id == catId }
                if (category != null) {
                    CategoryScreen(
                        category = category,
                        progressManager = progressManager,
                        onBack = { navController.popBackStack() },
                        onStartQuiz = { navController.navigate("quiz/$catId") }
                    )
                }
            }
            composable(
                route = ROUTE_QUIZ,
                arguments = listOf(navArgument("catId") { type = NavType.StringType })
            ) { backStackEntry ->
                val catId = backStackEntry.arguments?.getString("catId") ?: return@composable
                val category = content.categories.find { it.id == catId }
                if (category != null) {
                    QuizScreen(
                        category = category,
                        progressManager = progressManager,
                        xpManager = xpManager,
                        onBack = { navController.popBackStack() },
                        onComplete = { navController.popBackStack() }
                    )
                }
            }
            // Stats route
            composable(route = "stats") {
                StatsScreen(
                    content = content,
                    progressManager = progressManager,
                    xpManager = xpManager,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }

    // Name dialog on first launch
    if (showNameDialog) {
        UserNameDialog(
            onSave = { name ->
                progressManager.userName = name
                showNameDialog = false
            }
        )
    }
}
