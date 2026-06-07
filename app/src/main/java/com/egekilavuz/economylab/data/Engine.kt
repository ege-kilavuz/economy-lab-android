package com.egekilavuz.economylab.data

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.random.Random

// ─── Simple seeded RNG ───

class SeededRandom(seed: Int) {
    private var state = seed

    fun next(): Double {
        state = (state * 1664525L + 1013904223L).toInt()
        return (state.toUInt().toDouble() / 4294967296.0)
    }

    fun nextInt(until: Int) = (next() * until).toInt()
}

// ─── New Game ───

fun newGame(difficulty: Difficulty, seed: Int = abs(Random.nextInt()) % 1000000): GameState {
    val b = balanceFor(difficulty)
    val gross = (b.startingCash / 0.8).roundToInt()
    val tax = gross - b.startingCash
    val grossSalary = (b.startingCash / 0.8).roundToInt()
    val taxAmount = grossSalary - b.startingCash
    return GameState(
        cash = b.startingCash,
        currentRent = b.rent,
        difficulty = difficulty,
        seed = seed,
        log = listOf(
            "Oyun başladı ($difficulty). Brüt maaş: ${grossSalary.toLocaleString()} TL | " +
            "SGK+GV kesintisi: -${taxAmount.toLocaleString()} TL | Net: ${b.startingCash.toLocaleString()} TL"
        ),
        quest = QuestState("Bugün dolabı 40% üstünde tut", "Market yapmayı unutma.", false)
    )
}

// ─── Quest ───

private fun questForDay(s: GameState): QuestState {
    val rng = SeededRandom(s.seed + s.day * 777)

    if (!s.rentPaid && s.day >= 5)
        return QuestState("Bugün kira planını çöz", "Kira gecikirse ay dengesi hızla bozulur.")

    if (s.cardDebt >= 7000)
        return QuestState("Bugün kart borcunu azalt", "Borç baskısı büyümeden ödeme yapmak daha güvenlidir.")

    if (s.fridge <= 35)
        return QuestState("Bugün dolabı 45% üstüne taşı", "İhtiyaç tarafı çok düşerse moral de etkilenir.")

    if (s.cash < 600 && s.cardDebt > 0)
        return QuestState("Bugün en az 600 TL nakit tampon bırak", "Tamponsuz kalmak yeni harcamayı karta iter.")

    val pool = listOf(
        "Bugün dolabı 40% üstünde tut" to "Market yapmayı unutma.",
        "Bugün kart borcunu azalt" to "Asgari bile ödesen puan alırsın.",
        "Bugün en az 500 TL nakit bırak" to "Tüm parayı harcama.",
        "Bugün bir faturayı öde" to "Gecikme cezası can yakar.",
        "Bugün moralini 65% üstünde tut" to "Bazen küçük eğlence iyidir.",
        "Bugün yatırımda tek varlığa yüklenme" to "Çeşitlendirme bazen kazançtan önce riski yönetmektir."
    )
    val pick = pool[rng.nextInt(pool.size)]
    return QuestState(pick.first, pick.second, false)
}

// ─── Actions ───

sealed class ActionResult {
    data class PaymentResult(
        val state: GameState,
        val ok: Boolean,
        val chargedTo: String? = null // "cash" | "card" | null
    )
}

fun canAfford(s: GameState, amount: Int) = s.cash >= amount

private fun pay(s: GameState, amount: Int, useCard: Boolean): ActionResult.PaymentResult {
    if (amount <= 0) return ActionResult.PaymentResult(s, false)
    if (!useCard) {
        if (s.cash < amount)
            return ActionResult.PaymentResult(logPush(s, "Yetersiz bakiye: $amount TL ödenemedi."), false)
        return ActionResult.PaymentResult(s.copy(cash = s.cash - amount), true, "cash")
    }
    return ActionResult.PaymentResult(s.copy(cardDebt = s.cardDebt + amount), true, "card")
}

private fun payBill(
    s: GameState, billId: String, amount: Int, label: String, useCard: Boolean
): GameState {
    val paid = when (billId) {
        "electric" -> s.billsPaid.electric
        "gas" -> s.billsPaid.gas
        "internet" -> s.billsPaid.internet
        "dues" -> s.billsPaid.dues
        else -> false
    }
    if (paid) return logPush(s, "$label zaten ödendi.")

    val payment = pay(s, amount, useCard)
    if (!payment.ok) return payment.state

    val newBills = when (billId) {
        "electric" -> s.billsPaid.copy(electric = true)
        "gas" -> s.billsPaid.copy(gas = true)
        "internet" -> s.billsPaid.copy(internet = true)
        "dues" -> s.billsPaid.copy(dues = true)
        else -> s.billsPaid
    }
    return logPush(
        payment.state.copy(billsPaid = newBills),
        "$label ödendi: ${amount.toLocaleString()} TL (${if (payment.chargedTo == "card") "kart" else "nakit"})"
    )
}

fun applyAction(
    s0: GameState,
    action: ActionId,
    useCard: Boolean = false,
    amount: Int = 1,
    asset: HoldingId? = null
): GameState {
    val b = balanceFor(s0.difficulty)
    var s = s0

    return when (action) {
        ActionId.Grocery -> {
            val lowFridgeBoost = when {
                s0.fridge < 30 -> 0.75
                s0.fridge < 50 -> 0.4
                else -> 0.15
            }
            val cost = (b.groceryBase.toDouble() * (0.85 + lowFridgeBoost)).roundToInt()
            val payment = pay(s, cost, useCard)
            if (!payment.ok) return payment.state

            val fridgeGain = when {
                s0.fridge < 30 -> 26; s0.fridge < 50 -> 22; else -> 16
            }
            val moodGain = if (s0.fridge < 30) 4 else 2
            logPush(
                payment.state.copy(
                    fridge = clamp(payment.state.fridge + fridgeGain, 0, 100),
                    mood = clamp(payment.state.mood + moodGain, 0, 100),
                    discipline = clamp(payment.state.discipline + 1, 0, 100)
                ),
                "Market: -${cost.toLocaleString()} TL (${if (payment.chargedTo == "card") "kart" else "nakit"}), dolap +$fridgeGain"
            )
        }
        ActionId.Cinema -> {
            val cost = b.cinema
            val payment = pay(s, cost, useCard)
            if (!payment.ok) return payment.state
            val moodGain = if (s.cardDebt >= 8000) 8 else 12
            logPush(
                payment.state.copy(
                    mood = clamp(payment.state.mood + moodGain, 0, 100),
                    energy = clamp(payment.state.energy - 5, 0, 100),
                    discipline = clamp(payment.state.discipline - 1, 0, 100)
                ),
                "Sinema: -${cost.toLocaleString()} TL (${if (payment.chargedTo == "card") "kart" else "nakit"}), moral +$moodGain"
            )
        }
        ActionId.LightsOff -> {
            logPush(
                s.copy(
                    mood = clamp(s.mood + 1, 0, 100),
                    discipline = clamp(s.discipline + 2, 0, 100)
                ),
                "Tasarruf: ışıkları kapattın. (+1 moral)"
            )
        }
        ActionId.PayRent -> {
            if (s.rentPaid) return logPush(s, "Kira zaten ödendi.")
            val rentAmount = s.currentRent.takeIf { it > 0 } ?: b.rent
            val payment = pay(s, rentAmount, useCard)
            if (!payment.ok) return payment.state
            logPush(
                payment.state.copy(rentPaid = true),
                "Kira ödendi: ${rentAmount.toLocaleString()} TL (${if (payment.chargedTo == "card") "kart" else "nakit"})"
            )
        }
        ActionId.PayDues -> payBill(s, "dues", b.dues, "Aidat", useCard)
        ActionId.PayElectric -> payBill(s, "electric", b.electric, "Elektrik", useCard)
        ActionId.PayGas -> payBill(s, "gas", b.gas, "Doğalgaz", useCard)
        ActionId.PayInternet -> payBill(s, "internet", b.internet, "İnternet", useCard)

        ActionId.BuyAsset -> {
            val a = asset ?: return logPush(s, "Asset seçilmedi.")
            if (amount <= 0) return s
            val priceTL = assetPrice(s, a)
            val cost = amount * priceTL
            if (s.cash < cost) return logPush(s, "Alım için yeterli nakit yok.")

            val assetTLBefore = totalAssetValue(s)
            val projectedTotal = assetTLBefore + cost
            val currentHoldings = s.holdings[a] ?: 0
            val projectedAssetValue = (currentHoldings * priceTL) + cost
            val concentration = if (projectedTotal > 0) projectedAssetValue.toDouble() / projectedTotal else 0.0

            var moodDelta = 0
            var extraLog = ""
            if (concentration >= 0.75) {
                moodDelta = -2; extraLog = " · Uyarı: portföy tek varlıkta yoğunlaşıyor"
            } else if (concentration <= 0.45 && projectedTotal >= 2000) {
                moodDelta = 1; extraLog = " · Çeşitlendirme dengesi iyi görünüyor"
            }

            val newHoldings = s.holdings.toMutableMap()
            newHoldings[a] = currentHoldings + amount

            logPush(
                s.copy(
                    cash = s.cash - cost,
                    mood = clamp(s.mood + moodDelta, 0, 100),
                    holdings = newHoldings
                ),
                "Alım: $a +$amount ( -${cost.toLocaleString()} TL )$extraLog"
            )
        }
        ActionId.SellAsset -> {
            val a = asset ?: return logPush(s, "Asset seçilmedi.")
            if (amount <= 0) return s
            val currentHolding = s.holdings[a] ?: 0
            if (currentHolding < amount) return logPush(s, "Satacak varlık yok.")

            val priceTL = assetPrice(s, a)
            val gain = amount * priceTL
            val newHoldings = s.holdings.toMutableMap()
            newHoldings[a] = currentHolding - amount
            logPush(
                s.copy(cash = s.cash + gain, holdings = newHoldings),
                "Satış: $a -$amount ( +${gain.toLocaleString()} TL )"
            )
        }
        ActionId.PayCardMin -> {
            val minPay = max(500, (s.cardDebt.toDouble() * 0.25).roundToInt())
            if (s.cash < minPay) return logPush(s, "Asgari ödemeyi yapmak için nakit yok.")
            logPush(
                s.copy(
                    cash = s.cash - minPay,
                    cardDebt = max(0, s.cardDebt - minPay),
                    mood = clamp(s.mood + 1, 0, 100),
                    discipline = clamp(s.discipline + 2, 0, 100)
                ),
                "Kredi kartı asgari ödeme: -${minPay.toLocaleString()} TL"
            )
        }
        ActionId.PayCardAll -> {
            val amt = s.cardDebt
            if (amt <= 0) return logPush(s, "Kart borcu yok.")
            if (s.cash < amt) return logPush(s, "Kart borcunu kapatacak nakit yok.")
            logPush(
                s.copy(
                    cash = s.cash - amt,
                    cardDebt = 0,
                    mood = clamp(s.mood + 4, 0, 100),
                    discipline = clamp(s.discipline + 4, 0, 100)
                ),
                "Kredi kartı borcu kapatıldı: -${amt.toLocaleString()} TL"
            )
        }
    }
}

// ─── Helpers ───

fun assetPrice(s: GameState, h: HoldingId): Int = when (h) {
    HoldingId.Gold -> s.goldPrice
    HoldingId.Stock -> (s.stockIndex * 20)
    HoldingId.Usd -> s.usdTry
    HoldingId.Btc -> s.btcTry
    HoldingId.Eth -> s.ethTry
}

fun totalAssetValue(s: GameState): Int {
    var total = 0
    for ((h, qty) in s.holdings) {
        total += qty * assetPrice(s, h)
    }
    return total
}

// ─── Daily Simulation (simplified core) ───

private fun applyDailyExpenses(s: GameState): GameState {
    var fridge = s.fridge - 6
    var mood = s.mood
    var energy = s.energy
    var discipline = s.discipline
    var cash = s.cash

    if (fridge < 0) {
        fridge = 0; mood -= 8; energy -= 6; discipline -= 2
    } else if (fridge < 20) {
        mood -= 4; energy -= 2; discipline -= 1
    } else if (fridge < 40) {
        mood -= 1
    }

    val stressSpend = when {
        s.cardDebt >= 8000 -> 25; s.cardDebt >= 4000 -> 10; else -> 0
    }
    val lowMoodSpend = if (s.mood < 45) 15 else 0
    val daily = 80 + stressSpend + lowMoodSpend

    return if (cash >= daily) {
        s.copy(
            fridge = clamp(fridge, 0, 100), mood = clamp(mood, 0, 100),
            energy = clamp(energy, 0, 100), discipline = clamp(discipline, 0, 100),
            cash = cash - daily
        )
    } else {
        s.copy(
            fridge = clamp(fridge, 0, 100),
            mood = clamp(mood - 1, 0, 100), energy = clamp(energy, 0, 100),
            discipline = clamp(discipline - 2, 0, 100),
            cardDebt = s.cardDebt + (daily - cash), cash = 0
        )
    }
}

private fun updateMarkets(s: GameState): GameState {
    val b = balanceFor(s.difficulty)
    val rng = SeededRandom(s.seed + s.day * 1337)

    val policyDelta = (rng.next() - 0.5) * 2.0 * b.policyRateVol
    val policyRate = clamp((s.policyRate.toDouble() + policyDelta * 100.0).roundToInt(), 1000, 8000)

    val mood = s.marketMood
    var goldBias = 0.0; var stockBias = 0.0; var usdBias = 0.0; var btcBias = 0.0; var ethBias = 0.0
    when (mood) {
        MarketMood.Inflation -> { goldBias += 0.006; usdBias += 0.004; stockBias -= 0.003 }
        MarketMood.Fx -> { usdBias += 0.007; goldBias += 0.004; stockBias -= 0.002 }
        MarketMood.Credit -> { stockBias -= 0.006; btcBias -= 0.004; ethBias -= 0.004 }
        MarketMood.Equity -> { stockBias += 0.006; btcBias += 0.003; ethBias += 0.003 }
        else -> {}
    }

    fun shock(vol: Double) = (rng.next() - 0.5) * 2.0 * vol
    val rateTight = (policyRate - 4000) / 10000.0

    return s.copy(
        goldPrice = max(500, (s.goldPrice.toDouble() * (1 + shock(b.volatility.first) + goldBias + 0.002 * rateTight)).roundToInt()),
        stockIndex = max(20, (s.stockIndex.toDouble() * (1 + shock(b.volatility.second) + stockBias - 0.004 * rateTight)).roundToInt()),
        usdTry = max(500, (s.usdTry.toDouble() * (1 + shock(b.volatility.first * 1.2) + usdBias + 0.001 * rateTight)).roundToInt()),
        btcTry = max(50000, (s.btcTry.toDouble() * (1 + shock(b.volatility.second * 1.6) + btcBias)).roundToInt()),
        ethTry = max(2000, (s.ethTry.toDouble() * (1 + shock(b.volatility.second * 1.4) + ethBias)).roundToInt()),
        policyRate = policyRate,
        marketMood = MarketMood.Neutral
    )
}

private fun applyLifeEvent(s: GameState): Pair<GameState, String?> {
    val b = balanceFor(s.difficulty)
    val rng = SeededRandom(s.seed + s.day * 9001)
    val cashPressure = if (s.cash < 500) 0.25 else 0.0
    val debtPressure = clampD(s.cardDebt.toDouble() / max(1, b.startingCash * 2).toDouble(), 0.0, 0.6)
    val moodPressure = when { s.mood < 40 -> 0.25; s.mood < 55 -> 0.1; else -> 0.0 }
    val stressFactor = clampD(1.0 + cashPressure + debtPressure + moodPressure, 0.7, 1.8)

    val guestP = clampD((b.guestDaysAvg / 30.0) * (2.0 - stressFactor), 0.0, 0.6)
    if (rng.next() < guestP) {
        val cost = (b.guestCostMin + rng.next() * (b.guestCostMax - b.guestCostMin)).roundToInt()
        return if (s.cash >= cost)
            s.copy(cash = s.cash - cost, mood = clamp(s.mood + 2, 0, 100)) to
            "Misafir geldi: -${cost.toLocaleString()} TL (ikram/alışveriş)"
        else
            s.copy(cardDebt = s.cardDebt + cost, mood = clamp(s.mood - 3, 0, 100)) to
            "Misafir geldi: -${cost.toLocaleString()} TL (ikram/alışveriş)"
    }

    val roll = rng.next()
    val negThreshold = 0.12 * stressFactor
    val posThreshold = negThreshold + 0.08 * (2.0 - stressFactor)
    val spoilThreshold = posThreshold + 0.06 * stressFactor

    return when {
        roll < negThreshold -> {
            val cost = (1500 + rng.next() * 3500).roundToInt()
            if (s.cash >= cost)
                s.copy(cash = s.cash - cost, mood = clamp(s.mood - 2, 0, 100)) to
                "Beklenmedik gider: -${cost.toLocaleString()} TL (telefon/ev eşyası)"
            else
                s.copy(cardDebt = s.cardDebt + cost, mood = clamp(s.mood - 4, 0, 100)) to
                "Beklenmedik gider: -${cost.toLocaleString()} TL (telefon/ev eşyası)"
        }
        roll < posThreshold -> {
            val extra = (1000 + rng.next() * 2500).roundToInt()
            s.copy(cash = s.cash + extra, mood = clamp(s.mood + 1, 0, 100)) to
            "Ek gelir: +${extra.toLocaleString()} TL (freelance/prim)"
        }
        roll < spoilThreshold -> {
            val drop = (10 + rng.next() * 25).roundToInt()
            s.copy(fridge = clamp(s.fridge - drop, 0, 100), mood = clamp(s.mood - 2, 0, 100)) to
            "Dolap sürprizi: bazı ürünler bozuldu (-$drop% dolap)"
        }
        else -> s to null
    }
}

// ─── Next Day ───

fun nextDay(s0: GameState): GameState {
    var s = applyDailyExpenses(s0)

    // Card interest at day 30
    if (s.day == 30 && s.cardDebt > 0) {
        val b = balanceFor(s.difficulty)
        val interest = (s.cardDebt.toDouble() * b.cardMonthlyRate).roundToInt()
        s = s.copy(cardDebt = s.cardDebt + interest)
        s = logPush(s, "Ay sonu kart faizi işlendi: +${interest.toLocaleString()} TL")
    }

    // Rent increase every 10 days
    if (s.day % 10 == 0) {
        val b = balanceFor(s.difficulty)
        val maxRent = (b.rent * 1.3).roundToInt()
        if (s.currentRent < maxRent) {
            val increase = (b.rent * 0.05).roundToInt()
            val newRent = min(s.currentRent + increase, maxRent)
            s = s.copy(currentRent = newRent)
            s = logPush(s, "🏠 Enflasyon kira artışı: +${increase.toLocaleString()} TL (güncel: ${newRent.toLocaleString()} TL)")
        }
    }

    val (afterEvent, eventMsg) = applyLifeEvent(s)
    s = afterEvent
    if (eventMsg != null) s = logPush(s, eventMsg)

    // Penalty checks
    if (s.day == 10 && !s.rentPaid) {
        s = logPush(
            s.copy(mood = clamp(s.mood - 8, 0, 100), cardDebt = s.cardDebt + 300),
            "Kira gecikti: moral düştü, 300 TL gecikme (karta yazıldı)."
        )
    }
    if (s.day == 20) {
        val unpaid = listOfNotNull(
            if (!s.billsPaid.electric) "elektrik" else null,
            if (!s.billsPaid.gas) "doğalgaz" else null,
            if (!s.billsPaid.internet) "internet" else null,
            if (!s.billsPaid.dues) "aidat" else null,
        )
        if (unpaid.isNotEmpty()) {
            val penalty = 200 + unpaid.size * 60
            s = logPush(
                s.copy(mood = clamp(s.mood - 6 - unpaid.size, 0, 100), cardDebt = s.cardDebt + penalty),
                "Faturalar gecikiyor (${unpaid.joinToString(", ")}): $penalty TL ceza (karta)."
            )
        }
    }

    s = updateMarkets(s)

    val day = s.day + 1
    return if (day <= 30) {
        val logged = logPush(s.copy(day = day, quest = questForDay(s.copy(day = day))), "Gün $day başladı.")
        logged
    } else {
        s // stays at day 30
    }
}

// ─── Score ───

fun scoreEndOfMonth(s: GameState): Int {
    var score = s.points
    if (s.rentPaid) score += 20
    score += listOf(s.billsPaid.electric, s.billsPaid.gas, s.billsPaid.internet, s.billsPaid.dues).count { it } * 8
    score += clamp(s.fridge / 5, 0, 20)
    score += clamp(s.mood / 5, 0, 20)
    score += clamp(s.discipline / 6, 0, 16)
    score -= clamp(s.cardDebt / 1000, 0, 80)
    score += clamp(totalAssetValue(s) / 2000, 0, 40)
    score += clamp(s.cash / 1000, 0, 25)
    return score
}

// ─── Display Helpers ───

fun difficultyLabel(d: Difficulty): String = when (d) {
    Difficulty.Easy -> "Kolay"
    Difficulty.Normal -> "Normal"
    Difficulty.Hard -> "Zor"
}

fun Int.toLocaleString(): String =
    java.text.NumberFormat.getIntegerInstance().format(this)
