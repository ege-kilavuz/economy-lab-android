package com.egekilavuz.economylab.data

import android.content.Context
import kotlinx.serialization.json.Json

object ContentLoader {
    private val json = Json { ignoreUnknownKeys = true }

    private var cached: JsonContent? = null

    fun load(context: Context): JsonContent {
        cached?.let { return it }

        val jsonString = context.assets.open("content.json")
            .bufferedReader()
            .use { it.readText() }

        val content = json.decodeFromString<JsonContent>(jsonString)
        cached = content
        return content
    }

    fun getCategoryById(context: Context, id: String): JsonCategory? {
        return load(context).categories.find { it.id == id }
    }

    fun getScenarioById(context: Context, id: String): JsonScenario? {
        return load(context).scenarios.find { it.id == id }
    }
}
