package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.LevelInfo
import com.egekilavuz.economylab.data.XpManager
import com.egekilavuz.economylab.data.XpSummary

@Composable
fun XpBar(xpManager: XpManager) {
    // global revision değişince yeniden okunur (oyun içi XP kazanımları dahil)
    val revision = XpManager.revision
    val currentSummary = remember(revision) { xpManager.getSummary() }
    var showDetails by remember { mutableStateOf(false) }

    val animatedProgress by animateFloatAsState(
        targetValue = currentSummary.progress,
        animationSpec = tween(600), label = "xpProgress"
    )

    Card(
        onClick = { showDetails = true },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Level + title
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Seviye ${currentSummary.level}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = currentSummary.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // XP bar + count
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${currentSummary.total} XP",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(2.dp))
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.width(100.dp).height(6.dp).clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                Text(
                    text = "${currentSummary.nextLevelXp} XP",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.width(6.dp))
            Text(
                text = "›",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (showDetails) {
        XpDetailsDialog(
            summary = currentSummary,
            streak = xpManager.getStreak(),
            onDismiss = { showDetails = false }
        )
    }
}

@Composable
private fun XpDetailsDialog(
    summary: XpSummary,
    streak: Int,
    onDismiss: () -> Unit
) {
    val levels: List<LevelInfo> = XpManager.allLevels()
    val nextLevel = levels.firstOrNull { it.level == summary.level + 1 }
    val remaining = (summary.nextLevelXp - summary.total).coerceAtLeast(0)

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Kapat") }
        },
        title = {
            Column {
                Text(
                    "Seviye ${summary.level}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    summary.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${summary.total} XP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${summary.level} / ${XpManager.maxLevel}. seviye",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { summary.progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (nextLevel != null) {
                        "Sonraki seviye: ${nextLevel.title} · $remaining XP kaldı"
                    } else {
                        "Maksimum seviyeye ulaştın. Yeni seviyeler yakında!"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(12.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(Modifier.height(10.dp))

                Text(
                    "Seviyeler",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                levels.forEach { level ->
                    val reached = summary.total >= level.xpRequired
                    val isCurrent = level.level == summary.level
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isCurrent) "▶" else if (reached) "✓" else "•",
                            modifier = Modifier.width(20.dp),
                            color = if (reached) MaterialTheme.colorScheme.secondary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = level.title,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (reached) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${level.xpRequired} XP",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (reached) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp)
                Spacer(Modifier.height(10.dp))

                Text(
                    "🔥 Gün serisi: $streak gün",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )

                Spacer(Modifier.height(10.dp))
                Text(
                    "XP nasıl kazanılır?",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text("• İçerik okuma +10 · Quiz +20 · Günlük quiz +15", style = MaterialTheme.typography.bodySmall)
                Text("• Mini oyun +5 · Oyun bitirme +50", style = MaterialTheme.typography.bodySmall)
                Text("• Oyun içi bölüm +5 · hedef +10 · rozet +20", style = MaterialTheme.typography.bodySmall)
                Text("• Başarım rozeti +25", style = MaterialTheme.typography.bodySmall)
            }
        }
    )
}

@Composable
fun XpNotification(xp: Int, action: String, onDismiss: () -> Unit) {
    val emoji = when {
        xp >= 30 -> "🎉"
        xp >= 15 -> "👍"
        else -> "✨"
    }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$emoji ", fontSize = 18.sp)
            Text("+$xp XP", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)

            Spacer(Modifier.width(8.dp))

            val label = when (action) {
                "read_item" -> "İçerik okundu"
                "quiz_complete" -> "Quiz tamamlandı"
                "quiz_perfect" -> "Mükemmel quiz!"
                "game_finish" -> "Oyun bitti"
                "mini_game" -> "Mini oyun"
                "achievement" -> "Rozet kazanıldı"
                "daily_quiz" -> "Günlük quiz"
                else -> action
            }
            Text(
                label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
