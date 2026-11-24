package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.LeaderboardEntry
import com.egekilavuz.economylab.data.LeaderboardManager
import com.egekilavuz.economylab.data.ProgressManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen() {
    val context = LocalContext.current
    val leaderboardManager = remember { LeaderboardManager(context) }
    val progressManager = remember { ProgressManager(context) }

    var localScores by remember { mutableStateOf(leaderboardManager.getLocalScores()) }
    var globalScores by remember { mutableStateOf<List<LeaderboardEntry>?>(null) }
    var showGlobal by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        localScores = leaderboardManager.getLocalScores()
        loading = true
        leaderboardManager.fetchGlobalScores { scores ->
            globalScores = scores
            loading = false
        }
    }

    val displayScores = if (showGlobal && globalScores != null) globalScores!! else localScores
    val sortedScores = if (showGlobal) {
        displayScores.sortedByDescending { it.score }
    } else {
        displayScores.sortedByDescending { it.xp + it.score * 2 }
    }

    val playerName = progressManager.userName.trim()
    val playerRank = if (playerName.isNotBlank()) {
        sortedScores.indexOfFirst { it.name == playerName }
    } else -1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "🏆 Liderlik",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (showGlobal) "Global oyuncular · skora göre"
                            else "Yerel oyuncular · XP + skora göre",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (loading && showGlobal) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = !showGlobal,
                        onClick = { showGlobal = false },
                        label = { Text("Yerel") }
                    )
                    FilterChip(
                        selected = showGlobal,
                        onClick = { showGlobal = true },
                        label = { Text("Global") }
                    )
                }

                if (playerRank >= 0 && sortedScores.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                    ) {
                        Text(
                            "Sıran: #${playerRank + 1} / ${sortedScores.size}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Sütun başlıkları
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "#",
                        modifier = Modifier.width(40.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        "Oyuncu",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        if (showGlobal) "Puan" else "XP",
                        modifier = Modifier.width(96.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.End
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp),
                    thickness = 0.5.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )

                if (sortedScores.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏅", fontSize = 34.sp)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Henüz skor yok",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            "Oyunu bitirip skorunu kaydet, ilk sen ol!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    sortedScores.take(20).forEachIndexed { index, entry ->
                        LeaderRow(
                            rank = index,
                            entry = entry,
                            isGlobal = showGlobal,
                            isPlayer = entry.name == playerName
                        )
                        if (index < sortedScores.take(20).lastIndex) {
                            HorizontalDivider(
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.18f)
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            )
        ) {
            Column(Modifier.padding(14.dp)) {
                Text("📊 Nasıl XP kazanılır?", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                XpInfoLine("İçerik okuma", "+10 XP")
                XpInfoLine("Quiz tamamlama", "+20 XP")
                XpInfoLine("Mükemmel quiz", "+30 XP")
                XpInfoLine("Günlük quiz", "+15 XP")
                XpInfoLine("Mini oyun", "+5 XP")
                XpInfoLine("Oyun bitirme", "+50 XP")
                XpInfoLine("Oyun içi bölüm / hedef / rozet", "+5 / +10 / +20 XP")
                XpInfoLine("Başarım rozeti", "+25 XP")
                Spacer(Modifier.height(6.dp))
                Text(
                    "İpucu: Üstteki seviye barına dokunarak seviye tablosunu ve XP ilerlemeni görebilirsin.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LeaderRow(
    rank: Int,
    entry: LeaderboardEntry,
    isGlobal: Boolean,
    isPlayer: Boolean
) {
    val medal = when (rank) {
        0 -> "🥇"
        1 -> "🥈"
        2 -> "🥉"
        else -> null
    }
    val name = listOf(entry.badge, entry.name)
        .filter { it.isNotBlank() }
        .joinToString(" ")
    val subLine = if (isGlobal) {
        entry.difficulty.ifBlank { "Global oyuncu" }
    } else {
        "Seviye ${entry.level} · ${entry.levelTitle}"
    }
    val primary = if (isGlobal) "${entry.score} puan" else "${entry.xp} XP"
    val secondary = if (isGlobal) {
        if (entry.xp > 0) "${entry.xp} XP" else ""
    } else {
        "${entry.score} puan"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isPlayer) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                else Color.Transparent
            )
            .padding(horizontal = 4.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.width(40.dp), contentAlignment = Alignment.Center) {
            if (medal != null) {
                Text(medal, fontSize = 18.sp)
            } else {
                Text(
                    "#${rank + 1}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Column(modifier = Modifier.weight(1f).padding(horizontal = 4.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isPlayer) FontWeight.Bold else FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subLine,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(
            modifier = Modifier.width(96.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
            if (secondary.isNotBlank()) {
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun XpInfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "• $label",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}
