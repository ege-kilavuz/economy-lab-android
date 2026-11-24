package com.egekilavuz.economylab.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.Sfx
import com.egekilavuz.economylab.data.SoundManager
import com.egekilavuz.economylab.data.story.*
import kotlinx.coroutines.delay

@Composable
fun StoryPlayScreen(vm: StoryViewModel) {
    val state = vm.state
    val result = state.pendingResult
    val event = vm.currentEvent()
    val quick = vm.quickMode
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current

    Column(modifier = Modifier.fillMaxSize()) {
        StoryDayHeader(
            state = state,
            quick = quick,
            onToggleQuick = { vm.toggleQuickMode() },
            onJournal = { vm.openJournal() }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            state.notice?.let { notice ->
                NoticeBanner(notice)
                Spacer(Modifier.height(8.dp))
            }

            StoryStatsPanel(state)

            Spacer(Modifier.height(8.dp))
            MarketTicker(state.market)

            Spacer(Modifier.height(8.dp))
            GoalBanner(state.goalText, state.goalDone, state.goalStreak)

            Spacer(Modifier.height(12.dp))

            when {
                result != null -> ResultPanel(
                    result = result,
                    isLastDay = state.day >= totalChapters(),
                    quick = quick,
                    newBadges = state.newBadges,
                    goalDone = state.goalDone,
                    streak = state.goalStreak,
                    onNext = { vm.nextDay() }
                )
                event != null -> EventPanel(state = state, event = event, quick = quick) { choice ->
                    SoundManager.getInstance(context).play(Sfx.CLICK)
                    haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    vm.choose(choice)
                }
                else -> {
                    Text(
                        "Bu gün için bir olay bulunamadı.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { vm.nextDay() }) { Text("İlerle") }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StoryDayHeader(
    state: StoryState,
    quick: Boolean,
    onToggleQuick: () -> Unit,
    onJournal: () -> Unit
) {
    val completed = if (state.pendingResult != null) state.day else state.day - 1
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GÜN ${state.day} / ${totalChapters()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nakit ${formatTL(state.stats.cash)} · Kart ${formatTL(state.stats.debt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onToggleQuick) {
                Text(
                    text = if (quick) "⚡ Hızlı" else "⚡ Normal",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (quick) FontWeight.Bold else FontWeight.Normal,
                    color = if (quick) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onJournal) {
                Text("📓", style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(Modifier.height(3.dp))
        LinearProgressIndicator(
            progress = { (completed.coerceIn(0, totalChapters())) / totalChapters().toFloat() },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
        )
    }
}

@Composable
private fun EventPanel(
    state: StoryState,
    event: StoryEvent,
    quick: Boolean,
    onChoose: (StoryChoice) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(event.icon, fontSize = 32.sp)
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = "BÖLÜM ${event.day}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            val paragraphs = if (quick) event.scene.take(1) else event.scene
            paragraphs.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
            }
            if (quick && event.scene.size > 1) {
                Text(
                    "⚡ Hızlı mod: metnin devamı gizli.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    Spacer(Modifier.height(14.dp))
    SectionTitle("Ne yapacaksın?")
    Spacer(Modifier.height(4.dp))
    Text(
        "Her seçim farklı bir sonuç doğurur. Bazı seçenekler bilgi veya nakit gerektirir.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Spacer(Modifier.height(8.dp))

    event.choices.forEachIndexed { index, choice ->
        val enabled = canAffordChoice(state, choice)
        ChoiceCard(
            choice = choice,
            index = index,
            enabled = enabled,
            lockedReason = if (enabled) null else lockReasonFor(state, choice),
            onClick = { if (enabled) onChoose(choice) }
        )
        Spacer(Modifier.height(7.dp))
    }
}

@Composable
private fun ResultPanel(
    result: StoryLogEntry,
    isLastDay: Boolean,
    quick: Boolean,
    newBadges: List<String>,
    goalDone: Boolean,
    streak: Int,
    onNext: () -> Unit
) {
    var autoAdvance by remember(result) { mutableStateOf(quick) }
    var showLesson by remember(result) { mutableStateOf(!quick) }
    val context = LocalContext.current

    LaunchedEffect(result) {
        val sounds = SoundManager.getInstance(context)
        when {
            newBadges.isNotEmpty() -> sounds.play(Sfx.BADGE)
            goalDone -> sounds.play(Sfx.CORRECT)
        }
    }

    LaunchedEffect(result, autoAdvance) {
        if (autoAdvance) {
            delay(if (newBadges.isNotEmpty()) 4_500 else 3_000)
            onNext()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("✅", fontSize = 26.sp)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bölüm ${result.day} · ${result.eventTitle}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Kararın uygulandı",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                XpGainChip(result.xp)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = result.choiceLabel,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = result.result,
                style = MaterialTheme.typography.bodyMedium
            )
            if (result.deltas.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                DeltaChips(result.deltas)
            }

            Spacer(Modifier.height(10.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (goalDone)
                    MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Text(
                    text = if (goalDone) "🎯 Günün hedefi tuttu! +10 XP" else "🎯 Günün hedefi bu sefer olmadı",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (goalDone) MaterialTheme.colorScheme.secondary
                    else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
            if (streak >= 3) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "🔥 $streak hedef üst üste!",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
            if (newBadges.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "🏅 Yeni rozet!",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(Modifier.height(4.dp))
                BadgeRow(newBadges)
            }

            val lesson = result.lesson
            if (!lesson.isNullOrBlank()) {
                Spacer(Modifier.height(10.dp))
                if (showLesson) {
                    LessonBox(lesson)
                } else {
                    TextButton(onClick = { showLesson = true }) {
                        Text("💡 Ekonomi notunu göster", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }

            if (quick && autoAdvance) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "⏳ Otomatik geçiş yapılıyor...",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { autoAdvance = false }) {
                        Text("Duraklat", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = if (isLastDay) "Ayı Bitir ve Sonucu Gör" else "Gün ${result.day + 1}'e Geç",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
