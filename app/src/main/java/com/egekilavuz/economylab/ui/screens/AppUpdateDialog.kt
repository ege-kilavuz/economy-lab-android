package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.UpdateViewModel

@Composable
fun AppUpdateDialog(vm: UpdateViewModel) {
    if (!vm.visible) return

    val info = vm.info
    val error = vm.error
    val progress = vm.progress

    val title = when {
        vm.permissionRequired -> "Kurulum İzni Gerekli"
        error != null -> "Güncelleme Hatası"
        vm.upToDate -> "Güncel Sürüm"
        else -> "Yeni Sürüm Var"
    }

    AlertDialog(
        onDismissRequest = { vm.dismiss() },
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 340.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                when {
                    vm.checking -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                "Güncelleme kontrol ediliyor...",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    vm.permissionRequired -> {
                        Text(
                            "APK'yı kurabilmek için bu uygulamanın \"bilinmeyen uygulamaları yükleme\" iznine ihtiyacı var.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Ayarları açıp izni ver, sonra \"Tekrar Dene\"ye bas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    error != null -> {
                        Text(error, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "İnternet bağlantını kontrol edip tekrar deneyebilirsin.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    vm.upToDate -> {
                        Text(
                            "Güncel sürümü kullanıyorsun (v${vm.currentVersion}).",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    info != null -> {
                        Text(
                            "v${vm.currentVersion} → v${info.version}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(8.dp))

                        if (progress != null) {
                            Text(
                                "İndiriliyor... %${(progress * 100).toInt()}",
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                if (info.apkSize > 0) {
                                    "APK boyutu: ${"%.1f".format(info.apkSize / 1024.0 / 1024.0)} MB"
                                } else {
                                    ""
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                "Yenilikler",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                info.notes.take(1500).ifBlank { "Sürüm notu yok." },
                                style = MaterialTheme.typography.bodySmall
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Kurulum sırasında bilinmeyen kaynaklara izin vermen gerekebilir.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            when {
                vm.checking -> Unit
                vm.permissionRequired -> TextButton(onClick = { vm.install() }) {
                    Text("Tekrar Dene")
                }
                error != null -> TextButton(onClick = { vm.check(force = true) }) {
                    Text("Tekrar Dene")
                }
                progress != null -> Unit
                info != null -> Button(onClick = { vm.download() }) {
                    Text("İndir ve Kur", fontWeight = FontWeight.Bold)
                }
                else -> TextButton(onClick = { vm.dismiss() }) {
                    Text("Kapat")
                }
            }
        },
        dismissButton = {
            when {
                vm.checking || progress != null -> Unit
                vm.permissionRequired -> TextButton(onClick = { vm.openInstallSettings() }) {
                    Text("Ayarları Aç")
                }
                error != null -> TextButton(onClick = { vm.dismiss() }) {
                    Text("Kapat")
                }
                info != null -> TextButton(onClick = { vm.skip() }) {
                    Text("Bu sürümü atla", fontSize = 12.sp)
                }
                else -> Unit
            }
        }
    )
}
