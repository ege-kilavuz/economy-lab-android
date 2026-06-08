package com.egekilavuz.economylab.data

import org.junit.Test
import org.junit.Assert.*

class EngineTest {

    // Helper: state with neutral defaults so bonuses can be tested in isolation
    private fun baseState(points: Int = 0) = GameState(
        points = points,
        fridge = 0,
        mood = 0,
        discipline = 0,
        cash = 0
    )

    // ─── difficultyLabel tests ───
    @Test
    fun testDifficultyLabel_returnsCorrectLabelForEasy() {
        assertEquals("Kolay", difficultyLabel(Difficulty.Easy))
    }

    @Test
    fun testDifficultyLabel_returnsCorrectLabelForNormal() {
        assertEquals("Normal", difficultyLabel(Difficulty.Normal))
    }

    @Test
    fun testDifficultyLabel_returnsCorrectLabelForHard() {
        assertEquals("Zor", difficultyLabel(Difficulty.Hard))
    }

    // ─── scoreEndOfMonth tests ───
    @Test
    fun testScoreEndOfMonth_returnsBasePoints() {
        val state = baseState(points = 100)
        assertEquals(100, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsRentBonusWhenPaid() {
        val state = baseState(points = 100).copy(rentPaid = true)
        assertEquals(120, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsBillsScore() {
        val state = baseState(points = 100).copy(
            billsPaid = BillsState(electric = true, gas = true, internet = true, dues = true)
        )
        assertEquals(132, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_subtractsDebtPenalty() {
        val state = baseState(points = 100).copy(cardDebt = 5000)
        assertEquals(95, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_capsDebtPenaltyAt80() {
        val state = baseState(points = 100).copy(cardDebt = 100000)
        assertEquals(20, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsAssetBonus() {
        val state = baseState(points = 100).copy(
            holdings = mapOf(HoldingId.Gold to 10)
        )
        assertEquals(111, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsCashBonus() {
        val state = baseState(points = 100).copy(cash = 50000)
        assertEquals(125, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsFridgeBonus() {
        val state = baseState(points = 100).copy(fridge = 80)
        assertEquals(116, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsMoodBonus() {
        val state = baseState(points = 100).copy(mood = 90)
        assertEquals(118, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_addsDisciplineBonus() {
        val state = baseState(points = 100).copy(discipline = 96)
        assertEquals(116, scoreEndOfMonth(state))
    }

    @Test
    fun testScoreEndOfMonth_combinesAllBonuses() {
        val state = baseState(points = 100).copy(
            rentPaid = true,
            billsPaid = BillsState(electric = true, gas = true, internet = true, dues = true),
            fridge = 100,
            mood = 100,
            discipline = 100,
            cardDebt = 0,
            holdings = mapOf(HoldingId.Gold to 10),
            cash = 50000
        )
        assertEquals(244, scoreEndOfMonth(state))
    }

    // ─── newGame tests ───
    @Test
    fun testNewGame_setsDefaultDifficulty() {
        val state = newGame(Difficulty.Easy)
        assertEquals(Difficulty.Easy, state.difficulty)
    }

    @Test
    fun testNewGame_initializesDayOne() {
        val state = newGame(Difficulty.Easy)
        assertEquals(1, state.day)
    }

    @Test
    fun testNewGame_initializesLog() {
        val state = newGame(Difficulty.Easy)
        assertTrue(state.log.isNotEmpty())
    }

    @Test
    fun testNewGame_setsStartingCash() {
        val state = newGame(Difficulty.Easy)
        assertEquals(17002, state.cash)
    }

    // ─── applyAction tests ───
    @Test
    fun testApplyAction_groceryDecreasesCash() {
        val state = GameState(cash = 10000, fridge = 50)
        val result = applyAction(state, ActionId.Grocery, useCard = false, amount = 1)
        assertTrue(result.cash < 10000)
    }

    @Test
    fun testApplyAction_groceryIncreasesFridge() {
        val state = GameState(cash = 10000, fridge = 50)
        val result = applyAction(state, ActionId.Grocery, useCard = false, amount = 1)
        assertTrue(result.fridge > 50)
    }

    @Test
    fun testApplyAction_payRentSetsRentPaid() {
        val state = GameState(cash = 10000, rentPaid = false)
        val result = applyAction(state, ActionId.PayRent, useCard = false)
        assertTrue(result.rentPaid)
    }

    @Test
    fun testApplyAction_payRent_insufficientCash() {
        val state = GameState(cash = 100, rentPaid = false)
        val result = applyAction(state, ActionId.PayRent, useCard = false)
        assertFalse(result.rentPaid)
    }

    @Test
    fun testApplyAction_cinemaDecreasesCash() {
        val state = GameState(cash = 10000, mood = 50)
        val result = applyAction(state, ActionId.Cinema, useCard = false)
        assertTrue(result.cash < 10000)
    }

    @Test
    fun testApplyAction_lightsOff_increasesMood() {
        val state = GameState(mood = 50)
        val result = applyAction(state, ActionId.LightsOff)
        assertTrue(result.mood > 50)
    }

    @Test
    fun testApplyAction_buyAsset_insufficientCash() {
        val state = GameState(cash = 0)
        val result = applyAction(state, ActionId.BuyAsset, useCard = false, amount = 1, asset = HoldingId.Gold)
        assertEquals(0, result.cash)
    }

    // ─── nextDay tests ───
    @Test
    fun testNextDay_incrementsDay() {
        val state = GameState(day = 5)
        val result = nextDay(state)
        assertEquals(6, result.day)
    }

    @Test
    fun testNextDay_dayDoesNotExceed30() {
        val state = GameState(day = 30)
        val result = nextDay(state)
        assertEquals(30, result.day)
    }

    @Test
    fun testNextDay_appliesDailyExpenses() {
        val state = GameState(cash = 10000)
        val result = nextDay(state)
        assertTrue(result.cash < 10000)
    }

    // ─── canAfford tests ───
    @Test
    fun testCanAfford_returnsTrueWhenSufficient() {
        val state = GameState(cash = 1000)
        assertTrue(canAfford(state, 500))
    }

    @Test
    fun testCanAfford_returnsFalseWhenInsufficient() {
        val state = GameState(cash = 100)
        assertFalse(canAfford(state, 500))
    }
}
