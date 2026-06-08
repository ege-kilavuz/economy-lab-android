package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences

class ProgressManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("economy_lab_progress", Context.MODE_PRIVATE)

    var completedQuizIds: Set<String>
        get() = prefs.getStringSet("completed_quizzes", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("completed_quizzes", value).apply()

    var openedItemIds: Set<String>
        get() = prefs.getStringSet("opened_items", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("opened_items", value).apply()

    var openedSimulationIds: Set<String>
        get() = prefs.getStringSet("opened_simulations", emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet("opened_simulations", value).apply()

    var userName: String
        get() = prefs.getString("user_name", "") ?: ""
        set(value) = prefs.edit().putString("user_name", value).apply()

    val hasUserName: Boolean get() = userName.isNotBlank()

    fun isQuizCompleted(id: String): Boolean = completedQuizIds.contains(id)

    fun markQuizCompleted(id: String) {
        completedQuizIds = completedQuizIds + id
    }

    fun markItemOpened(id: String) {
        openedItemIds = openedItemIds + id
    }

    fun markSimulationOpened(id: String) {
        openedSimulationIds = openedSimulationIds + id
    }

    fun isDailyQuizCompleted(): Boolean = prefs.getBoolean("daily_quiz_done", false)

    fun markDailyQuizCompleted() {
        prefs.edit().putBoolean("daily_quiz_done", true).apply()
    }

    fun getCategoryProgress(categoryId: String, items: List<String>): CategoryProgress {
        val readCount = items.count { openedItemIds.contains(it) }
        val readPct = if (items.isNotEmpty()) (readCount * 100) / items.size else 0
        val quizDone = isQuizCompleted(categoryId)
        val totalPct = if (quizDone) maxOf(readPct, 50) else minOf(readPct, 50)
        return CategoryProgress(readCount, readPct, quizDone, totalPct)
    }

    data class CategoryProgress(
        val readCount: Int,
        val readPct: Int,
        val quizDone: Boolean,
        val totalPct: Int
    )
}
