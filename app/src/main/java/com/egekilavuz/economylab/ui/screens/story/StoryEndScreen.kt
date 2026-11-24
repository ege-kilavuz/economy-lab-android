package com.egekilavuz.economylab.ui.screens.story

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.*
import com.egekilavuz.economylab.data.story.*
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryEndScreen(vm: StoryViewModel) {
    val state = vm.state
    val score = storyScore(state)
    val archetype = archetypeFor(state)
    val ending = endingFor(state)
    val runBadges = state.earnedBadges.mapNotNull { badgeById(it) }
    val invested = state.stats.invested
    val investedValue = investmentValue(state)
    val context = LocalContext.current

    val leaderboardManager = remember { LeaderboardManager(context) }
    val progressManager = remember { ProgressManager(context) }
    val xpManager = remember { XpManager(context) }

    val unlocked = remember(state) {
        checkAchievements(
            AchievementStats(
                completedQuizzes = progressManager.completedQuizIds.size,
                readItems = progressManager.openedItemIds.size,
                gameCompleted = true,
                gameScore = score,
                gameDifficulty = state.difficulty,
                portfolioValue = maxOf(invested, investedValue),
                noCardDebt = state.stats.debt == 0
            )
        )
    }

    var playerName by remember { mutableStateOf(progressManager.userName) }
    var showNameInput by remember { mutableStateOf(true) }
    var localScores by remember { mutableStateOf(leaderboardManager.getLocalScores()) }
    var globalScores: List<LeaderboardEntry>? by remember { mutableStateOf(null) }
    var showGlobal by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        leaderboardManager.fetchGlobalScores { scores -> globalScores = scores }
        xpManager.addXpForAction("game_finish")
        val gameXp = (score / 2).coerceIn(0, 100)
        if (gameXp > 0) xpManager.addXp(gameXp)
        val sounds = SoundManager.getInstance(context)
        if (unlocked.isNotEmpty()) {
            sounds.play(Sfx.BADGE)
            sounds.play(Sfx.LEVEL_UP)
        } else {
            sounds.play(if (score >= 50) Sfx.CORRECT else Sfx.WRONG)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("📊 Ay Sonu", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { vm.goToIntro() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                    }
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Puan ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "30 GÜN SONUNDA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$score",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = when {
                            score >= 65 -> MaterialTheme.colorScheme.secondary
                            score >= 40 -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.error
                        }
                    )
                    Text("/ 100 puan", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(10.dp))
                    Text(
                        scoreVerdict(score),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        EndChip(difficultyLabel(state.difficulty), MaterialTheme.colorScheme.primary)
                        EndChip("${state.log.size} karar", MaterialTheme.colorScheme.tertiary)
                        EndChip(archetype.emoji, MaterialTheme.colorScheme.secondary)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Ayın hikâyesi (final) ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(ending.emoji, fontSize = 40.sp)
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "AYIN HİKÂYESİ",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                ending.title,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                            Text(
                                ending.tagline,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    ending.paragraphs.forEach { paragraph ->
                        Text(paragraph, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Bu ayın özeti ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SectionTitle("Bu ayın özeti")
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        EndSummaryStat("${state.totalXp}", "XP")
                        EndSummaryStat("${state.goalHits}/${state.log.size}", "hedef")
                        EndSummaryStat("${runBadges.size}", "rozet")
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (runBadges.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.08f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        SectionTitle("Bu ay kazandığın rozetler")
                        Spacer(Modifier.height(8.dp))
                        BadgeRow(state.earnedBadges)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Arketip ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                )
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(archetype.emoji, fontSize = 40.sp)
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Senin arketipin",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            archetype.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(3.dp))
                        Text(archetype.description, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Bilanço ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Ay Sonu Bilançosu")
                    Spacer(Modifier.height(10.dp))
                    BalanceRow("💵 Nakit", formatTL(state.stats.cash), MaterialTheme.colorScheme.secondary)
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), thickness = 0.5.dp)
                    BalanceRow(
                        "📈 Yatırım",
                        if (invested > 0) "${formatTL(invested)} → ${formatTL(investedValue)}" else "Yok",
                        MaterialTheme.colorScheme.primary
                    )
                    if (invested > 0) {
                        val pct = ((investedValue - invested) * 100.0 / invested).roundToInt()
                        Text(
                            text = if (pct >= 0) "Getiri: +%$pct" else "Getiri: %$pct",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (pct >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), thickness = 0.5.dp)
                    BalanceRow("💳 Kart borcu", formatTL(state.stats.debt), MaterialTheme.colorScheme.error)
                    HorizontalDivider(Modifier.padding(vertical = 6.dp), thickness = 0.5.dp)
                    BalanceRow(
                        "🧮 Net değer",
                        formatTL(storyNetWorth(state)),
                        if (storyNetWorth(state) >= 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Göstergeler ──
            StoryStatsPanel(state)

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { vm.openJournal() },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) { Text("📓 Tüm Kararları Gör") }

            Spacer(Modifier.height(12.dp))

            // ── Rozetler ──
            if (unlocked.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("🏆 Rozetlerin", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            unlocked.take(8).forEach { achievement ->
                                SuggestionChip(
                                    onClick = {},
                                    label = {
                                        Text(
                                            "${achievement.emoji} ${achievement.title}",
                                            fontSize = MaterialTheme.typography.labelSmall.fontSize
                                        )
                                    }
                                )
                            }
                        }
                        if (unlocked.size > 8) {
                            Text(
                                "+${unlocked.size - 8} daha...",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // ── Skor kaydet ──
            if (showNameInput) {
                OutlinedTextField(
                    value = playerName,
                    onValueChange = { if (it.length <= 20) playerName = it },
                    label = { Text("Adın") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { })
                )
                Spacer(Modifier.height(6.dp))
                Button(
                    onClick = {
                        if (playerName.isNotBlank()) {
                            val entry = LeaderboardEntry(
                                name = playerName.trim(),
                                score = score,
                                difficulty = difficultyLabel(state.difficulty),
                                date = formatScoreDate(),
                                achievements = unlocked.size,
                                badge = ending.emoji,
                                xp = xpManager.getSummary().total,
                                level = xpManager.getSummary().level,
                                levelTitle = xpManager.getSummary().title
                            )
                            leaderboardManager.saveLocalScore(entry)
                            leaderboardManager.submitScoreToGitHub(entry) { }
                            progressManager.userName = playerName.trim()
                            localScores = leaderboardManager.getLocalScores()
                            showNameInput = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) { Text("Skorumu Kaydet 🏆") }
                Spacer(Modifier.height(12.dp))
            }

            // ── Liderlik ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "🏆 Liderlik Tablosu",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { showGlobal = !showGlobal }) {
                            Text(
                                if (showGlobal) "Yerel" else "Global",
                                fontSize = MaterialTheme.typography.labelSmall.fontSize
                            )
                        }
                    }
                    val display = if (showGlobal && globalScores != null) globalScores!! else localScores
                    if (display.isEmpty()) {
                        Text(
                            "Henüz skor yok. İlk sen ol!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        display.take(10).forEachIndexed { index, entry ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val medal = when (index) {
                                    0 -> "🥇"
                                    1 -> "🥈"
                                    2 -> "🥉"
                                    else -> "#${index + 1}"
                                }
                                Text(medal, fontSize = MaterialTheme.typography.bodySmall.fontSize)
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    "${entry.badge} ${entry.name}",
                                    modifier = Modifier.weight(1f),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    maxLines = 1
                                )
                                Text(
                                    "${entry.score}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (index < display.take(10).lastIndex) {
                                HorizontalDivider(
                                    thickness = 0.5.dp,
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { vm.startNewGame(state.difficulty) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Yeni Bir Ay Başlat", fontWeight = FontWeight.Bold) }
            Spacer(Modifier.height(6.dp))
            OutlinedButton(
                onClick = { vm.goToIntro() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) { Text("Ana Ekrana Dön") }

            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun BalanceRow(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun EndChip(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.14f)) {
        Text(
            text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun EndSummaryStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
