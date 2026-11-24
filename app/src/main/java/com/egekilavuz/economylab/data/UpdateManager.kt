package com.egekilavuz.economylab.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class UpdateInfo(
    val version: String,
    val notes: String,
    val apkUrl: String,
    val apkSize: Long
)

sealed interface UpdateCheck {
    data class Available(val info: UpdateInfo) : UpdateCheck
    data object UpToDate : UpdateCheck
    data class Error(val message: String) : UpdateCheck
}

enum class InstallStart { Started, PermissionRequired, Failed }

/** "v1.7.0" > "1.6.2" gibi sürüm karşılaştırması. */
fun isNewerVersion(latest: String, current: String): Boolean {
    fun parts(value: String): List<Int> = value
        .trim()
        .removePrefix("v")
        .removePrefix("V")
        .split('.', '-', '+')
        .map { it.takeWhile(Char::isDigit).toIntOrNull() ?: 0 }

    val a = parts(latest)
    val b = parts(current)
    val size = maxOf(a.size, b.size)
    for (i in 0 until size) {
        val newPart = a.getOrElse(i) { 0 }
        val oldPart = b.getOrElse(i) { 0 }
        if (newPart > oldPart) return true
        if (newPart < oldPart) return false
    }
    return false
}

/** GitHub "latest release" JSON'undan APK bilgisini çıkarır. */
fun parseLatestRelease(jsonText: String): UpdateInfo? {
    return try {
        val root = Json.parseToJsonElement(jsonText).jsonObject
        val tag = root["tag_name"]?.jsonPrimitive?.contentOrNull ?: return null
        val notes = root["body"]?.jsonPrimitive?.contentOrNull ?: ""
        val assets = root["assets"]?.jsonArray ?: return null

        val apk = assets.mapNotNull { element ->
            val obj = element.jsonObject
            val name = obj["name"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            if (!name.endsWith(".apk", ignoreCase = true)) return@mapNotNull null
            val url = obj["browser_download_url"]?.jsonPrimitive?.contentOrNull ?: return@mapNotNull null
            val size = obj["size"]?.jsonPrimitive?.longOrNull ?: 0L
            Triple(name, url, size)
        }.firstOrNull() ?: return null

        UpdateInfo(
            version = tag.removePrefix("v").removePrefix("V"),
            notes = notes,
            apkUrl = apk.second,
            apkSize = apk.third
        )
    } catch (_: Exception) {
        null
    }
}

class UpdateManager(private val context: Context) {

    companion object {
        private const val PREFS = "app_updates"
        private const val KEY_LAST_CHECK = "last_check"
        private const val KEY_LAST_INFO = "last_info"
        private const val KEY_SKIPPED_VERSION = "skipped_version"
        private const val CHECK_INTERVAL_MS = 6 * 60 * 60 * 1000L
        private const val RELEASES_URL =
            "https://api.github.com/repos/ege-kilavuz/economy-lab-android/releases/latest"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    @Suppress("DEPRECATION")
    fun currentVersionName(): String {
        return try {
            val info = if (Build.VERSION.SDK_INT >= 33) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    android.content.pm.PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                context.packageManager.getPackageInfo(context.packageName, 0)
            }
            info.versionName ?: "0"
        } catch (_: Exception) {
            "0"
        }
    }

    fun isSkipped(version: String): Boolean =
        prefs.getString(KEY_SKIPPED_VERSION, null) == version

    fun skip(version: String) {
        prefs.edit().putString(KEY_SKIPPED_VERSION, version).apply()
    }

    fun check(force: Boolean, callback: (UpdateCheck) -> Unit) {
        val now = System.currentTimeMillis()

        if (!force) {
            val last = prefs.getLong(KEY_LAST_CHECK, 0L)
            if (now - last in 0L until CHECK_INTERVAL_MS) {
                val cached = loadCachedInfo()
                val result = if (cached != null && isNewerVersion(cached.version, currentVersionName())) {
                    UpdateCheck.Available(cached)
                } else {
                    UpdateCheck.UpToDate
                }
                callback(result)
                return
            }
        }

        GlobalScope.launch(Dispatchers.IO) {
            val result = try {
                val body = fetchLatestReleaseJson()
                val info = parseLatestRelease(body)
                prefs.edit().putLong(KEY_LAST_CHECK, now).apply()
                if (info != null) {
                    saveCachedInfo(info)
                    if (isNewerVersion(info.version, currentVersionName())) {
                        UpdateCheck.Available(info)
                    } else {
                        UpdateCheck.UpToDate
                    }
                } else {
                    UpdateCheck.UpToDate
                }
            } catch (e: Exception) {
                UpdateCheck.Error(e.message ?: "Güncelleme sunucusuna ulaşılamadı")
            }
            withContext(Dispatchers.Main) { callback(result) }
        }
    }

    fun download(
        info: UpdateInfo,
        onProgress: (Float) -> Unit,
        onDone: (File) -> Unit,
        onError: (String) -> Unit
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val dir = File(context.cacheDir, "update").apply { mkdirs() }
                dir.listFiles()?.forEach { if (it.isFile) it.delete() }

                val file = File(dir, "economy-lab-${info.version}.apk")
                val connection = URL(info.apkUrl).openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = true
                connection.connectTimeout = 15_000
                connection.readTimeout = 60_000
                connection.setRequestProperty("User-Agent", "economy-lab-android")

                if (connection.responseCode !in 200..299) {
                    throw IOException("İndirme hatası: HTTP ${connection.responseCode}")
                }

                val total = connection.contentLengthLong
                connection.inputStream.use { input ->
                    file.outputStream().use { output ->
                        val buffer = ByteArray(16 * 1024)
                        var downloaded = 0L
                        var lastPercent = -1
                        while (true) {
                            val read = input.read(buffer)
                            if (read <= 0) break
                            output.write(buffer, 0, read)
                            downloaded += read
                            if (total > 0) {
                                val percent = ((downloaded * 100) / total).toInt()
                                if (percent != lastPercent && (percent % 2 == 0 || percent == 100)) {
                                    lastPercent = percent
                                    withContext(Dispatchers.Main) { onProgress(percent / 100f) }
                                }
                            }
                        }
                    }
                }

                if (file.length() <= 0L) throw IOException("İndirilen dosya boş")
                withContext(Dispatchers.Main) { onDone(file) }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message ?: "Güncelleme indirilemedi")
                }
            }
        }
    }

    fun install(file: File): InstallStart {
        return try {
            if (Build.VERSION.SDK_INT >= 26 && !context.packageManager.canRequestPackageInstalls()) {
                openInstallSettings()
                return InstallStart.PermissionRequired
            }

            val uri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            InstallStart.Started
        } catch (_: Exception) {
            InstallStart.Failed
        }
    }

    fun openInstallSettings() {
        if (Build.VERSION.SDK_INT < 26) return
        try {
            val intent = Intent(
                Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                Uri.parse("package:${context.packageName}")
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            // Ayar ekranı yoksa sessizce geç.
        }
    }

    private fun fetchLatestReleaseJson(): String {
        val connection = URL(RELEASES_URL).openConnection() as HttpURLConnection
        connection.connectTimeout = 15_000
        connection.readTimeout = 20_000
        connection.setRequestProperty("Accept", "application/vnd.github+json")
        connection.setRequestProperty("User-Agent", "economy-lab-android")
        return try {
            if (connection.responseCode !in 200..299) {
                throw IOException("HTTP ${connection.responseCode}")
            }
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private fun saveCachedInfo(info: UpdateInfo) {
        prefs.edit().putString(KEY_LAST_INFO, json.encodeToString(info)).apply()
    }

    private fun loadCachedInfo(): UpdateInfo? {
        val raw = prefs.getString(KEY_LAST_INFO, null) ?: return null
        return try {
            json.decodeFromString<UpdateInfo>(raw)
        } catch (_: Exception) {
            null
        }
    }
}
