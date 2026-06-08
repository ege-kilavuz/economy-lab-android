package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@Serializable
data class LeaderboardEntry(
    val name: String,
    val score: Int,
    val difficulty: String,
    val date: String,
    val achievements: Int = 0,
    val badge: String = ""
)

class LeaderboardManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("leaderboard_cache", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun saveLocalScore(entry: LeaderboardEntry) {
        val scores = getLocalScores().toMutableList()
        scores.add(entry)
        val sorted = scores.sortedByDescending { it.score }.take(100)
        val data = StringBuilder().apply {
            append("[")
            sorted.forEachIndexed { i, e ->
                if (i > 0) append(",")
                append("""{"name":"${e.name}","score":${e.score},"difficulty":"${e.difficulty}","date":"${e.date}","achievements":${e.achievements},"badge":"${e.badge}"}""")
            }
            append("]")
        }.toString()
        prefs.edit().putString("local_scores", data).apply()
    }

    fun getLocalScores(): List<LeaderboardEntry> {
        val raw = prefs.getString("local_scores", null) ?: return emptyList()
        return try {
            val entries = mutableListOf<LeaderboardEntry>()
            val parts = raw.split("},{")
            parts.forEach { part ->
                val name = Regex(""""name":"([^"]+)""").find(part)?.groupValues?.get(1) ?: return@forEach
                val score = Regex(""""score":(\d+)""").find(part)?.groupValues?.get(1)?.toIntOrNull() ?: return@forEach
                val difficulty = Regex(""""difficulty":"([^"]+)""").find(part)?.groupValues?.get(1) ?: ""
                val date = Regex(""""date":"([^"]+)""").find(part)?.groupValues?.get(1) ?: ""
                val achievements = Regex(""""achievements":(\d+)""").find(part)?.groupValues?.get(1)?.toIntOrNull() ?: 0
                val badge = Regex(""""badge":"([^"]+)""").find(part)?.groupValues?.get(1) ?: ""
                entries.add(LeaderboardEntry(name, score, difficulty, date, achievements, badge))
            }
            entries.sortedByDescending { it.score }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getPlayerBest(name: String): LeaderboardEntry? {
        return getLocalScores().filter { it.name == name }.maxByOrNull { it.score }
    }

    fun fetchGlobalScores(onResult: (List<LeaderboardEntry>) -> Unit) {
        Thread {
            try {
                val url = URL("https://api.github.com/repos/ege-kilavuz/economy-lab-android/issues?state=all&per_page=50")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.connectTimeout = 8000

                if (conn.responseCode != 200) {
                    onResult(emptyList())
                    return@Thread
                }

                val body = conn.inputStream.bufferedReader().readText()
                val entries = mutableListOf<LeaderboardEntry>()

                val issueRegex = Regex(""""title":"Skor: ([^-]+) - (\d+)""")
                val scoreRegex = Regex("""\*\*Skor:\*\* (\d+)""")
                val nameRegex = Regex("""\*\*Oyuncu:\*\* ([^\n]+)""")
                val diffRegex = Regex("""\*\*Zorluk:\*\* ([^\n]+)""")
                val badgeRegex = Regex("""\*\*Rozet:\*\* ([^\n]+)""")

                // Parse each issue body for scores
                val issues = Regex("\"body\":\"([^\"]+)\"").findAll(body)
                for (issue in issues) {
                    val issueBody = issue.groupValues[1].replace("\\n", "\n").replace("\\\"", "\"")
                    val name = nameRegex.find(issueBody)?.groupValues?.get(1)?.trim() ?: continue
                    val scoreStr = scoreRegex.find(issueBody)?.groupValues?.get(1) ?: continue
                    val score = scoreStr.toIntOrNull() ?: continue
                    val difficulty = diffRegex.find(issueBody)?.groupValues?.get(1)?.trim() ?: ""
                    val badge = badgeRegex.find(issueBody)?.groupValues?.get(1)?.trim() ?: ""
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                    entries.add(LeaderboardEntry(name, score, difficulty, date, 0, badge))
                }

                onResult(entries.sortedByDescending { it.score }.take(50))
            } catch (_: Exception) {
                onResult(emptyList())
            }
        }.start()
    }

    fun submitScoreToGitHub(entry: LeaderboardEntry, onResult: (Boolean) -> Unit) {
        Thread {
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val issueBody = "## Yeni Skor\n" +
                        "**Oyuncu:** ${entry.name}\n" +
                        "**Skor:** ${entry.score}\n" +
                        "**Zorluk:** ${entry.difficulty}\n" +
                        "**Rozet:** ${entry.badge}\n" +
                        "**Tarih:** $dateStr"

                val issueUrl = URL("https://api.github.com/repos/ege-kilavuz/economy-lab-android/issues")
                val conn = issueUrl.openConnection() as HttpURLConnection
                conn.doOutput = true
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")

                val title = "Skor: ${entry.name} - ${entry.score}"
                val payload = """{"title":"$title","body":${Json.encodeToString(issueBody)}}"""
                conn.outputStream.write(payload.toByteArray())

                onResult(conn.responseCode == 201)
            } catch (_: Exception) {
                onResult(false)
            }
        }.start()
    }
}

fun formatScoreDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
