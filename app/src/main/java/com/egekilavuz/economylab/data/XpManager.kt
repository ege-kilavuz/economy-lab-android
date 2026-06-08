package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences
import java.util.Calendar

data class LevelInfo(val level: Int, val title: String, val xpRequired: Int)

data class XpSummary(val total: Int, val level: Int, val title: String, val progress: Float, val nextLevelXp: Int)

class XpManager(context: Context) {

    companion object {
        private const val PREFS = "xp_data"
        private const val KEY_TOTAL = "total_xp"
        private const val KEY_STREAK = "streak_days"
        private const val KEY_LAST_DATE = "last_activity_date"

        // XP rewards
        const val XP_READ_ITEM = 10
        const val XP_QUIZ_COMPLETE = 20
        const val XP_QUIZ_PERFECT = 30  // bonus on top
        const val XP_GAME_FINISH = 50
        const val XP_MINI_GAME = 5
        const val XP_ACHIEVEMENT = 25
        const val XP_DAILY_QUIZ = 15
        const val XP_STREAK_BONUS = 10  // per day streak after 3 days

        private val LEVELS = listOf(
            LevelInfo(1, "Başlangıç 🐣", 0),
            LevelInfo(2, "Öğrenci 📖", 150),
            LevelInfo(3, "Tasarrufçu 🐷", 400),
            LevelInfo(4, "Yatırımcı 📈", 800),
            LevelInfo(5, "Finansçı 🧠", 1500),
            LevelInfo(6, "Uzman 🎓", 2500),
            LevelInfo(7, "Efsane 🏅", 4000),
            LevelInfo(8, "Efsanevi 🏆", 6000),
        )
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun getTotalXp(): Int = prefs.getInt(KEY_TOTAL, 0)

    fun getSummary(): XpSummary {
        val total = getTotalXp()
        val currentLevel = calculateLevel(total)
        val nextLvl = LEVELS.find { it.level == currentLevel + 1 }
        val currentLvlInfo = LEVELS.find { it.level == currentLevel } ?: LEVELS.last()
        val nextLvlInfo = nextLvl ?: currentLvlInfo

        val xpInCurrentLevel = total - currentLvlInfo.xpRequired
        val xpNeeded = nextLvlInfo.xpRequired - currentLvlInfo.xpRequired
        val progress = if (xpNeeded > 0) (xpInCurrentLevel.toFloat() / xpNeeded).coerceIn(0f, 1f) else 1f

        return XpSummary(total, currentLevel, currentLvlInfo.title, progress, nextLvlInfo.xpRequired)
    }

    fun addXp(amount: Int): XpSummary {
        val oldTotal = getTotalXp()
        val newTotal = (oldTotal + amount).coerceAtMost(LEVELS.last().xpRequired + 9999)
        prefs.edit().putInt(KEY_TOTAL, newTotal).apply()
        updateStreak()
        return getSummary()
    }

    fun addXpForAction(action: String): XpSummary {
        val xp = when (action) {
            "read_item" -> XP_READ_ITEM
            "quiz_complete" -> XP_QUIZ_COMPLETE
            "quiz_perfect" -> XP_QUIZ_COMPLETE + XP_QUIZ_PERFECT
            "game_finish" -> XP_GAME_FINISH
            "mini_game" -> XP_MINI_GAME
            "achievement" -> XP_ACHIEVEMENT
            "daily_quiz" -> XP_DAILY_QUIZ
            else -> 0
        }
        return if (xp > 0) addXp(xp) else getSummary()
    }

    fun getStreak(): Int = prefs.getInt(KEY_STREAK, 0)

    private fun updateStreak() {
        val lastDate = prefs.getLong(KEY_LAST_DATE, 0L)
        val today = normalizeDate(System.currentTimeMillis())
        val last = normalizeDate(lastDate)

        if (last == today) return // already updated today

        val yesterday = today - 86400000L
        val streak = if (last >= yesterday) getStreak() + 1 else 1

        prefs.edit().putInt(KEY_STREAK, streak).putLong(KEY_LAST_DATE, today).apply()
    }

    private fun calculateLevel(xp: Int): Int {
        var level = 1
        for (lvl in LEVELS) {
            if (xp >= lvl.xpRequired) level = lvl.level else break
        }
        return level
    }

    private fun normalizeDate(timeMs: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timeMs
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    fun getDailyQuizSeed(): Int {
        val cal = Calendar.getInstance()
        return cal.get(Calendar.YEAR) * 10000 + (cal.get(Calendar.MONTH) + 1) * 100 + cal.get(Calendar.DAY_OF_MONTH)
    }

    fun isDailyQuizDone(): Boolean {
        val today = normalizeDate(System.currentTimeMillis())
        return prefs.getLong("daily_quiz_date", 0L) == today
    }

    fun markDailyQuizDone() {
        prefs.edit().putLong("daily_quiz_date", normalizeDate(System.currentTimeMillis())).apply()
    }
}
