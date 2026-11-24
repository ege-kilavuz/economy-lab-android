package com.egekilavuz.economylab.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.story.*

@Composable
fun StoryPlayScreen(vm: StoryViewModel) {
    val state = vm.state
    val result = state.pendingResult
    val event = vm.currentEvent()

    Column(modifier = Modifier.fillMaxSize()) {
        StoryDayHeader(state) { vm.openJournal() }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            StoryStatsPanel(state)

            Spacer(Modifier.height(8.dp))
            MarketTicker(state.market)

            Spacer(Modifier.height(12.dp))

            when {
                result != null -> ResultPanel(
                    result = result,
                    isLastDay = state.day >= totalChapters(),
                    onNext = { vm.nextDay() }
                )
                event != null -> EventPanel(state = state, event = event) { choice ->
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
private fun StoryDayHeader(state: StoryState, onJournal: () -> Unit) {
    val completed = if (state.pendingResult != null) state.day else state.day - 1
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "GÜN ${state.day} / ${totalChapters()}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Nakit ${formatTL(state.stats.cash)} · Kart borcu ${formatTL(state.stats.debt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            TextButton(onClick = onJournal) {
                Text("📓 Günlük", style = MaterialTheme.typography.labelMedium)
            }
        }
        Spacer(Modifier.height(4.dp))
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
            event.scene.forEach { paragraph ->
                Text(
                    text = paragraph,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
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
    onNext: () -> Unit
) {
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
                Column {
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
            val lesson = result.lesson
            if (!lesson.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                LessonBox(lesson)
            }
            Spacer(Modifier.height(16.dp))
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
