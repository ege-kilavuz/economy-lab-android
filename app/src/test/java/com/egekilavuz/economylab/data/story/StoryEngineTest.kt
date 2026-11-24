package com.egekilavuz.economylab.data.story

import com.egekilavuz.economylab.data.Difficulty
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class StoryEngineTest {

    private fun firstChoice(day: Int = 1, state: StoryState = newStory(Difficulty.Normal, seed = 42)): StoryChoice {
        val event = eventForDay(day, state)
        assertNotNull("Gün $day için olay bulunamadı", event)
        return event!!.choices.first()
    }

    // ── Yeni oyun ──

    @Test
    fun newStory_startsOnDayOne() {
        assertEquals(1, newStory(Difficulty.Normal, seed = 1).day)
    }

    @Test
    fun newStory_appliesDifficultyStartingStats() {
        val easy = newStory(Difficulty.Easy, seed = 1)
        val hard = newStory(Difficulty.Hard, seed = 1)
        assertTrue(easy.stats.cash > hard.stats.cash)
        assertTrue(easy.stats.debt < hard.stats.debt)
        assertTrue(easy.stats.knowledge > hard.stats.knowledge)
    }

    @Test
    fun newStory_allDaysHaveEvents() {
        var state = newStory(Difficulty.Normal, seed = 7)
        for (day in 1..totalChapters()) {
            state = state.copy(day = day)
            assertNotNull("Gün $day olayı eksik", eventForDay(day, state))
        }
    }

    @Test
    fun everyEventHasAtLeastTwoChoices() {
        STORY_EVENTS.forEach { event ->
            assertTrue("${event.title} için seçenek az", event.choices.size >= 2)
        }
        GENERIC_SCENARIOS.forEach { event ->
            assertTrue("${event.title} için seçenek az", event.choices.size >= 2)
        }
        EXTRA_SCENARIOS.forEach { event ->
            assertTrue("${event.title} için seçenek az", event.choices.size >= 2)
        }
        CHARACTER_SCENARIOS.forEach { event ->
            assertTrue("${event.title} için seçenek az", event.choices.size >= 2)
        }
        DILEMMA_SCENARIOS.forEach { event ->
            assertTrue("${event.title} için seçenek az", event.choices.size >= 2)
        }
    }

    // ── Seçim uygulama ──

    @Test
    fun applyChoice_spendsCash() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val choice = event.choices.first { it.id == "d1-debt" }
        val result = applyChoice(state, event, choice)
        assertEquals(state.stats.cash - 4_000, result.stats.cash)
    }

    @Test
    fun applyChoice_paysDebt() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val choice = event.choices.first { it.id == "d1-debt" }
        val result = applyChoice(state, event, choice)
        assertEquals(0, result.stats.debt)
    }

    @Test
    fun applyChoice_recordsResultAndBlocksSecondChoice() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val once = applyChoice(state, event, event.choices.first())
        assertNotNull(once.pendingResult)
        val twice = applyChoice(once, event, event.choices.last())
        assertEquals(once, twice)
    }

    @Test
    fun applyChoice_overflowBecomesCardDebt() {
        val expensive = StoryChoice(
            id = "test",
            label = "Test",
            effect = ChoiceEffect(cash = -100_000),
            result = "test"
        )
        val state = newStory(Difficulty.Normal, seed = 3).copy(day = 1)
        val event = eventForDay(1, state)!!
        val result = applyChoice(state, event, expensive)
        assertEquals(0, result.stats.cash)
        assertTrue(result.stats.debt > state.stats.debt)
    }

    @Test
    fun applyChoice_lockedChoiceIsRejected() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val locked = StoryChoice(
            id = "locked",
            label = "Kilitli",
            requires = ChoiceRequirement(minKnowledge = 99),
            result = "olmamalı"
        )
        val event = eventForDay(1, state)!!
        val result = applyChoice(state, event, locked)
        assertEquals(state.pendingResult, result.pendingResult)
    }

    // ── Gün ilerletme ──

    @Test
    fun advanceDay_incrementsDayAndClearsResult() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val resolved = applyChoice(state, event, event.choices.first())
        val next = advanceDay(resolved)
        assertEquals(2, next.day)
        assertEquals(null, next.pendingResult)
    }

    @Test
    fun advanceDay_withoutChoiceDoesNothing() {
        val state = newStory(Difficulty.Normal, seed = 3)
        assertEquals(state, advanceDay(state))
    }

    @Test
    fun advanceDay_onLastDayFinishesGame() {
        val state = newStory(Difficulty.Normal, seed = 3).copy(day = totalChapters())
        val event = eventForDay(state.day, state)!!
        val resolved = applyChoice(state, event, event.choices.first())
        val finished = advanceDay(resolved)
        assertTrue(finished.finished)
        assertEquals(totalChapters(), finished.day)
    }

    // ── Puanlama ──

    @Test
    fun storyScore_staysWithinRange() {
        val state = newStory(Difficulty.Hard, seed = 11)
        val score = storyScore(state)
        assertTrue(score in 0..100)
    }

    @Test
    fun investmentValue_isDeterministic() {
        val state = newStory(Difficulty.Normal, seed = 99)
            .copy(stats = StoryStats(invested = 10_000))
        assertEquals(investmentValue(state), investmentValue(state))
        assertTrue(investmentValue(state) > 0)
    }

    @Test
    fun archetype_returnsNonEmpty() {
        val archetype = archetypeFor(newStory(Difficulty.Easy, seed = 5))
        assertTrue(archetype.title.isNotEmpty())
        assertTrue(archetype.emoji.isNotEmpty())
    }

    @Test
    fun flags_areAppliedAndCleared() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val choice = StoryChoice(
            id = "flag",
            label = "Flag",
            effect = ChoiceEffect(setFlags = listOf("a", "b")),
            result = "ok"
        )
        val event = eventForDay(1, state)!!
        val withFlags = applyChoice(state, event, choice)
        assertTrue(withFlags.flags.containsAll(listOf("a", "b")))

        val cleanState = withFlags.copy(pendingResult = null)
        val clear = StoryChoice(
            id = "clear",
            label = "Clear",
            effect = ChoiceEffect(clearFlags = listOf("a")),
            result = "ok"
        )
        val cleared = applyChoice(cleanState, event, clear)
        assertFalse(cleared.flags.contains("a"))
        assertTrue(cleared.flags.contains("b"))
    }

    // ── Nakit yeterliliği ──

    @Test
    fun requiredCash_spendIsRequired() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val choice = StoryChoice(
            id = "spend",
            label = "Harca",
            effect = ChoiceEffect(cash = -5_000),
            result = "ok"
        )
        assertEquals(5_000, requiredCash(state, choice))
    }

    @Test
    fun requiredCash_investmentCounts() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val choice = StoryChoice(
            id = "invest",
            label = "Yatır",
            effect = ChoiceEffect(invest = 3_000),
            result = "ok"
        )
        assertEquals(3_000, requiredCash(state, choice))
    }

    @Test
    fun requiredCash_cardPaymentNeedsNoCash() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val choice = StoryChoice(
            id = "card",
            label = "Kartla",
            effect = ChoiceEffect(debt = 9_000),
            result = "ok"
        )
        assertEquals(0, requiredCash(state, choice))
    }

    @Test
    fun canAffordChoice_falseWhenCashInsufficient() {
        val state = newStory(Difficulty.Normal, seed = 3)
            .copy(stats = StoryStats(cash = 1_000))
        val choice = StoryChoice(
            id = "big",
            label = "Büyük harcama",
            effect = ChoiceEffect(cash = -5_000),
            result = "ok"
        )
        assertFalse(canAffordChoice(state, choice))
        assertTrue(lockReasonFor(state, choice) != null)
    }

    @Test
    fun applyChoice_doesNotBorrowForInvestment() {
        val state = newStory(Difficulty.Normal, seed = 3)
            .copy(stats = StoryStats(cash = 3_000, debt = 0))
        val choice = StoryChoice(
            id = "overinvest",
            label = "Fazla yatırım",
            effect = ChoiceEffect(invest = 10_000),
            result = "ok"
        )
        val event = eventForDay(1, state)!!
        val result = applyChoice(state, event, choice)
        assertEquals(3_000, result.stats.invested)
        assertEquals(0, result.stats.debt)
        assertEquals(0, result.stats.cash)
    }

    // ── Hedefler / rozetler / canlı ekonomi ──

    @Test
    fun goalForDay_isAchievableEveryDay() {
        for (day in 1..totalChapters()) {
            val state = newStory(Difficulty.Normal, seed = 100 + day).copy(day = day)
            val event = eventForDay(day, state)!!
            val goal = goalForDay(state, event)
            assertNotNull("Gün $day için hedef yok", goal)
            val affordable = event.choices.filter { canAffordChoice(state, it) }
            val achievable = affordable.any { goal!!.check(state, previewStats(state, it), it) }
            assertTrue("Gün $day hedefi hiçbir seçenekle tutmuyor", achievable)
        }
    }

    @Test
    fun choiceAwardsXp() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val result = applyChoice(state, event, event.choices.first { it.id == "d1-debt" })
        assertTrue(result.lastXpGain >= 5)
        assertEquals(result.lastXpGain, result.log.last().xp)
        assertEquals(result.lastXpGain, result.totalXp)
    }

    @Test
    fun weeklyInterestIsApplied() {
        val state = newStory(Difficulty.Normal, seed = 1).copy(
            day = 6,
            stats = StoryStats(cash = 20_000, debt = 10_000)
        ).copy(pendingResult = StoryLogEntry(6, "test", "test", "test"))
        val next = advanceDay(state)
        assertEquals(7, next.day)
        assertTrue(next.stats.debt > 10_000)
        assertNotNull(next.notice)
    }

    @Test
    fun portfolioMovesDaily() {
        val state = newStory(Difficulty.Normal, seed = 3).copy(
            day = 5,
            stats = StoryStats(cash = 10_000, invested = 10_000, portfolioValue = 10_000),
            flags = setOf("investor", "gold")
        )
        val market = nextMarket(state.market, state.seed, 6)
        val value = nextPortfolio(state, market)
        assertTrue(value > 0)
    }

    @Test
    fun badgesUnlockFromStats() {
        val state = newStory(Difficulty.Normal, seed = 2).copy(
            day = 12,
            stats = StoryStats(knowledge = 65, invested = 1_000, portfolioValue = 1_000, debt = 0)
        )
        val ids = evaluateBadges(state)
        assertTrue(ids.contains("knowledge"))
        assertTrue(ids.contains("first-invest"))
        assertTrue(ids.contains("debt-free"))
    }

    @Test
    fun endingReflectsDebt() {
        val state = newStory(Difficulty.Normal, seed = 1)
            .copy(stats = StoryStats(debt = 40_000))
        assertEquals("debt-spiral", endingFor(state).id)
    }

    @Test
    fun actSummaryAggregates() {
        val state = newStory(Difficulty.Normal, seed = 3)
        val event = eventForDay(1, state)!!
        val after = applyChoice(state, event, event.choices.first())
        val summary = actSummaryFor(after, 1)
        assertEquals(1, summary.entries.size)
        assertTrue(summary.xp > 0)
    }

    // ── Senaryo çeşitliliği / tekrar kontrolü ──

    @Test
    fun genericScenariosAppearAcrossSeeds() {
        val keys = (1..80).map { seed ->
            val state = newStory(Difficulty.Normal, seed).copy(day = 5)
            eventKey(eventForDay(5, state)!!)
        }.toSet()
        assertTrue("Farklı seed'lerde tek senaryo çıkıyor", keys.size > 1)
        assertTrue("Genel senaryolar hiç seçilmiyor", keys.any { it.startsWith("gen-") })
    }

    @Test
    fun scenariosDoNotRepeatWithinRun() {
        var state = newStory(Difficulty.Normal, seed = 777)
        var guard = 0
        while (!state.finished && guard < 60) {
            guard++
            val event = eventForDay(state.day, state)!!
            val choice = event.choices.first { canAffordChoice(state, it) }
            state = advanceDay(applyChoice(state, event, choice))
        }
        assertTrue(state.finished)
        assertEquals(30, state.seenScenarios.size)
        assertEquals(30, state.seenScenarios.distinct().size)
    }

    @Test
    fun seenScenarioIsSkippedWhenAlternativeExists() {
        val base = newStory(Difficulty.Normal, seed = 5).copy(day = 5)
        val scripted = STORY_EVENTS.first { it.day == 5 }
        val scriptedKey = eventKey(scripted)
        val state = base.copy(seenScenarios = listOf(scriptedKey))
        val picked = eventForDay(5, state)!!
        assertNotEquals(scriptedKey, eventKey(picked))
    }

    @Test
    fun alreadySeenScenariosAreCarriedIntoNewRun() {
        val seen = setOf("gen-subscription", "5:Market Günü")
        val state = newStory(Difficulty.Normal, seed = 9, alreadySeen = seen)
        assertTrue(state.seenScenarios.containsAll(seen))
    }

    // ── Hedef / ilişki / gecikmeli sonuç ──

    @Test
    fun objectiveIsStoredInNewStory() {
        val state = newStory(Difficulty.Normal, seed = 1, objective = StoryObjective.Investor)
        assertEquals(StoryObjective.Investor, state.objective)
    }

    @Test
    fun objectiveReachedForDebtFree() {
        val state = newStory(Difficulty.Normal, seed = 1).copy(
            objective = StoryObjective.DebtFree,
            stats = StoryStats(debt = 0)
        )
        assertTrue(objectiveReached(state))
        assertEquals(1f, objectiveProgress(state))
    }

    @Test
    fun relationRequirementBlocksChoice() {
        val requirement = ChoiceRequirement(minBoss = 70, note = "Patron 70+")
        val state = newStory(Difficulty.Normal, seed = 1)
            .copy(stats = StoryStats(boss = 50))
        assertFalse(requirementMet(state, requirement))
    }

    @Test
    fun choiceUpdatesRelations() {
        val state = newStory(Difficulty.Normal, seed = 1).copy(day = 2)
        val event = eventForDay(2, state)!!
        val choice = event.choices.first { it.id == "d2-cash" }
        val after = applyChoice(state, event, choice)
        assertTrue(after.stats.landlord > state.stats.landlord)
    }

    @Test
    fun pendingEffectFiresOnDueDay() {
        val state = newStory(Difficulty.Normal, seed = 42).copy(
            day = 1,
            stats = StoryStats(cash = 10_000),
            pendingResult = StoryLogEntry(1, "test", "test", "test"),
            pendingEffects = listOf(
                PendingEffect(
                    id = "test-effect",
                    title = "Test sonucu",
                    dueDay = 2,
                    message = "Gecikmeli etki çalıştı.",
                    cash = 500
                )
            )
        )
        val next = advanceDay(state)
        assertTrue(next.notice?.contains("Test sonucu") == true)
        assertTrue(next.pendingEffects.isEmpty())
    }
}
