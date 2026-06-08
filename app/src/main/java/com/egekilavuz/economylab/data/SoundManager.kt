package com.egekilavuz.economylab.data

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.egekilavuz.economylab.R

enum class Sfx(val resId: Int) {
    CORRECT(R.raw.success),
    WRONG(R.raw.fail),
    LEVEL_UP(R.raw.levelup),
    CLICK(R.raw.click),
    BADGE(R.raw.badge)
}

class SoundManager(context: Context) {

    private val soundPool: SoundPool
    private val loadedIds = mutableMapOf<Sfx, Int>()

    init {
        val attrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attrs)
            .build()

        Sfx.entries.forEach { sfx ->
            loadedIds[sfx] = soundPool.load(context, sfx.resId, 1)
        }
    }

    companion object {
        @Volatile
        private var instance: SoundManager? = null

        fun getInstance(context: Context): SoundManager {
            return instance ?: synchronized(this) {
                instance ?: SoundManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun play(sfx: Sfx) {
        val id = loadedIds[sfx] ?: return
        soundPool.play(id, 1f, 1f, 1, 0, 1f)
    }

    fun release() {
        soundPool.release()
    }
}
