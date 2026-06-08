package com.egekilavuz.economylab.data

import org.junit.Test
import org.junit.Assert.*

class XpManagerTest {

    @Test
    fun testAllActions_havePositiveXpValues() {
        // Just verify constants exist and are positive
        assertTrue(XpManager.XP_READ_ITEM > 0)
        assertTrue(XpManager.XP_QUIZ_COMPLETE > 0)
        assertTrue(XpManager.XP_QUIZ_PERFECT > 0)
        assertTrue(XpManager.XP_GAME_FINISH > 0)
        assertTrue(XpManager.XP_GAME_FINISH == 50)
    }

    @Test
    fun testConstants_areDistinct() {
        assertNotEquals(XpManager.XP_READ_ITEM, XpManager.XP_QUIZ_COMPLETE)
        assertNotEquals(XpManager.XP_QUIZ_COMPLETE, XpManager.XP_GAME_FINISH)
    }
}
