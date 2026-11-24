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
    seed: Int = Random.nextInt(1, 1_000_000),
    alreadySeen: Set<String> = emptySet(),
    objective: StoryObjective = StoryObjective.DebtFree
): StoryState {
    val stats = when (difficulty) {
        Difficulty.Easy -> StoryStats(
            cash = 44_000, debt = 0, invested = 0, portfolioValue = 0,
            knowledge = 30, wellbeing = 60, risk = 10
        )
        Difficulty.Normal -> StoryStats(
            cash = 40_000, debt = 4_000, invested = 0, portfolioValue = 0,
            knowledge = 25, wellbeing = 55, risk = 15
        )
        Difficulty.Hard -> StoryStats(
            cash = 36_000, debt = 8_000, invested = 0, portfolioValue = 0,
            knowledge = 20, wellbeing = 50, risk = 20
        )
    }
    val base = StoryState(
        day = 1,
        stats = stats,
        seed = seed,
        difficulty = difficulty,
        seenScenarios = alreadySeen.toList(),
        objective = objective
    )
    val event = eventForDay(1, base)
    return base.copy(goalText = goalForDay(base, event)?.text ?: "")
}

// ─────────────────────────────────────────────────────────────
//  Nakit / yatırım hesapları
// ─────────────────────────────────────────────────────────────

fun requirementMet(state: StoryState, requirement: ChoiceRequirement?): Boolean {
    if (requirement == null) return true
    if (state.stats.knowledge < requirement.minKnowledge) return false
    if (state.stats.cash < requirement.minCash) return false
    if (state.stats.landlord < requirement.minLandlord) return false
    if (state.stats.boss < requirement.minBoss) return false
    if (state.stats.friend < requirement.minFriend) return false
    if (requirement.requireFlag != null && requirement.requireFlag !in state.flags) return false
    if (requirement.forbidFlag != null && requirement.forbidFlag in state.flags) return false
    return true
}

/** Portföyün güncel değeri (eski kayıtlar için yatırım tutarına düşer). */
fun portfolioOf(state: StoryState): Int {
    val value = state.stats.portfolioValue
    if (value > 0) return value
    return state.stats.invested.coerceAtLeast(0)
}

private fun cashFromDivest(state: StoryState, effect: ChoiceEffect): Int {
    val old = state.stats
    val divested = effect.divest.coerceIn(0, old.invested)
    if (divested <= 0) return 0
    if (old.invested <= 0) return 0
    val portfolio = portfolioOf(state)
    return (divested.toLong() * portfolio / old.invested).toInt().coerceAtLeast(0)
}

/** Seçimin anında gerektirdiği net nakit (yatırım satışı düşülerek). */
fun requiredCash(state: StoryState, choice: StoryChoice): Int {
    val effect = choice.effect
    val cashIn = cashFromDivest(state, effect)
    val outflow = effect.invest - cashIn - effect.cash
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

// ─────────────────────────────────────────────────────────────
//  Seçim önizlemesi / uygulaması
// ─────────────────────────────────────────────────────────────

private data class AppliedStats(
    val stats: StoryStats,
    val overflowDebt: Int,
    val cashFromDivest: Int,
    val investedAdded: Int,
    val divestedCost: Int
)

private fun computeStats(state: StoryState, choice: StoryChoice): AppliedStats {
    val old = state.stats
    val effect = choice.effect

    var cashDelta = effect.cash
    var debtDelta = effect.debt

    val divestedCost = effect.divest.coerceIn(0, old.invested)
    val cashIn = cashFromDivest(state, effect)
    cashDelta += cashIn

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
    val oldPortfolio = portfolioOf(state)
    val newInvested = (old.invested - divestedCost + investedAdded).coerceAtLeast(0)
    val newPortfolio = (oldPortfolio - cashIn + investedAdded).coerceAtLeast(0)

    return AppliedStats(
        stats = old.copy(
            cash = newCash,
            debt = newDebt,
            invested = newInvested,
            portfolioValue = newPortfolio,
            knowledge = clampInt(old.knowledge + effect.knowledge, 0, 100),
            wellbeing = clampInt(old.wellbeing + effect.wellbeing, 0, 100),
            risk = clampInt(old.risk + effect.risk, 0, 100),
            landlord = clampInt(old.landlord + effect.landlord, 0, 100),
            boss = clampInt(old.boss + effect.boss, 0, 100),
            friend = clampInt(old.friend + effect.friend, 0, 100)
        ),
        overflowDebt = overflowDebt,
        cashFromDivest = cashIn,
        investedAdded = investedAdded,
        divestedCost = divestedCost
    )
}

/** Sadece istatistik sonucunu döndürür (hedef kontrolü için). */
fun previewStats(state: StoryState, choice: StoryChoice): StoryStats =
    computeStats(state, choice).stats

fun applyChoice(state: StoryState, event: StoryEvent, choice: StoryChoice): StoryState {
    if (!requirementMet(state, choice.requires)) return state
    if (state.pendingResult != null || state.finished) return state

    val old = state.stats
    val applied = computeStats(state, choice)
    val newStats = applied.stats

    var flags = state.flags
    if (choice.effect.setFlags.isNotEmpty()) flags = flags + choice.effect.setFlags
    if (choice.effect.clearFlags.isNotEmpty()) flags = flags - choice.effect.clearFlags.toSet()

    // ── Günün hedefi ──
    val goal = goalForDay(state, event)
    val goalDone = goal?.check(state, newStats, choice) == true
    val newStreak = if (goalDone) state.goalStreak + 1 else 0
    val newGoalHits = state.goalHits + if (goalDone) 1 else 0
    val goalXp = if (goalDone) (goal?.rewardXp ?: 0) else 0
    val streakXp = if (goalDone && newStreak > 0 && newStreak % 3 == 0) 15 else 0

    // ── Rozetler ──
    val badgeState = state.copy(stats = newStats, flags = flags, goalStreak = newStreak)
    val unlocked = evaluateBadges(badgeState).filter { it !in state.earnedBadges }
    val badgeXp = unlocked.size * 20

    val xpGain = 5 + goalXp + streakXp + badgeXp
    val newBadges = state.earnedBadges + unlocked

    val deltas = buildList {
        if (newStats.cash != old.cash) add(StatDelta("Nakit", newStats.cash - old.cash, money = true))
        if (newStats.debt != old.debt) add(StatDelta("Kart borcu", newStats.debt - old.debt, money = true))
        if (newStats.invested != old.invested) add(StatDelta("Yatırım", newStats.invested - old.invested, money = true))
        if (newStats.knowledge != old.knowledge) add(StatDelta("Bilgi", newStats.knowledge - old.knowledge))
        if (newStats.wellbeing != old.wellbeing) add(StatDelta("Refah", newStats.wellbeing - old.wellbeing))
        if (newStats.risk != old.risk) add(StatDelta("Risk", newStats.risk - old.risk))
    }

    val resultText = buildString {
        append(choice.result)
        if (applied.overflowDebt > 0) {
            append(" (Eksik kalan ${formatTL(applied.overflowDebt)} karta yazıldı.)")
        }
    }

    val entry = StoryLogEntry(
        day = state.day,
        eventTitle = event.title,
        choiceLabel = choice.label,
        result = resultText,
        deltas = deltas,
        lesson = event.lesson,
        xp = xpGain,
        goalDone = goalDone
    )

    val scheduled = choice.effect.delayed.map { delayed ->
        PendingEffect(
            id = delayed.id.ifBlank { "delayed-${state.day}-${delayed.title}" },
            title = delayed.title,
            dueDay = (state.day + delayed.days).coerceIn(state.day + 1, totalChapters()),
            message = delayed.message,
            cash = delayed.cash,
            debt = delayed.debt,
            knowledge = delayed.knowledge,
            wellbeing = delayed.wellbeing,
            risk = delayed.risk,
            landlord = delayed.landlord,
            boss = delayed.boss,
            friend = delayed.friend,
            setFlags = delayed.setFlags
        )
    }

    return state.copy(
        stats = newStats,
        flags = flags,
        log = state.log + entry,
        pendingResult = entry,
        lastXpGain = xpGain,
        totalXp = state.totalXp + xpGain,
        earnedBadges = newBadges,
        newBadges = unlocked,
        goalDone = goalDone,
        goalStreak = newStreak,
        goalHits = newGoalHits,
        seenScenarios = (state.seenScenarios + eventKey(event)).distinct(),
        pendingEffects = state.pendingEffects + scheduled
    )
}

// ─────────────────────────────────────────────────────────────
//  Gün ilerletme + canlı ekonomi
// ─────────────────────────────────────────────────────────────

fun advanceDay(state: StoryState): StoryState {
    if (state.finished) return state
    if (state.pendingResult == null) return state
    if (state.day >= totalChapters()) {
        return state.copy(finished = true, pendingResult = null, checkpointAct = 0)
    }

    val nextDay = state.day + 1
    val newMarket = nextMarket(state.market, state.seed, nextDay)
    val oldPortfolio = portfolioOf(state)
    val newPortfolio = nextPortfolio(state, newMarket)

    var stats = state.stats.copy(portfolioValue = newPortfolio)
    var flags = state.flags
    val notices = mutableListOf<String>()

    // Gecikmeli sonuçlar (kart faizi, taksit, vergi cezası, karakter tepkileri…)
    val due = state.pendingEffects.filter { it.dueDay <= nextDay }
    val remainingEffects = state.pendingEffects.filter { it.dueDay > nextDay }
    due.forEach { effect ->
        var cash = stats.cash + effect.cash
        var debt = stats.debt + effect.debt
        if (cash < 0) {
            debt += -cash
            cash = 0
        }
        stats = stats.copy(
            cash = cash,
            debt = debt.coerceAtLeast(0),
            knowledge = clampInt(stats.knowledge + effect.knowledge, 0, 100),
            wellbeing = clampInt(stats.wellbeing + effect.wellbeing, 0, 100),
            risk = clampInt(stats.risk + effect.risk, 0, 100),
            landlord = clampInt(stats.landlord + effect.landlord, 0, 100),
            boss = clampInt(stats.boss + effect.boss, 0, 100),
            friend = clampInt(stats.friend + effect.friend, 0, 100)
        )
        if (effect.setFlags.isNotEmpty()) flags = flags + effect.setFlags
        notices += "⏳ ${effect.title}: ${effect.message}"
    }

    // Haftalık kart faizi (7, 14, 21, 28. günler)
    if (nextDay % 7 == 0 && stats.debt > 0) {
        val interest = (stats.debt * 0.012).roundToInt().coerceAtLeast(25)
        stats = stats.copy(debt = stats.debt + interest)
        notices += "💳 Kart faizi işledi: +${formatTL(interest)} borç (haftalık %1,2)"
    }

    // Rastgele ara olay
    val rng = StoryRandom(state.seed + nextDay * 3571)
    if (rng.next() < 0.28) {
        val randomEvent = RANDOM_EVENTS[rng.nextInt(RANDOM_EVENTS.size)]
        if (randomEvent.cash < 0) {
            val afterCash = stats.cash + randomEvent.cash
            stats = if (afterCash >= 0) {
                stats.copy(cash = afterCash)
            } else {
                stats.copy(cash = 0, debt = stats.debt - afterCash)
            }
        } else {
            stats = stats.copy(cash = stats.cash + randomEvent.cash)
        }
        stats = stats.copy(wellbeing = clampInt(stats.wellbeing + randomEvent.wellbeing, 0, 100))
        notices += "🎲 ${randomEvent.text}"
    }

    var next = state.copy(
        day = nextDay,
        pendingResult = null,
        market = newMarket,
        stats = stats,
        flags = flags,
        portfolioDelta = newPortfolio - oldPortfolio,
        lastXpGain = 0,
        newBadges = emptyList(),
        checkpointAct = 0,
        notice = notices.joinToString("\n").takeIf { it.isNotEmpty() },
        pendingEffects = remainingEffects
    )

    val event = eventForDay(nextDay, next)
    next = next.copy(
        goalText = goalForDay(next, event)?.text ?: "",
        goalDone = false
    )
    return next
}

fun nextMarket(current: MarketState, seed: Int, day: Int): MarketState {
    val rng = StoryRandom(seed + day * 7919)
    val inflation = (current.inflation + rng.nextBetween(-0.25, 0.85)).coerceIn(15.0, 95.0)
    val usd = (current.usd * (1.0 + rng.nextBetween(-0.004, 0.013))).coerceIn(20.0, 150.0)
    val gold = (current.gold * (1.0 + rng.nextBetween(-0.006, 0.016))).toInt().coerceAtLeast(500)
    val stockIndex = (current.stockIndex * (1.0 + rng.nextBetween(-0.014, 0.016))).coerceIn(10.0, 10_000.0)
    val btc = (current.btc * (1.0 + rng.nextBetween(-0.035, 0.040))).toInt().coerceAtLeast(100_000)
    return MarketState(
        inflation = (inflation * 10).roundToInt() / 10.0,
        usd = (usd * 100).roundToInt() / 100.0,
        gold = gold,
        stockIndex = (stockIndex * 100).roundToInt() / 100.0,
        policyRate = current.policyRate,
        btc = btc
    )
}

/** Portföyün bir sonraki günkü değeri; oyuncunun yatırım tercihlerine göre ağırlıklandırılır. */
fun nextPortfolio(state: StoryState, newMarket: MarketState): Int {
    val value = portfolioOf(state)
    if (value <= 0) return 0

    val old = state.market
    fun change(newValue: Double, oldValue: Double): Double =
        if (oldValue <= 0.0) 0.0 else (newValue - oldValue) / oldValue

    val goldReturn = change(newMarket.gold.toDouble(), old.gold.toDouble())
    val usdReturn = change(newMarket.usd, old.usd)
    val stockReturn = change(newMarket.stockIndex, old.stockIndex)
    val btcReturn = change(newMarket.btc.toDouble(), old.btc.toDouble())

    // gold, usd, stock, btc ağırlıkları
    val weights = when {
        "crypto" in state.flags -> listOf(0.15, 0.15, 0.15, 0.55)
        "stocks" in state.flags -> listOf(0.20, 0.15, 0.50, 0.15)
        "gold" in state.flags -> listOf(0.55, 0.20, 0.15, 0.10)
        else -> listOf(0.35, 0.30, 0.20, 0.15)
    }

    val dailyReturn = (goldReturn * weights[0] + usdReturn * weights[1] +
            stockReturn * weights[2] + btcReturn * weights[3]).coerceIn(-0.10, 0.12)

    return (value * (1.0 + dailyReturn)).roundToInt().coerceAtLeast(1)
}

// ─────────────────────────────────────────────────────────────
//  Sonuç: yatırım getirisi, puan, arketip
// ─────────────────────────────────────────────────────────────

fun investmentValue(state: StoryState): Int = portfolioOf(state)

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

// ─────────────────────────────────────────────────────────────
//  Ayın hedefi
// ─────────────────────────────────────────────────────────────

fun objectiveReached(state: StoryState): Boolean = when (state.objective) {
    StoryObjective.DebtFree -> state.stats.debt == 0
    StoryObjective.EmergencyFund -> (state.stats.cash + investmentValue(state)) >= 25_000
    StoryObjective.Investor -> investmentValue(state) >= 15_000
}

fun objectiveProgress(state: StoryState): Float = when (state.objective) {
    StoryObjective.DebtFree ->
        if (state.stats.debt <= 0) 1f
        else (1f - state.stats.debt / 20_000f).coerceIn(0f, 1f)
    StoryObjective.EmergencyFund ->
        ((state.stats.cash + investmentValue(state)) / 25_000f).coerceIn(0f, 1f)
    StoryObjective.Investor ->
        (investmentValue(state) / 15_000f).coerceIn(0f, 1f)
}

fun objectiveStatusText(state: StoryState): String = when (state.objective) {
    StoryObjective.DebtFree ->
        if (state.stats.debt <= 0) "Borçsuzsun 🎉"
        else "Kart borcu: ${formatTL(state.stats.debt)}"
    StoryObjective.EmergencyFund -> {
        val buffer = state.stats.cash + investmentValue(state)
        "Tampon: ${formatTL(buffer)} / ${formatTL(25_000)}"
    }
    StoryObjective.Investor ->
        "Portföy: ${formatTL(investmentValue(state))} / ${formatTL(15_000)}"
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
            "Ayı borçla kapattın. Yüksek faizli borç, gelecek ayların kararlarını senin yerine vermeye başlar."
        )
        storyScore(state) >= 80 -> StoryArchetype(
            "🏆", "Finansal Özgürlük",
            "Nakit, bilgi, refah ve riski birlikte yönettin. Bu ay parayı yönetmeyi öğrendin."
        )
        stats.debt >= 15_000 -> StoryArchetype(
            "😰", "Baskı Altında",
            "Ayı bitirdin ama kart borcu omzunda kaldı. Borcun faizi gelecek ayın en pahalı gideri olacak."
        )
        stats.knowledge >= 65 && stats.risk <= 40 -> StoryArchetype(
            "🧠", "Bütçe Stratejisti",
            "Kararlarını duygularla değil bilgiyle verdin. Riskin farkında oldun ve planına sadık kaldın."
        )
        stats.debt == 0 && stats.cash >= 25_000 -> StoryArchetype(
            "🐷", "Tasarruf Ustası",
            "Borçtan uzak durdun ve nakit tamponunu korudun. Zor günler için sağlam bir alan açtın."
        )
        stats.risk >= 60 -> StoryArchetype(
            "🎲", "Risk Avcısı",
            "Cesur kararlar verdin. Bazıları kazandırdı, bazıları ders oldu; şimdi riski yönetme zamanı."
        )
        stats.wellbeing >= 70 -> StoryArchetype(
            "🌿", "Dengeli Hayat",
            "Bütçe disiplini ile yaşam kalitesi arasında sağlıklı bir denge kurdun."
        )
        else -> StoryArchetype(
            "🌱", "Yolun Başı",
            "İlk ayı tamamladın. Bir sonraki ay aynı hataları yapmadan daha ileri gideceksin."
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

fun randomSeed(): Int = abs(Random.nextInt(1, 1_000_000))
