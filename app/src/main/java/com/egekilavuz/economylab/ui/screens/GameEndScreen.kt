package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.egekilavuz.economylab.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEndScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState
    val score = scoreEndOfMonth(g)
    val context = LocalContext.current
    val leaderboardManager = remember { LeaderboardManager(context) }
    val progressManager = remember { ProgressManager(context) }

    // Calculate achievements
    val stats = AchievementStats(
        completedQuizzes = progressManager.completedQuizIds.size,
        readItems = progressManager.openedItemIds.size,
        gameCompleted = true,
        gameScore = score,
        gameDifficulty = g.difficulty,
        portfolioValue = totalAssetValue(g),
        noCardDebt = g.cardDebt == 0
    )
    val unlocked = remember(stats) { checkAchievements(stats) }

    // Leaderboard state
    var playerName by remember { mutableStateOf("") }
    var showNameInput by remember { mutableStateOf(true) }
    var localScores by remember { mutableStateOf(leaderboardManager.getLocalScores()) }
    var globalScores: List<LeaderboardEntry>? by remember { mutableStateOf<List<LeaderboardEntry>?>(null) }
    var showGlobal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        /* leaderboard okuma yakinda */
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Ay Sonu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri") }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(8.dp))

            // Score card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Skor", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text("$score", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black,
                        color = when { score >= 70 -> MaterialTheme.colorScheme.secondary; score >= 40 -> MaterialTheme.colorScheme.primary; else -> MaterialTheme.colorScheme.error })
                    Text("/ 200", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        StatChip2("Nakit: ${g.cash} TL", MaterialTheme.colorScheme.secondary)
                        StatChip2("Kart: ${g.cardDebt} TL", if (g.cardDebt > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                        StatChip2("Dolap: ${g.fridge}%", MaterialTheme.colorScheme.primary)
                    }

                    val eval = when {
                        score >= 70 -> "Dengeyi kurdun! 💪"
                        score >= 40 -> "Kıl payı, biraz daha dikkat 📈"
                        else -> "Zorlu ay, tekrar dene 🔄"
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(eval, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(12.dp))

            // Name input + save
            if (showNameInput) {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { if (it.length <= 20) playerName = it },
                    label = { Text("Adın") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (playerName.isNotBlank()) {
                            val badge = unlocked.firstOrNull()?.emoji ?: ""
                            val entry = LeaderboardEntry(
                                name = playerName.trim(),
                                score = score,
                                difficulty = difficultyLabel(g.difficulty),
                                date = formatScoreDate(),
                                achievements = unlocked.size,
                                badge = badge
                            )
                            leaderboardManager.saveLocalScore(entry)
                            localScores = leaderboardManager.getLocalScores()
                            showNameInput = false
                        }
                    })
                )
                Spacer(Modifier.height(4.dp))
                Button(
                    onClick = {
                        if (playerName.isNotBlank()) {
                            val badge = unlocked.firstOrNull()?.emoji ?: ""
                            val entry = LeaderboardEntry(
                                name = playerName.trim(),
                                score = score,
                                difficulty = difficultyLabel(g.difficulty),
                                date = formatScoreDate(),
                                achievements = unlocked.size,
                                badge = badge
                            )
                            leaderboardManager.saveLocalScore(entry)
                            leaderboardManager.submitScoreToGitHub(entry) { /* submitted */ }
                            localScores = leaderboardManager.getLocalScores()
                            showNameInput = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Skorumu Kaydet 🏆")
                }
            }

            Spacer(Modifier.height(12.dp))

            // Achievements
            if (unlocked.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🏆 Rozetlerin", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                            unlocked.take(8).forEach { a ->
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text("${a.emoji} ${a.title}", fontSize = MaterialTheme.typography.labelSmall.fontSize) }
                                )
                            }
                        }
                        if (unlocked.size > 8) {
                            Text("+${unlocked.size - 8} daha...", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Leaderboard
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏆 Liderlik Tablosu", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { showGlobal = !showGlobal }) {
                            Text(if (showGlobal) "Yerel" else "Global", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                        }
                    }

                    val displayScores = if (showGlobal && globalScores != null) globalScores!! else localScores

                    if (displayScores.isEmpty()) {
                        Text("Henüz skor yok. İlk sen ol!", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        displayScores.take(10).forEachIndexed { idx, entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val medal = when (idx) { 0 -> "🥇"; 1 -> "🥈"; 2 -> "🥉"; else -> "#${idx + 1}" }
                                Text(medal, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                                Spacer(Modifier.width(4.dp))
                                Text("${entry.badge} ${entry.name}", modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                Text("${entry.score}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary)
                            }
                            if (idx < displayScores.take(10).lastIndex) {
                                HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Game buttons
            Button(onClick = { vm.startNewGame(g.difficulty) }, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)) {
                Text("Tekrar Oyna", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(4.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)) {
                Text("Telefona dön")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatChip2(text: String, color: androidx.compose.ui.graphics.Color) {
    SuggestionChip(
        onClick = {},
        label = { Text(text, fontSize = MaterialTheme.typography.labelSmall.fontSize, fontWeight = FontWeight.Bold) },
        colors = SuggestionChipDefaults.suggestionChipColors(containerColor = color.copy(alpha = 0.12f), labelColor = color)
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameNewsPlaceholder(vm: GameViewModel) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("📰 Haberler", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { vm.navigate("home") }) { Text("Geri") }
        }
    }
}
