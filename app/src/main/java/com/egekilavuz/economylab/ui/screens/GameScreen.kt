package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.egekilavuz.economylab.data.GameViewModel

@Composable
fun GameScreen(vm: GameViewModel) {
    val g = vm.gameState

    when (vm.currentScreen) {
        "home" -> GameHomeScreen(vm) { screen ->
            vm.navigate(screen)
        }
        "bank" -> GameBankScreen(vm) { vm.navigate("home") }
        "market" -> GameMarketScreen(vm) { vm.navigate("home") }
        "invest" -> GameInvestScreen(vm) { vm.navigate("home") }
        "news" -> GameNewsScreen(vm) { vm.navigate("home") }
        "end" -> GameEndScreen(vm) { vm.navigate("home") }
        else -> GameHomeScreen(vm) { vm.navigate(it) }
    }

    // Check if game ended
    LaunchedEffect(g.day) {
        if (g.day > 30) {
            vm.navigate("end")
        }
    }
}
