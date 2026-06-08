package com.egekilavuz.economylab.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object ContentLoader {
    private val json = Json { ignoreUnknownKeys = true }

    private var cached: JsonContent? = null
    private var remoteVersion: Int = 0

    /**
     * Load content: tries cached file first, then bundled assets.
     * Also triggers a background check for remote updates.
     */
    fun load(context: Context): JsonContent {
        cached?.let { return it }

        // Try cached file first
        val cacheFile = File(context.filesDir, "content.json")
        if (cacheFile.exists()) {
            try {
                val text = cacheFile.readText()
                val content = json.decodeFromString<JsonContent>(text)
                cached = content
                return content
            } catch (_: Exception) {
                cacheFile.delete()
            }
        }

        // Fall back to bundled assets
        val jsonString = context.assets.open("content.json")
            .bufferedReader()
            .use { it.readText() }

        val content = json.decodeFromString<JsonContent>(jsonString)
        cached = content
        return content
    }

    /**
     * Check for remote update in background.
     * Call this from the main composable on start.
     */
    fun checkForUpdate(context: Context, onResult: ((Boolean, String) -> Unit)? = null) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val bundled = withContext(Dispatchers.IO) {
                    context.assets.open("content.json").bufferedReader().use { it.readText() }
                }

                val bundledJson = json.decodeFromString<JsonContent>(bundled)
                val currentVersion = bundledJson._version

                val url = bundledJson._updateUrl.ifEmpty {
                    "https://raw.githubusercontent.com/ege-kilavuz/economy-lab-android/main/app/src/main/assets/content.json"
                }

                val conn = URL(url).openConnection() as HttpURLConnection
                conn.connectTimeout = 5000
                conn.readTimeout = 5000

                if (conn.responseCode != 200) {
                    withContext(Dispatchers.Main) { onResult?.invoke(false, "Sunucu yanıt vermedi (${conn.responseCode})") }
                    return@launch
                }

                val remoteText = conn.inputStream.bufferedReader().use { it.readText() }
                val remoteJson = json.decodeFromString<JsonContent>(remoteText)

                if (remoteJson._version > currentVersion) {
                    val cacheFile = File(context.filesDir, "content.json")
                    cacheFile.writeText(remoteText)
                    cached = remoteJson
                    remoteVersion = remoteJson._version
                    withContext(Dispatchers.Main) { onResult?.invoke(true, "İçerik güncellendi (v${currentVersion} → v${remoteJson._version})") }
                } else {
                    withContext(Dispatchers.Main) { onResult?.invoke(false, "Zaten güncel (v$currentVersion)") }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { onResult?.invoke(false, "Güncelleme kontrolü başarısız: ${e.localizedMessage ?: "bağlantı hatası"}") }
            }
        }
    }

    fun getCachedVersion(): Int = cached?._version ?: 0
    fun getRemoteVersion(): Int = remoteVersion

    fun getCategoryById(context: Context, id: String): JsonCategory? {
        return load(context).categories.find { it.id == id }
    }

    fun getScenarioById(context: Context, id: String): JsonScenario? {
        return load(context).scenarios.find { it.id == id }
    }
}
