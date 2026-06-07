package com.egekilavuz.economylab.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var gameState by mutableStateOf(newGame(Difficulty.Easy))
        private set

    var useCard by mutableStateOf(false)
        private set

    var currentScreen by mutableStateOf("home")
        private set

    val finished: Boolean get() = gameState.day > 30

    val summary: Summary get() = Summary(
        cash = gameState.cash,
        cardDebt = gameState.cardDebt,
        mood = gameState.mood,
        day = gameState.day
    )

    data class Summary(val cash: Int, val cardDebt: Int, val mood: Int, val day: Int)

    fun startNewGame(difficulty: Difficulty = Difficulty.Easy) {
        gameState = newGame(difficulty)
        useCard = false
        currentScreen = "home"
    }

    fun toggleUseCard(v: Boolean) { useCard = v }

    fun navigate(screen: String) { currentScreen = screen }

    fun act(action: ActionId, amount: Int = 1, asset: HoldingId? = null) {
        gameState = applyAction(gameState, action, useCard, amount, asset)
    }

    fun advanceDay() {
        gameState = if (gameState.day >= 30) {
            gameState.copy(day = 31, log = listOf("Ay bitti. Skor hesaplandı.") + gameState.log)
        } else {
            nextDay(gameState)
        }
    }

    fun setDifficulty(d: Difficulty) {
        startNewGame(d)
    }
}
