package com.egekilavuz.economylab.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.egekilavuz.economylab.data.*

@Composable
fun GameHomeScreen(
    vm: GameViewModel,
    onNavigate: (String) -> Unit
) {
    val g = vm.gameState
    val showResume by remember { mutableStateOf(vm.hasSaveGame() && g.day > 1 && g.day <= 30) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Header info
        Text(
            text = "Mod: ${difficultyLabel(g.difficulty)} · Faiz: ${g.policyRate / 100f}% · Puan: ${g.points}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(4.dp))

        // Resume or New Game
        if (showResume) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("💾 Kayıtlı oyun var", fontWeight = FontWeight.Bold)
                        Text("Gün ${g.day}/30", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilledTonalButton(onClick = {
                            vm.deleteSave()
                            vm.startNewGame(g.difficulty)
                        }) { Text("Yeni") }
                        Button(onClick = { onNavigate("game/play") }) { Text("Devam") }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
        }
        Spacer(Modifier.height(8.dp))

        // Stat chips
        StatChips(g)

        Spacer(Modifier.height(12.dp))

        // Daily quest card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Günlük Görev", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(g.quest.title, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                Text("İpucu: ${g.quest.hint}", style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (g.quest.done) {
                    Text("✅ +10", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary)
                }
                if (g.mood < 45) {
                    Spacer(Modifier.height(4.dp))
                    Text("Moral düşük: olumsuz olay ihtimali artar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error)
                }

                Spacer(Modifier.height(8.dp))
                Divider()

                // Nakit/Kart switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Ödeme: ", style = MaterialTheme.typography.bodySmall)
                    SuggestionChip(
                        onClick = { vm.toggleUseCard(false) },
                        label = { Text("Nakit", fontSize = 11.sp, fontWeight = if (!vm.useCard) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Spacer(Modifier.width(4.dp))
                    SuggestionChip(
                        onClick = { vm.toggleUseCard(true) },
                        label = { Text("Kart", fontSize = 11.sp, fontWeight = if (vm.useCard) FontWeight.Bold else FontWeight.Normal) }
                    )
                }

                // Debt/Fridge status
                Spacer(Modifier.height(6.dp))
                StatusTips(g)
            }
        }

        Spacer(Modifier.height(12.dp))

        // App icons grid
        Text("Uygulamalar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))

        AppIconGrid(g, vm, onNavigate)

        Spacer(Modifier.height(12.dp))

        // Difficulty selector
        DifficultySelector(g, vm)

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun StatChips(g: GameState) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatChip("Gün ${minOf(g.day, 30)}/30", MaterialTheme.colorScheme.primary)
            StatChip("Nakit: ${g.cash}", MaterialTheme.colorScheme.secondary)
            StatChip("Kart: ${g.cardDebt}", if (g.cardDebt > 0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            StatChip("Dolap: ${g.fridge}%", when { g.fridge < 30 -> MaterialTheme.colorScheme.error; g.fridge < 50 -> MaterialTheme.colorScheme.tertiary; else -> MaterialTheme.colorScheme.secondary })
            StatChip("Moral: ${g.mood}%", when { g.mood < 45 -> MaterialTheme.colorScheme.error; else -> MaterialTheme.colorScheme.secondary })
            StatChip("Enerji: ${g.energy}%", MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun StatChip(text: String, color: androidx.compose.ui.graphics.Color) {
    SuggestionChip(
        onClick = {},
        label = { Text(text, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        colors = SuggestionChipDefaults.suggestionChipColors(
            containerColor = color.copy(alpha = 0.15f),
            labelColor = color
        )
    )
}

@Composable
fun StatusTips(g: GameState) {
    Column {
        val cardTip = when {
            g.cardDebt >= 10000 -> "Kart borcu ciddi baskı oluşturuyor"
            g.cardDebt > 0 -> "Kart borcun var; ay sonu faiz baskısını azaltmak için ödeme yap"
            else -> "Kart borcun kontrol altında"
        }
        val fridgeTip = when {
            g.fridge <= 30 -> "Dolap seviyesi düşük. Market yapmalısın!"
            g.fridge <= 55 -> "Dolap orta seviyede; plansız birkaç gün daha geçerse zorlayabilir"
            else -> "Dolap tarafı rahat"
        }
        Text(cardTip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(fridgeTip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AppIconGrid(g: GameState, vm: GameViewModel, onNavigate: (String) -> Unit) {
    val apps = listOf(
        Triple("Banka", "bank", "🏦"),
        Triple("Market", "market", "🛒"),
        Triple("Yatırım", "invest", "📈"),
        Triple("Haber", "news", "📰"),
        Triple("Kart", "card-escape", "💳"),
        Triple("Bütçe", "budget-tetris", "🧩"),
        Triple("Gürültü", "news-noise", "📢"),
        Triple("Radar", "scam-radar", "🛡️"),
        Triple("Borsa", "candle", "📊"),
        Triple("Uyku", "sleep", "🌙"),
    )

    // 4 columns, 3 rows (touch hedefleri daha geniş)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        apps.chunked(4).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                row.forEach { (label, screenId, emoji) ->
                    AppIconButton(
                        emoji = emoji,
                        label = label,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (screenId == "sleep") vm.advanceDay()
                            else onNavigate(screenId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppIconButton(emoji: String, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.padding(2.dp).aspectRatio(1f).animateContentSize(animationSpec = tween(150)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(emoji, fontSize = 28.sp, modifier = Modifier.semantics { contentDescription = label })
            Text(label, fontSize = 11.sp, maxLines = 1, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun DifficultySelector(g: GameState, vm: GameViewModel) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Zorluk", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.entries.forEach { d ->
                    FilterChip(
                        selected = g.difficulty == d,
                        onClick = { vm.setDifficulty(d) },
                        label = { Text(difficultyLabel(d)) }
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(onClick = { vm.startNewGame(g.difficulty) }, modifier = Modifier.fillMaxWidth()) {
                Text("Sıfırla")
            }
        }
    }
}
