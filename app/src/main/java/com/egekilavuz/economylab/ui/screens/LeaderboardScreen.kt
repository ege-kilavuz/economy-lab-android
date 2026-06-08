package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.LeaderboardEntry
import com.egekilavuz.economylab.data.LeaderboardManager
import com.egekilavuz.economylab.data.ProgressManager
import com.egekilavuz.economylab.data.XpManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen() {
    val context = LocalContext.current
    val leaderboardManager = remember { LeaderboardManager(context) }
    val xpManager = remember { XpManager(context) }
    val progressManager = remember { ProgressManager(context) }

    var localScores by remember { mutableStateOf(leaderboardManager.getLocalScores()) }
    var globalScores by remember { mutableStateOf<List<LeaderboardEntry>?>(null) }
    var showGlobal by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    // XP summary
    val xpSummary = remember { xpManager.getSummary() }

    LaunchedEffect(Unit) {
        loading = true
        leaderboardManager.fetchGlobalScores { scores ->
            globalScores = scores
            loading = false
        }
    }

    val displayScores = if (showGlobal && globalScores != null) globalScores!! else localScores
    // Sort by XP (descending) for local, by score for global
    val sortedScores = if (showGlobal) displayScores.sortedByDescending { it.score }
                       else displayScores.sortedByDescending { it.xp + it.score * 2 }
    val scoreType = if (showGlobal && globalScores != null) "🌍 Global" else "🏠 XP Sıralaması"

    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp).verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        // XP Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
        ) {
            Row(Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("🏆 ", fontSize = 24.sp)
                Column(Modifier.weight(1f)) {
                    Text("Seviye ${xpSummary.level} · ${xpSummary.title}", fontWeight = FontWeight.Bold)
                    Text("${xpSummary.total} XP", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
        ) {
            Column(Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("$scoreType Sıralama", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        showGlobal = !showGlobal
                    }) {
                        Text(if (showGlobal) "Yerel" else "Global", fontWeight = FontWeight.Bold)
                    }
                }

                if (loading && showGlobal) {
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                Spacer(Modifier.height(8.dp))

                if (sortedScores.isEmpty()) {
                    Text("Henüz skor yok. İlk sen ol!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 16.dp))
                } else {
                    sortedScores.take(20).forEachIndexed { idx, entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val medal = when (idx) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "#${idx + 1}" }
                            Text(medal, modifier = Modifier.width(32.dp))
                            Text("${entry.badge} ${entry.name}", modifier = Modifier.weight(1f).padding(horizontal = 4.dp),
                                fontWeight = FontWeight.SemiBold, maxLines = 1)
                            Column(horizontalAlignment = Alignment.End) {
                                val scoreText = if (showGlobal) "${entry.score} puan" else "${entry.xp} XP"
                                Text(scoreText, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(if (showGlobal) entry.difficulty else "Seviye ${entry.level} ${entry.levelTitle}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (idx < sortedScores.take(20).lastIndex) {
                            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Info card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f))
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("📊 Nasıl Skor Kazanılır?", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("• İçerik okuma: +10 XP", style = MaterialTheme.typography.bodySmall)
                Text("• Quiz tamamlama: +20 XP", style = MaterialTheme.typography.bodySmall)
                Text("• Mükemmel quiz: +30 XP", style = MaterialTheme.typography.bodySmall)
                Text("• 30 Gün Oyunu: +50 XP", style = MaterialTheme.typography.bodySmall)
                Text("• Günlük Quiz: +15 XP", style = MaterialTheme.typography.bodySmall)
                Text("• Rozet kazanma: +25 XP", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
