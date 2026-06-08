package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.XpManager
import com.egekilavuz.economylab.data.XpSummary

@Composable
fun XpBar(xpManager: XpManager) {
    val summary = remember { xpManager.getSummary() }
    var currentSummary by remember { mutableStateOf(summary) }

    // Recompute on recomposition (when XP changes)
    LaunchedEffect(Unit) {
        currentSummary = xpManager.getSummary()
    }

    val animatedProgress by animateFloatAsState(
        targetValue = currentSummary.progress,
        animationSpec = tween(600), label = "xpProgress"
    )

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Level + title
            Column {
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
        }
    }
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
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        }
    }
}
