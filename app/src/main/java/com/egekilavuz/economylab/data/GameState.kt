package com.egekilavuz.economylab.data

import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// ─── Types ───

enum class Difficulty { Easy, Normal, Hard }

enum class HoldingId { Gold, Stock, Usd, Btc, Eth }

data class QuestState(
    val title: String,
    val hint: String,
    val done: Boolean = false
)

data class BillsState(
    val electric: Boolean = false,
    val gas: Boolean = false,
    val internet: Boolean = false,
    val dues: Boolean = false
)

enum class ActionId {
    Grocery, Cinema, LightsOff, PayRent,
    PayElectric, PayGas, PayInternet, PayDues,
    BuyAsset, SellAsset, PayCardMin, PayCardAll
}

enum class MarketMood {
    Inflation, Fx, Credit, Equity, Neutral
}

data class GameState(
    val day: Int = 1,
    val cash: Int = 0,
    val cardDebt: Int = 0,
    val fridge: Int = 55,
    val mood: Int = 60,
    val energy: Int = 60,
    val discipline: Int = 55,
    val holdings: Map<HoldingId, Int> = HoldingId.entries.associateWith { 0 },
    val currentRent: Int = 7000,
    val rentPaid: Boolean = false,
    val billsPaid: BillsState = BillsState(),
    val difficulty: Difficulty = Difficulty.Easy,
    val seed: Int = 0,
    val log: List<String> = emptyList(),
    val points: Int = 0,
    val quest: QuestState = QuestState("", "", false),
    val goldPrice: Int = 2200,
    val stockIndex: Int = 100,
    val usdTry: Int = 3350, // 33.50 TL * 100
    val btcTry: Int = 2200000,
    val ethTry: Int = 120000,
    val policyRate: Int = 4500, // 45.00%
    val marketMood: MarketMood = MarketMood.Neutral
)

// ─── Helpers ───

fun clamp(n: Int, lo: Int, hi: Int) = max(lo, min(hi, n))
fun clampD(n: Double, lo: Double, hi: Double): Double = max(lo, min(hi, n))

fun moneyTL(n: Int): String = "${n.toBigDecimal().toPlainString()} TL"

fun logPush(s: GameState, msg: String) =
    s.copy(log = listOf(msg) + s.log.take(119))

// ─── Balance ───

data class Balance(
    val startingCash: Int,
    val rent: Int,
    val dues: Int,
    val electric: Int,
    val gas: Int,
    val internet: Int,
    val groceryBase: Int,
    val cinema: Int,
    val cardMonthlyRate: Double,
    val volatility: Pair<Double, Double>, // gold, stocks
    val policyRateVol: Double,
    val guestDaysAvg: Double,
    val guestCostMin: Int,
    val guestCostMax: Int
)

fun balanceFor(d: Difficulty): Balance = when (d) {
    Difficulty.Easy -> Balance(
        startingCash = 17002, rent = 7000, dues = 600, electric = 700, gas = 900,
        internet = 450, groceryBase = 450, cinema = 280, cardMonthlyRate = 0.035,
        volatility = 0.015 to 0.02, policyRateVol = 0.6,
        guestDaysAvg = 1.5, guestCostMin = 250, guestCostMax = 600
    )
    Difficulty.Normal -> Balance(
        startingCash = 17002, rent = 8000, dues = 750, electric = 850, gas = 1100,
        internet = 500, groceryBase = 520, cinema = 320, cardMonthlyRate = 0.045,
        volatility = 0.03 to 0.045, policyRateVol = 1.2,
        guestDaysAvg = 3.0, guestCostMin = 350, guestCostMax = 900
    )
    Difficulty.Hard -> Balance(
        startingCash = 17002, rent = 9000, dues = 950, electric = 1000, gas = 1300,
        internet = 550, groceryBase = 600, cinema = 380, cardMonthlyRate = 0.055,
        volatility = 0.06 to 0.09, policyRateVol = 2.0,
        guestDaysAvg = 5.0, guestCostMin = 450, guestCostMax = 1400
    )
}
