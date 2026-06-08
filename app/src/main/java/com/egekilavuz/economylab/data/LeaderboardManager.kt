package com.egekilavuz.economylab.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    val difficulty: String = "",
    val date: String = "",
    val achievements: Int = 0,
    val badge: String = "",
    val xp: Int = 0,
    val level: Int = 0,
    val levelTitle: String = ""
)

class LeaderboardManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("leaderboard_cache", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    fun saveLocalScore(entry: LeaderboardEntry) {
        val scores = getLocalScores().toMutableList()
        scores.add(entry)
        val sorted = scores.sortedByDescending { it.xp + it.score * 2 }.take(100)
        prefs.edit().putString("local_scores", json.encodeToString(sorted)).apply()
    }

    fun getLocalScores(): List<LeaderboardEntry> {
        val raw = prefs.getString("local_scores", null) ?: return emptyList()
        return try {
            json.decodeFromString<List<LeaderboardEntry>>(raw)
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun getPlayerBest(name: String): LeaderboardEntry? {
        return getLocalScores().filter { it.name == name }.maxByOrNull { it.xp + it.score * 2 }
    }

    fun fetchGlobalScores(onResult: (List<LeaderboardEntry>) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("https://api.github.com/repos/ege-kilavuz/economy-lab-android/issues?state=all&per_page=50")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Accept", "application/vnd.github.v3+json")
                conn.connectTimeout = 8000

                if (conn.responseCode != 200) {
                    withContext(Dispatchers.Main) { onResult(emptyList()) }
                    return@launch
                }

                val body = conn.inputStream.bufferedReader().readText()
                val entries = mutableListOf<LeaderboardEntry>()

                val scoreRegex = Regex("""\*\*Skor:\*\* (\d+)""")
                val nameRegex = Regex("""\*\*Oyuncu:\*\* ([^\n]+)""")
                val diffRegex = Regex("""\*\*Zorluk:\*\* ([^\n]+)""")
                val badgeRegex = Regex("""\*\*Rozet:\*\* ([^\n]+)""")

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

                withContext(Dispatchers.Main) {
                    onResult(entries.sortedByDescending { it.score }.take(50))
                }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun submitScoreToGitHub(entry: LeaderboardEntry, onResult: (Boolean) -> Unit) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val dateStr = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                val issueBody = "## Yeni Skor\n" +
                        "**Oyuncu:** ${entry.name}\n" +
                        "**Skor:** ${entry.score}\n" +
                        "**Zorluk:** ${entry.difficulty}\n" +
                        "**Rozet:** ${entry.badge}\n" +
                        "**XP:** ${entry.xp}\n" +
                        "**Seviye:** ${entry.level} ${entry.levelTitle}\n" +
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

                withContext(Dispatchers.Main) { onResult(conn.responseCode == 201) }
            } catch (_: Exception) {
                withContext(Dispatchers.Main) { onResult(false) }
            }
        }
    }
}

fun formatScoreDate(): String =
    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
