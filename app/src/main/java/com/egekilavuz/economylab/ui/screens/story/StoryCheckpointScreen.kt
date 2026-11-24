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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.story.*

@Composable
fun StoryCheckpointScreen(vm: StoryViewModel) {
    val state = vm.state
    val act = state.checkpointAct.coerceAtLeast(1)
    val summary = actSummaryFor(state, act)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(18.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("🏁", fontSize = 42.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    actTitle(act),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    actSubtitle(act),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(12.dp))
                XpGainChip(25)
                Spacer(Modifier.height(6.dp))
                Text(
                    "Periyot tamamlama bonusu",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // Bu periyodun özeti
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SectionTitle("Bu periyotta")
                Spacer(Modifier.height(10.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    SummaryStat("${summary.goals}/${summary.totalGoals}", "hedef", Modifier.weight(1f))
                    SummaryStat("${summary.xp}", "XP", Modifier.weight(1f))
                    SummaryStat("${summary.badges.size}", "rozet", Modifier.weight(1f))
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        StoryStatsPanel(state)

        Spacer(Modifier.height(12.dp))

        if (summary.entries.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SectionTitle("Öne çıkan kararlar")
                    Spacer(Modifier.height(8.dp))

                    // Hizalı sütun başlıkları
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "GÜN",
                            modifier = Modifier.width(48.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "OLAY",
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "🎯",
                            modifier = Modifier.width(22.dp),
                            style = MaterialTheme.typography.labelSmall,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    )

                    summary.entries.takeLast(4).forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Gün ${entry.day}",
                                modifier = Modifier.width(48.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                entry.eventTitle,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Box(
                                modifier = Modifier.width(22.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (entry.goalDone) Text("🎯", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        if (summary.badges.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SectionTitle("Rozetlerin")
                    Spacer(Modifier.height(8.dp))
                    BadgeRow(summary.badges.map { it.id })
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { vm.continueFromCheckpoint() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = if (act >= 2) "3. Haftaya Geç" else "2. Haftaya Geç",
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        OutlinedButton(
            onClick = { vm.openJournal() },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("📓 Karar Günlüğü")
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun SummaryStat(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
