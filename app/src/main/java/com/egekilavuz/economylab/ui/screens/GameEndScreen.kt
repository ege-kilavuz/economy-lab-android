package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import com.egekilavuz.economylab.data.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameEndScreen(vm: GameViewModel, onBack: () -> Unit) {
    val g = vm.gameState
    val score = scoreEndOfMonth(g)
    val context = LocalContext.current
    val leaderboardManager = remember { LeaderboardManager(context) }
    val progressManager = remember { ProgressManager(context) }
    val xpManager = remember { XpManager(context) }

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
        leaderboardManager.fetchGlobalScores { scores ->
            globalScores = scores
        }
        xpManager.addXpForAction("game_finish")
        // Game score → bonus XP (score/2, max 100)
        val gameXp = (score / 2).coerceIn(0, 100)
        if (gameXp > 0) {
            xpManager.addXp(gameXp)
        }
        // Sound effects for achievements
        if (unlocked.isNotEmpty()) {
            SoundManager.getInstance(context).play(Sfx.BADGE)
            SoundManager.getInstance(context).play(Sfx.LEVEL_UP)
        } else {
            SoundManager.getInstance(context).play(if (score > 0) Sfx.CORRECT else Sfx.WRONG)
        }
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

            // Score card with entrance animation
            AnimatedVisibility(visible = true, enter = fadeIn(tween(400)) + slideInVertically(tween(400))) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth().animateContentSize(),
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
                            val xpSummary = xpManager.getSummary()
                            val entry = LeaderboardEntry(
                                name = playerName.trim(),
                                score = score,
                                difficulty = difficultyLabel(g.difficulty),
                                date = formatScoreDate(),
                                achievements = unlocked.size,
                                badge = badge,
                                xp = xpSummary.total,
                                level = xpSummary.level,
                                levelTitle = xpSummary.title
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
                            val xpSummary = xpManager.getSummary()
                            val entry = LeaderboardEntry(
                                name = playerName.trim(),
                                score = score,
                                difficulty = difficultyLabel(g.difficulty),
                                date = formatScoreDate(),
                                achievements = unlocked.size,
                                badge = badge,
                                xp = xpSummary.total,
                                level = xpSummary.level,
                                levelTitle = xpSummary.title
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
                            Text("+${unlocked.size - 8} daha...", style = MaterialTheme.typography.bodyMedium,
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
                        Text("Henüz skor yok. İlk sen ol!", style = MaterialTheme.typography.bodyMedium,
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
                                    style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text("${entry.score}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold,
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
    val g = vm.gameState
    val context = LocalContext.current

    fun eventIcon(msg: String): String = when {
        msg.contains("kira", true) -> "🏠"
        msg.contains("maaş", true) || msg.contains("gelir", true) || msg.contains("+$") -> "💰"
        msg.contains("fatura", true) || msg.contains("elektrik", true) || msg.contains("doğalgaz", true) || msg.contains("internet", true) || msg.contains("aidat", true) -> "📄"
        msg.contains("market", true) || msg.contains("dolap", true) || msg.contains("bozuldu", true) -> "🛒"
        msg.contains("kart", true) || msg.contains("borç", true) || msg.contains("ödem", true) -> "💳"
        msg.contains("yatırım", true) || msg.contains("hisse", true) || msg.contains("altın", true) || msg.contains("dolar", true) || msg.contains("btc", true) || msg.contains("alım", true) || msg.contains("satış", true) || msg.contains("portföy", true) -> "📈"
        msg.contains("misafir", true) || msg.contains("sinema", true) -> "🎬"
        msg.contains("tasarruf", true) -> "💡"
        msg.contains("gün", true) && msg.contains("başladı", true) -> "📅"
        msg.contains("gider", true) || msg.contains("beklenmedik", true) -> "⚠️"
        msg.contains("enflasyon", true) || msg.contains("faiz", true) -> "📊"
        msg.contains("skor", true) -> "🏆"
        msg.contains("ceza", true) || msg.contains("gecikti", true) || msg.contains("gecikme", true) -> "❌"
        else -> "📰"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📰 Haber Akışı", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { vm.navigate("home") }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
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
            Spacer(Modifier.height(4.dp))

            // Summary chips
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                SuggestionChip(onClick = {}, label = { Text("Gün ${g.day}/30") })
                SuggestionChip(onClick = {}, label = { Text("Nakit: ${g.cash.toLocaleString()} TL") })
                SuggestionChip(onClick = {}, label = { Text("Kart: ${g.cardDebt.toLocaleString()} TL") })
            }

            Spacer(Modifier.height(12.dp))

            // Market data card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📊 Piyasa Verileri", fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))

                    val marketItems = listOf(
                        "🥇 Altın" to g.goldPrice.toLocaleString(),
                        "📈 Borsa" to g.stockIndex.toLocaleString(),
                        "💵 Dolar" to g.usdTry.toLocaleString(),
                        "₿ BTC" to "₺${g.btcTry.toLocaleString()}",
                        "◆ ETH" to "₺${g.ethTry.toLocaleString()}",
                        "🏦 Politika Faizi" to "%${g.policyRate / 100f}"
                    )

                    // 3x2 grid
                    marketItems.chunked(3).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            row.forEach { (name, value) ->
                                Column(modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text(name, style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(value, style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                }
                            }
                            // Fill empty slots if row is not full
                            repeat(3 - row.size) {
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (g.log.isEmpty()) {
                Text("Henüz haber yok.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                g.log.reversed().forEachIndexed { idx, msg ->
                    val icon = eventIcon(msg)
                    val cardColor = when {
                        msg.contains("❌") || msg.contains("ceza") || msg.contains("gecikti") ->
                            MaterialTheme.colorScheme.error.copy(alpha = 0.06f)
                        msg.contains("💰") || msg.contains("gelir") || msg.contains("+$") ->
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.06f)
                        msg.contains("⚠️") || msg.contains("gider") ->
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.06f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(icon, fontSize = 18.sp, modifier = Modifier.padding(end = 8.dp))
                            Text(
                                text = msg,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    if (idx == 0 && g.log.size > 3) {
                        Text(
                            text = "— ${g.log.size - 1} eski olay —",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
