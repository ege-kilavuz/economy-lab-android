package com.egekilavuz.economylab.ui.screens

import androidx.compose.runtime.*
import com.egekilavuz.economylab.data.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel) {
    val g = vm.gameState

    when (vm.currentScreen) {
        "home" -> GameHomeScreen(vm) { screen -> vm.navigate(screen) }
        "bank" -> GameBankScreen(vm) { vm.navigate("home") }
        "market" -> GameMarketScreen(vm) { vm.navigate("home") }
        "invest" -> GameInvestScreen(vm) { vm.navigate("home") }
        "news" -> GameNewsPlaceholder(vm)
        "end" -> GameEndScreen(vm) { vm.navigate("home") }
        else -> GameHomeScreen(vm) { vm.navigate(it) }
    }

    LaunchedEffect(g.day) {
        if (g.day > 30) vm.navigate("end")
    }
}
