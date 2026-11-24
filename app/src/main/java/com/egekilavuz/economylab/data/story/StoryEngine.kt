package com.egekilavuz.economylab.data.story

import com.egekilavuz.economylab.data.Difficulty
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────
//  Deterministik rastgelelik (kayıt/yükleme tutarlılığı için)
// ─────────────────────────────────────────────────────────────

class StoryRandom(seed: Int) {
    private var state: Int = if (seed == 0) 1 else seed

    fun next(): Double {
        state = (state.toLong() * 1664525L + 1013904223L).toInt()
        return state.toUInt().toDouble() / 4294967296.0
    }

    fun nextBetween(min: Double, max: Double): Double = min + next() * (max - min)

    fun nextInt(until: Int): Int {
        if (until <= 0) return 0
        return (next() * until).toInt().coerceIn(0, until - 1)
    }
}

// ─────────────────────────────────────────────────────────────
//  Yeni oyun
// ─────────────────────────────────────────────────────────────

fun newStory(
    difficulty: Difficulty = Difficulty.Normal,
    seed: Int = Random.nextInt(1, 1_000_000)
): StoryState {
    val stats = when (difficulty) {
        // Kolay: borçsuz, daha yüksek bilgi ve refah
        Difficulty.Easy -> StoryStats(
            cash = 44_000, debt = 0, invested = 0,
            knowledge = 30, wellbeing = 60, risk = 10
        )
        // Normal: öğrencilikten kalan küçük bir kart borcu
        Difficulty.Normal -> StoryStats(
            cash = 40_000, debt = 4_000, invested = 0,
            knowledge = 25, wellbeing = 55, risk = 15
        )
        // Zor: daha az nakit, daha yüksek borç, daha düşük bilgi
        Difficulty.Hard -> StoryStats(
            cash = 36_000, debt = 8_000, invested = 0,
            knowledge = 20, wellbeing = 50, risk = 20
        )
    }
    return StoryState(
        day = 1,
        stats = stats,
        seed = seed,
        difficulty = difficulty
    )
}

// ─────────────────────────────────────────────────────────────
//  Seçim uygulama
// ─────────────────────────────────────────────────────────────

fun requirementMet(state: StoryState, requirement: ChoiceRequirement?): Boolean {
    if (requirement == null) return true
    if (state.stats.knowledge < requirement.minKnowledge) return false
    if (state.stats.cash < requirement.minCash) return false
    if (requirement.requireFlag != null && requirement.requireFlag !in state.flags) return false
    if (requirement.forbidFlag != null && requirement.forbidFlag in state.flags) return false
    return true
}

/** Seçimin anında gerektirdiği net nakit (yatırım satışı düşülerek). */
fun requiredCash(state: StoryState, choice: StoryChoice): Int {
    val effect = choice.effect
    val divested = effect.divest.coerceIn(0, state.stats.invested)
    val outflow = effect.invest - divested - effect.cash
    return outflow.coerceAtLeast(0)
}

/** Seçim hem bilgi/nakit koşullarını hem de anlık nakit yeterliliğini sağlıyor mu? */
fun canAffordChoice(state: StoryState, choice: StoryChoice): Boolean =
    requirementMet(state, choice.requires) && requiredCash(state, choice) <= state.stats.cash

/** Kilitliyse kullanıcıya gösterilecek neden. */
fun lockReasonFor(state: StoryState, choice: StoryChoice): String? = when {
    !requirementMet(state, choice.requires) ->
        choice.requires?.note?.takeIf { it.isNotBlank() } ?: "Kilitli"
    requiredCash(state, choice) > state.stats.cash ->
        "Nakit yetersiz · ${formatTL(requiredCash(state, choice))} gerekli"
    else -> null
}

fun applyChoice(state: StoryState, event: StoryEvent, choice: StoryChoice): StoryState {
    if (!requirementMet(state, choice.requires)) return state
    if (state.pendingResult != null || state.finished) return state

    val old = state.stats
    val effect = choice.effect

    var cashDelta = effect.cash
    var debtDelta = effect.debt

    val divested = effect.divest.coerceIn(0, old.invested)
    cashDelta += divested

    // Yatırım, elde olandan fazlaysa borca düşülmez; yalnızca karşılanabilen kısım yatırılır.
    val investedAdded = effect.invest
        .coerceAtLeast(0)
        .coerceAtMost((old.cash + cashDelta).coerceAtLeast(0))
    cashDelta -= investedAdded

    var newCash = old.cash + cashDelta
    var overflowDebt = 0
    if (newCash < 0) {
        overflowDebt = -newCash
        newCash = 0
    }

    val newDebt = (old.debt + debtDelta + overflowDebt).coerceAtLeast(0)
    val newInvested = old.invested - divested + investedAdded
    val newKnowledge = clampInt(old.knowledge + effect.knowledge, 0, 100)
    val newWellbeing = clampInt(old.wellbeing + effect.wellbeing, 0, 100)
    val newRisk = clampInt(old.risk + effect.risk, 0, 100)

    val newStats = old.copy(
        cash = newCash,
        debt = newDebt,
        invested = newInvested,
        knowledge = newKnowledge,
        wellbeing = newWellbeing,
        risk = newRisk
    )

    val deltas = buildList {
        if (newCash != old.cash) add(StatDelta("Nakit", newCash - old.cash, money = true))
        if (newDebt != old.debt) add(StatDelta("Kart borcu", newDebt - old.debt, money = true))
        if (newInvested != old.invested) add(StatDelta("Yatırım", newInvested - old.invested, money = true))
        if (newKnowledge != old.knowledge) add(StatDelta("Bilgi", newKnowledge - old.knowledge))
        if (newWellbeing != old.wellbeing) add(StatDelta("Refah", newWellbeing - old.wellbeing))
        if (newRisk != old.risk) add(StatDelta("Risk", newRisk - old.risk))
    }

    val resultText = if (overflowDebt > 0) {
        choice.result + " (Eksik kalan ${formatTL(overflowDebt)} karta yazıldı.)"
    } else {
        choice.result
    }

    val entry = StoryLogEntry(
        day = state.day,
        eventTitle = event.title,
        choiceLabel = choice.label,
        result = resultText,
        deltas = deltas,
        lesson = event.lesson
    )

    var flags = state.flags
    if (effect.setFlags.isNotEmpty()) flags = flags + effect.setFlags
    if (effect.clearFlags.isNotEmpty()) flags = flags - effect.clearFlags.toSet()

    return state.copy(
        stats = newStats,
        flags = flags,
        log = state.log + entry,
        pendingResult = entry
    )
}

// ─────────────────────────────────────────────────────────────
//  Gün ilerletme
// ─────────────────────────────────────────────────────────────

fun advanceDay(state: StoryState): StoryState {
    if (state.finished) return state
    if (state.pendingResult == null) return state // önce seçim yapılmalı
    if (state.day >= totalChapters()) {
        return state.copy(finished = true, pendingResult = null)
    }
    val nextDay = state.day + 1
    return state.copy(
        day = nextDay,
        pendingResult = null,
        market = nextMarket(state.market, state.seed, nextDay)
    )
}

fun nextMarket(current: MarketState, seed: Int, day: Int): MarketState {
    val rng = StoryRandom(seed + day * 7919)
    val inflation = (current.inflation + rng.nextBetween(-0.25, 0.85)).coerceIn(15.0, 95.0)
    val usd = (current.usd * (1.0 + rng.nextBetween(-0.004, 0.013))).coerceIn(20.0, 150.0)
    val gold = (current.gold * (1.0 + rng.nextBetween(-0.006, 0.016))).toInt().coerceAtLeast(500)
    val btc = (current.btc * (1.0 + rng.nextBetween(-0.035, 0.040))).toInt().coerceAtLeast(100_000)
    return MarketState(
        inflation = (inflation * 10).roundToInt() / 10.0,
        usd = (usd * 100).roundToInt() / 100.0,
        gold = gold,
        policyRate = current.policyRate,
        btc = btc
    )
}

// ─────────────────────────────────────────────────────────────
//  Sonuç: yatırım getirisi, puan, arketip
// ─────────────────────────────────────────────────────────────

/** Ay sonunda yatırımın ulaştığı değer (deterministik, oyuna göre değişir). */
fun investmentValue(state: StoryState): Int {
    if (state.stats.invested <= 0) return 0
    val rng = StoryRandom(state.seed + 4242)
    val factor = 0.85 + rng.next() * 0.35 // 0.85 - 1.20
    return (state.stats.invested * factor).roundToInt()
}

fun storyNetWorth(state: StoryState): Int =
    state.stats.cash + investmentValue(state) - state.stats.debt

fun storyScore(state: StoryState): Int {
    val netWorth = storyNetWorth(state)
    var score = 0
    score += clampInt(netWorth / 1000, 0, 45)
    score += clampInt(state.stats.knowledge, 0, 100) * 18 / 100
    score += clampInt(state.stats.wellbeing, 0, 100) * 22 / 100
    score += (100 - clampInt(state.stats.risk, 0, 100)) * 15 / 100
    return clampInt(score, 0, 100)
}

data class StoryArchetype(
    val emoji: String,
    val title: String,
    val description: String
)

fun archetypeFor(state: StoryState): StoryArchetype {
    val stats = state.stats
    return when {
        stats.debt >= 30_000 -> StoryArchetype(
            "🔗", "Borç Sarmalı",
            "Ayı borçla kapattın. Yüksek faizli borç, gelecek ayların kararlarını senin yerine vermeye başlar; ilk iş borcu küçültmek olmalı."
        )
        storyScore(state) >= 80 -> StoryArchetype(
            "🏆", "Finansal Özgürlük",
            "Nakit, bilgi, refah ve riski birlikte yönettin. Bu ay sadece para kazanmadın; parayı yönetmeyi öğrendin."
        )
        stats.debt >= 15_000 -> StoryArchetype(
            "😰", "Baskı Altında",
            "Ayı bitirdin ama kart borcu omzunda kaldı. Borcun faizi, bir sonraki ayın en pahalı gideri olacak."
        )
        stats.knowledge >= 65 && stats.risk <= 40 -> StoryArchetype(
            "🧠", "Bütçe Stratejisti",
            "Kararlarını duygularla değil, bilgiyle verdin. Riskin farkında oldun ve planına sadık kaldın."
        )
        stats.debt == 0 && stats.cash >= 25_000 -> StoryArchetype(
            "🐷", "Tasarruf Ustası",
            "Borçtan uzak durdun ve nakit tamponunu korudun. Zor günler için sağlam bir alan açtın."
        )
        stats.risk >= 60 -> StoryArchetype(
            "🎲", "Risk Avcısı",
            "Cesur kararlar verdin. Bazıları kazandırdı, bazıları ders oldu; şimdi riski yönetmeyi öğrenme zamanı."
        )
        stats.wellbeing >= 70 -> StoryArchetype(
            "🌿", "Dengeli Hayat",
            "Parayı hayatın merkezine koymadın. Bütçe disiplini ile yaşam kalitesi arasında sağlıklı bir denge kurdun."
        )
        else -> StoryArchetype(
            "🌱", "Yolun Başı",
            "İlk ayı tamamladın. Her karar bir deneyim; bir sonraki ay aynı hataları yapmadan daha ileri gideceksin."
        )
    }
}

fun scoreVerdict(score: Int): String = when {
    score >= 80 -> "Olağanüstü bir ay. Bütçe senin kontrolünde."
    score >= 65 -> "Güçlü bir ay. Birkaç küçük ayarla mükemmele yaklaşabilirsin."
    score >= 50 -> "Denge kurulmuş. Risk ve harcama tarafında hâlâ alan var."
    score >= 35 -> "Zorlu bir ay oldu ama öğrendiklerin gelecek ay işine yarayacak."
    else -> "Bu ay bütçe seni yönetti. Tekrar dene; her seferinde daha iyi olacaksın."
}

/** Zorluk bir çarpanla puanı etkilemez; zorluk yalnızca başlangıç koşullarını değiştirir. */
fun storyDifficultyBonus(difficulty: Difficulty): Int = 0

fun randomSeed(): Int = abs(Random.nextInt(1, 1_000_000))
