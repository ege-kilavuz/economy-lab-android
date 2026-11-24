package com.egekilavuz.economylab.data.story

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.egekilavuz.economylab.data.Difficulty
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class StoryViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val PREFS_NAME = "story_save"
        private const val KEY_STATE = "story_state_v1"
        const val SCREEN_INTRO = "intro"
        const val SCREEN_PLAY = "play"
        const val SCREEN_JOURNAL = "journal"
        const val SCREEN_END = "end"
    }

    private val prefs: SharedPreferences =
        app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    var state: StoryState by mutableStateOf(restore() ?: newStory())
        private set

    var screen: String by mutableStateOf(initialScreen())
        private set

    var hasSave: Boolean by mutableStateOf(prefs.contains(KEY_STATE))
        private set

    private fun initialScreen(): String {
        val saved = prefs.getString(KEY_STATE, null) ?: return SCREEN_INTRO
        return try {
            val restored = json.decodeFromString<StoryState>(saved)
            if (restored.finished) SCREEN_INTRO else SCREEN_PLAY
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

    fun startNewGame(difficulty: Difficulty) {
        state = newStory(difficulty)
        screen = SCREEN_PLAY
        persist()
    }

    fun continueGame() {
        if (state.finished) {
            screen = SCREEN_INTRO
        } else {
            screen = SCREEN_PLAY
        }
    }

    fun choose(choice: StoryChoice) {
        val event = currentEvent() ?: return
        state = applyChoice(state, event, choice)
        persist()
    }

    fun nextDay() {
        state = advanceDay(state)
        persist()
        if (state.finished) screen = SCREEN_END
    }

    fun openJournal() {
        screen = SCREEN_JOURNAL
    }

    fun closeJournal() {
        screen = if (state.finished) SCREEN_END else SCREEN_PLAY
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
