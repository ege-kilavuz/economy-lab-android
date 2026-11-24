package com.egekilavuz.economylab.data.story

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.egekilavuz.economylab.data.Difficulty
import com.egekilavuz.economylab.data.XpManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoryViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val PREFS_NAME = "story_save"
        private const val KEY_STATE = "story_state_v1"
        private const val KEY_QUICK = "quick_mode"
        private const val CHECKPOINT_XP = 25

        const val SCREEN_INTRO = "intro"
        const val SCREEN_PLAY = "play"
        const val SCREEN_JOURNAL = "journal"
        const val SCREEN_CHECKPOINT = "checkpoint"
        const val SCREEN_END = "end"
    }

    private val prefs: SharedPreferences =
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }
    private val xpManager = XpManager(app)

    var state: StoryState by mutableStateOf(restore() ?: newStory())
        private set

    var screen: String by mutableStateOf(initialScreen())
        private set

    var hasSave: Boolean by mutableStateOf(prefs.contains(KEY_STATE))
        private set

    var quickMode: Boolean by mutableStateOf(prefs.getBoolean(KEY_QUICK, false))
        private set

    private fun initialScreen(): String {
        val saved = prefs.getString(KEY_STATE, null) ?: return SCREEN_INTRO
        return try {
            val restored = json.decodeFromString<StoryState>(saved)
            when {
                restored.checkpointAct > 0 -> SCREEN_CHECKPOINT
                restored.finished -> SCREEN_INTRO
                else -> SCREEN_PLAY
            }
        } catch (_: Exception) {
            SCREEN_INTRO
        }
    }

    private fun restore(): StoryState? {
        val raw = prefs.getString(KEY_STATE, null) ?: return null
        return try {
            json.decodeFromString<StoryState>(raw)
        } catch (_: Exception) {
            null
        }
    }

    private fun persist() {
        prefs.edit().putString(KEY_STATE, json.encodeToString(state)).apply()
        hasSave = true
    }

    fun currentEvent(): StoryEvent? = eventForDay(state.day, state)

    fun toggleQuickMode() {
        quickMode = !quickMode
        prefs.edit().putBoolean(KEY_QUICK, quickMode).apply()
    }

    fun startNewGame(difficulty: Difficulty) {
        state = newStory(difficulty)
        screen = SCREEN_PLAY
        persist()
    }

    fun continueGame() {
        screen = when {
            state.checkpointAct > 0 -> SCREEN_CHECKPOINT
            state.finished -> SCREEN_INTRO
            else -> SCREEN_PLAY
        }
    }

    fun choose(choice: StoryChoice) {
        if (state.pendingResult != null || state.finished) return
        val event = currentEvent() ?: return
        val before = state
        val updated = applyChoice(state, event, choice)
        if (updated.log.size == before.log.size) return
        state = updated
        if (updated.lastXpGain > 0) {
            xpManager.addXp(updated.lastXpGain)
        }
        persist()
    }

    /** Sonuç ekranından sonraki gün. 10 ve 20. günlerde periyot özeti gösterir. */
    fun nextDay() {
        if (state.finished || state.checkpointAct > 0) return

        if (state.pendingResult != null && state.day in listOf(10, 20)) {
            val act = actOf(state.day)
            if (act !in state.claimedActs) {
                state = state.copy(
                    claimedActs = state.claimedActs + act,
                    checkpointAct = act,
                    totalXp = state.totalXp + CHECKPOINT_XP
                )
                xpManager.addXp(CHECKPOINT_XP)
                screen = SCREEN_CHECKPOINT
                persist()
                return
            }
        }

        state = advanceDay(state)
        persist()
        screen = if (state.finished) SCREEN_END else SCREEN_PLAY
    }

    fun continueFromCheckpoint() {
        state = advanceDay(state.copy(checkpointAct = 0))
        screen = if (state.finished) SCREEN_END else SCREEN_PLAY
        persist()
    }

    fun openJournal() {
        screen = SCREEN_JOURNAL
    }

    fun closeJournal() {
        screen = when {
            state.checkpointAct > 0 -> SCREEN_CHECKPOINT
            state.finished -> SCREEN_END
            else -> SCREEN_PLAY
        }
    }

    fun goToIntro() {
        screen = SCREEN_INTRO
    }

    fun showEndScreen() {
        if (state.finished) screen = SCREEN_END
    }

    fun resetProgress() {
        prefs.edit().remove(KEY_STATE).apply()
        hasSave = false
        state = newStory()
        screen = SCREEN_INTRO
    }
}
