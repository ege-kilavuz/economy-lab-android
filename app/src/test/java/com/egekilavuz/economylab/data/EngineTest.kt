package com.egekilavuz.economylab.data

import org.junit.Assert.*
import org.junit.Test

class EngineTest {

    // ─── newGame ───

    @Test
    fun `newGame should create valid state for each difficulty`() {
        for (diff in Difficulty.entries) {
            val g = newGame(diff, 12345)
            assertEquals(1, g.day)
            assertTrue(g.cash > 0)
            assertEquals(0, g.cardDebt)
            assertFalse(g.rentPaid)
            assertTrue(g.log.isNotEmpty())
            assertEquals(diff, g.difficulty)
        }
    }

    @Test
    fun `newGame should be deterministic with same seed`() {
        val a = newGame(Difficulty.Easy, 42)
        val b = newGame(Difficulty.Easy, 42)
        assertEquals(a.cash, b.cash)
        assertEquals(a.holdings, b.holdings)
    }

    @Test
    fun `newGame should produce different quests with different seeds`() {
        val titles = mutableSetOf<String>()
        for (s in 1..20) {
            var g = newGame(Difficulty.Easy, s)
            for (d in 1..3) { g = nextDay(g) }
            titles.add(g.quest.title)
        }
        assertTrue(titles.size > 1)
    }

    @Test
    fun `newGame should have different rent by difficulty`() {
        val easy = newGame(Difficulty.Easy)
        val hard = newGame(Difficulty.Hard)
        assertTrue(easy.currentRent < hard.currentRent)
    }

    // ─── applyAction ───

    @Test
    fun `payRent with cash should work`() {
        val g = newGame(Difficulty.Easy, 100)
        val result = applyAction(g, ActionId.PayRent, useCard = false)
        assertTrue(result.rentPaid)
        assertTrue(result.cash < g.cash)
        assertEquals(0, result.cardDebt)
    }

    @Test
    fun `payRent should fail when insufficient cash`() {
        val g = newGame(Difficulty.Easy, 200).copy(cash = 0)
        val result = applyAction(g, ActionId.PayRent, useCard = false)
        assertFalse(result.rentPaid)
        assertTrue(result.log[0].contains("Yetersiz bakiye"))
    }

    @Test
    fun `should not double-pay rent`() {
        val g = newGame(Difficulty.Easy, 300)
        val first = applyAction(g, ActionId.PayRent, useCard = false)
        val second = applyAction(first, ActionId.PayRent, useCard = false)
        assertTrue(second.log[0].contains("zaten ödendi"))
    }

    @Test
    fun `payRent with card should increase debt`() {
        val g = newGame(Difficulty.Easy, 400)
        val result = applyAction(g, ActionId.PayRent, useCard = true)
        assertTrue(result.rentPaid)
        assertTrue(result.cardDebt > 0)
    }

    @Test
    fun `grocery should increase fridge and decrease cash`() {
        val g = newGame(Difficulty.Easy, 500)
        val result = applyAction(g, ActionId.Grocery, useCard = false)
        assertTrue(result.cash < g.cash)
        assertTrue(result.fridge > g.fridge)
    }

    @Test
    fun `grocery with card should increase debt`() {
        val g = newGame(Difficulty.Easy, 600)
        val result = applyAction(g, ActionId.Grocery, useCard = true)
        assertTrue(result.cardDebt > 0)
        assertEquals(g.cash, result.cash)
    }

    @Test
    fun `cinema should increase mood`() {
        val g = newGame(Difficulty.Easy, 700)
        val result = applyAction(g, ActionId.Cinema, useCard = false)
        assertTrue(result.mood > g.mood)
        assertTrue(result.cash < g.cash)
    }

    @Test
    fun `lightsOff should increase discipline`() {
        val g = newGame(Difficulty.Easy, 800)
        val result = applyAction(g, ActionId.LightsOff)
        assertTrue(result.discipline > g.discipline)
        assertEquals(g.cash, result.cash)
    }

    @Test
    fun `buyAsset should add holdings`() {
        val g = newGame(Difficulty.Easy, 900)
        val result = applyAction(g, ActionId.BuyAsset, asset = HoldingId.Gold, amount = 1)
        assertEquals(1, result.holdings[HoldingId.Gold])
        assertTrue(result.cash < g.cash)
    }

    @Test
    fun `buyAsset should fail without cash`() {
        val g = newGame(Difficulty.Easy, 1000).copy(cash = 0)
        val result = applyAction(g, ActionId.BuyAsset, asset = HoldingId.Gold, amount = 1)
        assertEquals(0, result.holdings[HoldingId.Gold])
        assertTrue(result.log[0].contains("yeterli nakit"))
    }

    @Test
    fun `sellAsset should increase cash`() {
        val g = newGame(Difficulty.Easy, 1100).copy(holdings = mapOf(
            HoldingId.Gold to 5, HoldingId.Stock to 0, HoldingId.Usd to 0,
            HoldingId.Btc to 0, HoldingId.Eth to 0
        ))
        val result = applyAction(g, ActionId.SellAsset, asset = HoldingId.Gold, amount = 2)
        assertEquals(3, result.holdings[HoldingId.Gold])
        assertTrue(result.cash > g.cash)
    }

    @Test
    fun `sellAsset should fail without holdings`() {
        val g = newGame(Difficulty.Easy, 1200)
        val result = applyAction(g, ActionId.SellAsset, asset = HoldingId.Gold)
        assertTrue(result.log[0].contains("Satacak varlık"))
    }

    @Test
    fun `payCardMin should reduce debt`() {
        val g = newGame(Difficulty.Easy, 1300).copy(cardDebt = 10000, cash = 10000)
        val result = applyAction(g, ActionId.PayCardMin)
        assertTrue(result.cardDebt < g.cardDebt)
    }

    @Test
    fun `payCardMin should fail without cash`() {
        val g = newGame(Difficulty.Easy, 1400).copy(cardDebt = 10000, cash = 0)
        val result = applyAction(g, ActionId.PayCardMin)
        assertEquals(10000, result.cardDebt)
    }

    @Test
    fun `payCardAll should clear debt`() {
        val g = newGame(Difficulty.Easy, 1500).copy(cardDebt = 5000, cash = 10000)
        val result = applyAction(g, ActionId.PayCardAll)
        assertEquals(0, result.cardDebt)
        assertEquals(5000, result.cash)
    }

    @Test
    fun `payCardAll should not pay when no debt`() {
        val g = newGame(Difficulty.Easy, 1600)
        val result = applyAction(g, ActionId.PayCardAll)
        assertTrue(result.log[0].contains("Kart borcu yok"))
    }

    @Test
    fun `payBill actions should work`() {
        for (bill in listOf(ActionId.PayElectric, ActionId.PayGas, ActionId.PayInternet, ActionId.PayDues)) {
            val g = newGame(Difficulty.Easy, 1700)
            val result = applyAction(g, bill, useCard = false)
            assertTrue(result.cash < g.cash)
            val key = when (bill) {
                ActionId.PayElectric -> result.billsPaid.electric
                ActionId.PayGas -> result.billsPaid.gas
                ActionId.PayInternet -> result.billsPaid.internet
                ActionId.PayDues -> result.billsPaid.dues
                else -> false
            }
            assertTrue(key)
        }
    }

    @Test
    fun `buyAsset should warn about concentration`() {
        var g = newGame(Difficulty.Easy, 1800)
        g = applyAction(g, ActionId.BuyAsset, asset = HoldingId.Gold, amount = 3)
        assertTrue(g.log.any { it.contains("yoğunlaşıyor") })
    }

    // ─── nextDay ───

    @Test
    fun `nextDay should advance day by 1`() {
        val g = newGame(Difficulty.Easy, 2000)
        val next = nextDay(g)
        assertEquals(2, next.day)
    }

    @Test
    fun `nextDay should decrease fridge`() {
        val g = newGame(Difficulty.Easy, 2100)
        val next = nextDay(g)
        assertTrue(next.fridge < g.fridge)
    }

    @Test
    fun `nextDay should stop at day 30`() {
        var g = newGame(Difficulty.Easy, 2500)
        for (i in 2..30) { g = nextDay(g) }
        assertEquals(30, g.day)
        val final = nextDay(g)
        assertEquals(30, final.day)
    }
}
