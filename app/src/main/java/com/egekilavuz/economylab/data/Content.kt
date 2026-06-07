package com.egekilavuz.economylab.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class JsonCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val items: List<JsonItem>,
    val quiz: List<JsonQuestion>
)

@Serializable
data class JsonItem(
    val id: String,
    val title: String,
    val short: String,
    val body: List<String>,
    val scenario: String? = null,
    val qa: List<JsonQA>? = null,
    val tips: List<String>? = null,
    val warning: String? = null,
    val video: JsonVideo? = null
)

@Serializable
data class JsonQA(val q: String, val a: String)

@Serializable
data class JsonVideo(val title: String, val url: String, val source: String? = null)

@Serializable
data class JsonQuestion(
    val id: String,
    val q: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explain: String,
    val relatedItemIds: List<String>? = null
)

@Serializable
data class JsonScenario(
    val id: String,
    val title: String,
    val categoryId: String,
    val summary: String,
    val prompt: String,
    val check: List<String>,
    val recommendedModule: String
)

@Serializable
data class JsonContent(
    val categories: List<JsonCategory>,
    val scenarios: List<JsonScenario> = emptyList(),
    val learningPath: List<String> = emptyList()
)
