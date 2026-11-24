package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.*
import kotlin.math.min

data class StatsData(
    val completedQuizzes: Int,
    val totalQuizzes: Int,
    val readItems: Int,
    val totalItems: Int,
    val totalXp: Int,
    val level: Int,
    val levelTitle: String,
    val gameScores: List<Int>,
    val dailyQuizCount: Int,
    val simCompletionCount: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    content: JsonContent,
    progressManager: ProgressManager,
    xpManager: XpManager,
    onBack: () -> Unit,
    currentVersion: String = "",
    onCheckUpdates: () -> Unit = {}
) {
    val stats = remember {
        val totalItems = content.categories.sumOf { it.items.size }
        val totalQuizzes = content.categories.count { it.quiz.isNotEmpty() }
        val dailyQuizCount = content.dailyQuiz.values.sumOf { it.size }
        val xpSummary = xpManager.getSummary()
        // Simülasyonlar 9 adet sabit
        val simTotal = 9
        val simCompleted = (0 until simTotal).count { i ->
            progressManager.isSimulationCompleted("sim-$i")
        }
        StatsData(
            completedQuizzes = progressManager.completedQuizIds.size,
            totalQuizzes = totalQuizzes,
            readItems = progressManager.openedItemIds.size,
            totalItems = totalItems,
            totalXp = xpSummary.total,
            level = xpSummary.level,
            levelTitle = xpSummary.title,
            gameScores = emptyList(), // future: track last 5 game scores
            dailyQuizCount = dailyQuizCount,
            simCompletionCount = simCompleted
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 İstatistikler", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(16.dp))

            // Level Card
            StatCard(
                title = "Seviye ${stats.level}",
                subtitle = stats.levelTitle,
                value = "${stats.totalXp} XP",
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(12.dp))

            // Progress Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    label = "İçerik",
                    value = "${stats.readItems}/${stats.totalItems}",
                    pct = if (stats.totalItems > 0) stats.readItems * 100 / stats.totalItems else 0,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.secondary
                )
                StatBox(
                    label = "Quiz",
                    value = "${stats.completedQuizzes}/${stats.totalQuizzes}",
                    pct = if (stats.totalQuizzes > 0) stats.completedQuizzes * 100 / stats.totalQuizzes else 0,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.tertiary
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatBox(
                    label = "Günlük Quiz",
                    value = "${stats.totalQuizzes} tamamlandı",
                    pct = min(stats.completedQuizzes * 10, 100),
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
                StatBox(
                    label = "Simülasyon",
                    value = "${stats.simCompletionCount}/9",
                    pct = stats.simCompletionCount * 100 / 9,
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF4CAF50)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Kategori bazlı ilerleme
            Text(
                "📚 Kategori İlerlemesi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))

            content.categories.forEach { cat ->
                val progress = progressManager.getCategoryProgress(
                    cat.id,
                    cat.items.map { it.id }
                )
                CategoryProgressRow(cat.title, progress.readPct, progress.quizDone)
                Spacer(Modifier.height(6.dp))
            }

            Spacer(Modifier.height(24.dp))

            // Rozetler
            val unlocked = checkAchievements(
                AchievementStats(
                    completedQuizzes = progressManager.completedQuizIds.size,
                    readItems = progressManager.openedItemIds.size,
                    gameCompleted = progressManager.completedQuizIds.isNotEmpty(),
                    gameScore = 0,
                    gameDifficulty = Difficulty.Easy
                )
            )
            if (unlocked.isNotEmpty()) {
                Text(
                    "🏅 Kazanılan Rozetler",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                unlocked.forEach { badge ->
                    Surface(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(badge.emoji, fontSize = 28.sp)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(badge.title, fontWeight = FontWeight.Bold)
                                Text(badge.description, style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("🔄 Uygulama Güncelleme", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (currentVersion.isBlank()) "Sürüm bilgisi alınamadı"
                        else "Yüklü sürüm: v$currentVersion",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onCheckUpdates,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Güncelleme Kontrol Et")
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun StatCard(title: String, subtitle: String, value: String, color: Color) {
    AnimatedVisibility(visible = true, enter = fadeIn() + slideInVertically()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(subtitle, style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text(value, style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold, color = color)
            }
        }
    }
}

@Composable
private fun StatBox(label: String, value: String, pct: Int, modifier: Modifier, color: Color) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { pct / 100f },
                modifier = Modifier.fillMaxWidth().height(6.dp),
                color = color,
                trackColor = color.copy(alpha = 0.2f),
            )
        }
    }
}

@Composable
private fun CategoryProgressRow(title: String, readPct: Int, quizDone: Boolean) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { readPct / 100f },
                    modifier = Modifier.fillMaxWidth(0.8f).height(4.dp),
                    color = if (quizDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                if (quizDone) "✅" else "${readPct}%",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (quizDone) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// Needed for sim completion tracking
private fun ProgressManager.isSimulationCompleted(id: String): Boolean {
    // Track using opened item IDs (sim prefix)
    return openedItemIds.contains("sim_$id")
}
