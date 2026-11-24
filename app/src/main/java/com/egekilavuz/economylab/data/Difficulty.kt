package com.egekilavuz.economylab.data

import kotlinx.serialization.Serializable

@Serializable
enum class Difficulty { Easy, Normal, Hard }

fun difficultyLabel(d: Difficulty): String = when (d) {
    Difficulty.Easy -> "Kolay"
    Difficulty.Normal -> "Normal"
    Difficulty.Hard -> "Zor"
}
