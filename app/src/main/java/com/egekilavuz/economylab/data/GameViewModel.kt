package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GameViewModel(context: Context? = null) : ViewModel() {

    private val prefs: SharedPreferences? = context?.getSharedPreferences("game_save", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    var gameState by mutableStateOf(restoreGame() ?: newGame(Difficulty.Easy))
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

    private fun restoreGame(): GameState? {
        if (prefs == null) return null
        val raw = prefs.getString("game_state", null) ?: return null
        return try {
            json.decodeFromString<GameState>(raw)
        } catch (_: Exception) {
            null
        }
    }

    private fun persistGame(state: GameState) {
        prefs?.edit()?.putString("game_state", json.encodeToString(state))?.apply()
    }

    fun startNewGame(difficulty: Difficulty = Difficulty.Easy) {
        gameState = newGame(difficulty)
        useCard = false
        currentScreen = "home"
        persistGame(gameState)
    }

    fun toggleUseCard(v: Boolean) { useCard = v }

    fun navigate(screen: String) { currentScreen = screen }

    fun act(action: ActionId, amount: Int = 1, asset: HoldingId? = null) {
        gameState = applyAction(gameState, action, useCard, amount, asset)
        persistGame(gameState)
    }

    fun advanceDay() {
        gameState = if (gameState.day >= 30) {
            gameState.copy(day = 31, log = listOf("Ay bitti. Skor hesaplandı.") + gameState.log)
        } else {
            nextDay(gameState)
        }
        persistGame(gameState)
    }

    fun setDifficulty(d: Difficulty) {
        startNewGame(d)
    }

    fun hasSaveGame(): Boolean = prefs?.contains("game_state") == true

    fun deleteSave() {
        prefs?.edit()?.remove("game_state")?.apply()
    }
}
