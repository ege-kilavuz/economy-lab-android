package com.egekilavuz.economylab.data

data class Achievement(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val condition: (stats: AchievementStats) -> Boolean
)

data class AchievementStats(
    val completedQuizzes: Int,
    val readItems: Int,
    val gameCompleted: Boolean,
    val gameScore: Int,
    val gameDifficulty: Difficulty = Difficulty.Easy,
    val portfolioValue: Int = 0,
    val noCardDebt: Boolean = false,
    val dailyStreak: Int = 0
)

val ACHIEVEMENTS = listOf(
    Achievement("first-quiz", "İlk Adım", "👶", "İlk quizini tamamla") { it.completedQuizzes >= 1 },
    Achievement("five-quizzes", "Finansçı", "📚", "5 quiz tamamla") { it.completedQuizzes >= 5 },
    Achievement("ten-quizzes", "Uzman", "🎓", "10 quiz tamamla") { it.completedQuizzes >= 10 },
    Achievement("all-quizzes", "Her Şeyi Bilen", "🧠", "Tüm quizleri tamamla") { it.completedQuizzes >= 12 },
    Achievement("first-game", "Oyuncu", "🎮", "Oyunu bir kere bitir") { it.gameCompleted },
    Achievement("hard-mode", "Dayanıklı", "💪", "Zor modda oyunu bitir") {
        it.gameCompleted && it.gameDifficulty == Difficulty.Hard
    },
    Achievement("no-debt", "Tutumlu", "🐷", "Kart borcu olmadan oyunu bitir") {
        it.gameCompleted && it.noCardDebt
    },
    Achievement("investor", "Yatırımcı", "📈", "Portföy değeri 10.000 TL'yi geçsin") {
        it.portfolioValue >= 10000
    },
    Achievement("reader", "Okuyucu", "📖", "20 içerik oku") { it.readItems >= 20 },
    Achievement("streak-3", "Düzenli", "🔥", "3 gün üst üste giriş yap") { it.dailyStreak >= 3 },
    Achievement("streak-7", "Azimli", "⭐", "7 gün üst üste giriş yap") { it.dailyStreak >= 7 },
)

fun checkAchievements(stats: AchievementStats): List<Achievement> {
    return ACHIEVEMENTS.filter { it.condition(stats) }
}

data class ScoreEntry(
    val name: String,
    val score: Int,
    val difficulty: String,
    val date: String,
    val achievements: Int = 0
)
