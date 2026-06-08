package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences

class MiniGameManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("minigame_data", Context.MODE_PRIVATE)

    fun getHighScore(gameId: String): Int = prefs.getInt("highscore_$gameId", 0)

    fun saveHighScore(gameId: String, score: Int) {
        val current = getHighScore(gameId)
        if (score > current) {
            prefs.edit().putInt("highscore_$gameId", score).apply()
        }
    }

    fun getBestScore(gameId: String): Int = getHighScore(gameId)
}
