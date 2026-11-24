package com.egekilavuz.economylab.data.story

import com.egekilavuz.economylab.data.Difficulty
import kotlinx.serialization.Serializable
import java.text.NumberFormat
import java.util.Locale

// ─────────────────────────────────────────────────────────────
//  Çalışma zamanı durumu (kaydedilebilir)
// ─────────────────────────────────────────────────────────────

@Serializable
data class StoryStats(
    val cash: Int = 40_000,
    val debt: Int = 4_000,
    val invested: Int = 0,
    val portfolioValue: Int = 0,
    val knowledge: Int = 25,
    val wellbeing: Int = 55,
    val risk: Int = 15
)

@Serializable
data class MarketState(
    val inflation: Double = 38.4,
    val usd: Double = 42.60,
    val gold: Int = 4_350,
    val stockIndex: Double = 100.0,
    val policyRate: Double = 39.50,
    val btc: Int = 2_850_000
)

@Serializable
data class StatDelta(
    val label: String,
    val amount: Int,
    val money: Boolean = false
)

@Serializable
data class StoryLogEntry(
    val day: Int,
    val eventTitle: String,
    val choiceLabel: String,
    val result: String,
    val deltas: List<StatDelta> = emptyList(),
    val lesson: String? = null,
    val xp: Int = 0,
    val goalDone: Boolean = false
)

@Serializable
data class StoryState(
    val day: Int = 1,
    val stats: StoryStats = StoryStats(),
    val flags: Set<String> = emptySet(),
    val log: List<StoryLogEntry> = emptyList(),
    val pendingResult: StoryLogEntry? = null,
    val finished: Boolean = false,
    val seed: Int = 0,
    val difficulty: Difficulty = Difficulty.Normal,
    val market: MarketState = MarketState(),
    // ── Ödül ve ritim ──
    val lastXpGain: Int = 0,
    val totalXp: Int = 0,
    val earnedBadges: List<String> = emptyList(),
    val newBadges: List<String> = emptyList(),
    val goalText: String = "",
    val goalDone: Boolean = false,
    val goalStreak: Int = 0,
    val goalHits: Int = 0,
    val claimedActs: List<Int> = emptyList(),
    val checkpointAct: Int = 0,
    // ── Canlı ekonomi ──
    val portfolioDelta: Int = 0,
    val notice: String? = null,
    // ── Senoryo tekrar kontrolü ──
    val seenScenarios: List<String> = emptyList()
) {
    val isResolved: Boolean get() = pendingResult != null
    val progress: Float get() = ((day - 1).coerceIn(0, 30)) / 30f
}

// ─────────────────────────────────────────────────────────────
//  İçerik modeli (yalnızca kod içinde, serileştirilmez)
// ─────────────────────────────────────────────────────────────

enum class ChoiceTone { Safe, Balanced, Risky, Social }

data class ChoiceRequirement(
    val minKnowledge: Int = 0,
    val minCash: Int = 0,
    val requireFlag: String? = null,
    val forbidFlag: String? = null,
    val note: String = ""
)

data class ChoiceEffect(
    /** Doğrudan nakit değişimi. Negatif değer harcamadır. */
    val cash: Int = 0,
    /** Kredi kartı borcu değişimi. */
    val debt: Int = 0,
    /** Nakit -> yatırım. Engine bu tutarı ayrıca nakitden düşer. */
    val invest: Int = 0,
    /** Yatırımdan nakde dönüş. */
    val divest: Int = 0,
    val knowledge: Int = 0,
    val wellbeing: Int = 0,
    val risk: Int = 0,
    val setFlags: List<String> = emptyList(),
    val clearFlags: List<String> = emptyList()
)

data class StoryChoice(
    val id: String,
    val label: String,
    val hint: String? = null,
    val tone: ChoiceTone = ChoiceTone.Balanced,
    val requires: ChoiceRequirement? = null,
    val effect: ChoiceEffect = ChoiceEffect(),
    val result: String
)

data class StoryEvent(
    val day: Int,
    val title: String,
    val icon: String,
    val scene: List<String>,
    val choices: List<StoryChoice>,
    val lesson: String? = null,
    val condition: (StoryState) -> Boolean = { true },
    /** Tekrarı engellemek için benzersiz kimlik. Boşsa gün+başlık kullanılır. */
    val id: String = "",
    /** Bu olayın hangi gün aralığında çıkabileceği (genel senaryolar için). */
    val dayRange: IntRange = day..day
)

// ─────────────────────────────────────────────────────────────
//  Yardımcılar
// ─────────────────────────────────────────────────────────────

fun clampInt(value: Int, min: Int, max: Int): Int = when {
    value < min -> min
    value > max -> max
    else -> value
}

private val tlFormatter = NumberFormat.getIntegerInstance(Locale.forLanguageTag("tr-TR"))

/** 12500 -> "12.500 TL" */
fun formatTL(value: Int): String = "${tlFormatter.format(value)} TL"

/** 12500 -> "12.500" */
fun formatNumber(value: Int): String = tlFormatter.format(value)

fun formatPct(value: Double): String {
    val rounded = (value * 10).toInt() / 10.0
    return if (rounded % 1.0 == 0.0) "%${rounded.toInt()}" else "%${rounded}"
}
