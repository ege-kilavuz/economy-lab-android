package com.egekilavuz.economylab.data

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import java.io.File

class UpdateViewModel(app: Application) : AndroidViewModel(app) {

    private val manager = UpdateManager(app)

    val currentVersion: String = manager.currentVersionName()

    var visible: Boolean by mutableStateOf(false)
        private set
    var checking: Boolean by mutableStateOf(false)
        private set
    var info: UpdateInfo? by mutableStateOf(null)
        private set
    var progress: Float? by mutableStateOf(null)
        private set
    var error: String? by mutableStateOf(null)
        private set
    var upToDate: Boolean by mutableStateOf(false)
        private set
    var permissionRequired: Boolean by mutableStateOf(false)
        private set

    private var downloadedFile: File? = null

    fun check(force: Boolean = false) {
        if (checking) return
        checking = true
        error = null
        upToDate = false
        permissionRequired = false
        progress = null
        downloadedFile = null
        if (force) {
            visible = true
            info = null
        }

        manager.check(force) { result ->
            checking = false
            when (result) {
                is UpdateCheck.Available -> {
                    if (!force && manager.isSkipped(result.info.version)) {
                        visible = false
                    } else {
                        info = result.info
                        visible = true
                    }
                }
                UpdateCheck.UpToDate -> {
                    if (force) {
                        upToDate = true
                        visible = true
                    }
                }
                is UpdateCheck.Error -> {
                    if (force) {
                        error = result.message
                        visible = true
                    }
                }
            }
        }
    }

    fun download() {
        val update = info ?: return
        if (progress != null) return
        progress = 0f
        error = null
        permissionRequired = false
        manager.download(
            info = update,
            onProgress = { value -> progress = value },
            onDone = { file ->
                downloadedFile = file
                progress = 1f
                install()
            },
            onError = { message ->
                error = message
                progress = null
            }
        )
    }

    fun install() {
        val file = downloadedFile ?: return
        when (manager.install(file)) {
            InstallStart.Started -> {
                visible = false
                progress = null
                permissionRequired = false
            }
            InstallStart.PermissionRequired -> {
                permissionRequired = true
                progress = null
            }
            InstallStart.Failed -> {
                error = "Kurulum başlatılamadı. Dosyayı tekrar indirmeyi dene."
                progress = null
            }
        }
    }

    fun openInstallSettings() {
        manager.openInstallSettings()
    }

    fun skip() {
        info?.let { manager.skip(it.version) }
        visible = false
    }

    fun dismiss() {
        if (progress == null) {
            visible = false
            error = null
            upToDate = false
        }
    }
}
