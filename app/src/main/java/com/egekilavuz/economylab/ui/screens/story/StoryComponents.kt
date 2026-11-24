package com.egekilavuz.economylab.ui.screens.story

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.story.*
import java.util.Locale

// ─────────────────────────────────────────────────────────────
//  Renkler
// ─────────────────────────────────────────────────────────────

@Composable
fun cashColor(): Color = MaterialTheme.colorScheme.secondary

@Composable
fun debtColor(): Color = MaterialTheme.colorScheme.error

@Composable
fun knowledgeColor(): Color = MaterialTheme.colorScheme.primary

@Composable
fun wellbeingColor(): Color = MaterialTheme.colorScheme.tertiary

@Composable
fun riskColor(): Color = Color(0xFFFB7185)

@Composable
fun toneColor(tone: ChoiceTone): Color = when (tone) {
    ChoiceTone.Safe -> MaterialTheme.colorScheme.secondary
    ChoiceTone.Balanced -> MaterialTheme.colorScheme.primary
    ChoiceTone.Risky -> MaterialTheme.colorScheme.error
    ChoiceTone.Social -> MaterialTheme.colorScheme.tertiary
}

// ─────────────────────────────────────────────────────────────
//  Göstergeler
// ─────────────────────────────────────────────────────────────

@Composable
fun StoryGauge(
    icon: String,
    label: String,
    value: Int,
    color: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$icon $label",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "$value/100",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Spacer(Modifier.height(3.dp))
        val animated by animateFloatAsState(
            targetValue = (value.coerceIn(0, 100)) / 100f,
            animationSpec = tween(450),
            label = "storyGauge"
        )
        LinearProgressIndicator(
            progress = { animated },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = color.copy(alpha = 0.16f)
        )
    }
}

@Composable
fun StoryStatsPanel(state: StoryState) {
    val stats = state.stats
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.65f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MoneyCell("Nakit", formatNumber(stats.cash), cashColor(), Modifier.weight(1f))
                MoneyCell("Kart Borcu", formatNumber(stats.debt), debtColor(), Modifier.weight(1f))
                MoneyCell(
                    "Yatırım",
                    formatNumber(investmentValue(state)),
                    MaterialTheme.colorScheme.primary,
                    Modifier.weight(1f)
                )
            }
            if (stats.invested > 0) {
                Spacer(Modifier.height(4.dp))
                val delta = state.portfolioDelta
                val deltaText = if (delta == 0) {
                    "Portföy bugün değişmedi"
                } else {
                    "Portföy bugün ${if (delta > 0) "+" else "-"}${formatNumber(kotlin.math.abs(delta))} TL"
                }
                Text(
                    text = "$deltaText · Maliyet ${formatNumber(stats.invested)} TL",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (delta > 0) cashColor() else if (delta < 0) debtColor()
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(12.dp))
            StoryGauge("💡", "Bilgi", stats.knowledge, knowledgeColor())
            Spacer(Modifier.height(8.dp))
            StoryGauge("🌿", "Refah", stats.wellbeing, wellbeingColor())
            Spacer(Modifier.height(8.dp))
            StoryGauge("⚠️", "Risk", stats.risk, riskColor())
        }
    }
}

@Composable
fun MoneyCell(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1
        )
    }
}

@Composable
fun MarketTicker(market: MarketState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MarketPill("📈 Enflasyon", formatPct(market.inflation))
        MarketPill("💵 Dolar", String.format(Locale.US, "%.2f", market.usd))
        MarketPill("🥇 Gram altın", formatNumber(market.gold))
        MarketPill("📊 Borsa", formatNumber(market.stockIndex.toInt()))
        MarketPill("🏦 Faiz", formatPct(market.policyRate))
    }
}

@Composable
fun MarketPill(label: String, value: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(5.dp))
            Text(value, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Seçim kartı
// ─────────────────────────────────────────────────────────────

@Composable
fun ChoiceCard(
    choice: StoryChoice,
    index: Int,
    enabled: Boolean,
    lockedReason: String?,
    onClick: () -> Unit
) {
    val accent = toneColor(choice.tone)
    OutlinedCard(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (enabled)
                MaterialTheme.colorScheme.surface.copy(alpha = 0.45f)
            else
                MaterialTheme.colorScheme.surface.copy(alpha = 0.2f),
            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(64.dp)
                    .background(if (enabled) accent else accent.copy(alpha = 0.3f))
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${index + 1}.",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (enabled) accent else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = choice.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (enabled) MaterialTheme.colorScheme.onSurface
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                val sub = when {
                    !enabled -> lockedReason ?: "Kilitli"
                    choice.hint != null -> choice.hint
                    else -> null
                }
                if (sub != null) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = sub,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!enabled) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (!enabled) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Kilitli",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 12.dp).size(18.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Sonuç / değişim rozetleri
// ─────────────────────────────────────────────────────────────

@Composable
fun DeltaChips(deltas: List<StatDelta>) {
    if (deltas.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        deltas.forEach { delta ->
            val positive = delta.amount >= 0
            val color = when (delta.label) {
                "Kart borcu", "Risk" -> if (positive) debtColor() else cashColor()
                "Nakit", "Yatırım" -> if (positive) cashColor() else debtColor()
                "Bilgi" -> knowledgeColor()
                "Refah" -> wellbeingColor()
                else -> MaterialTheme.colorScheme.primary
            }
            val valueText = if (delta.money) {
                "${if (positive) "+" else "-"}${formatNumber(kotlin.math.abs(delta.amount))} TL"
            } else {
                "${if (positive) "+" else ""}${delta.amount}"
            }
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = color.copy(alpha = 0.14f)
            ) {
                Text(
                    text = "${delta.label} $valueText",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = color,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun LessonBox(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Text("💡", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    "Ekonomi notu",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold
    )
}

// ─────────────────────────────────────────────────────────────
//  Ödül / hedef / bildirim bileşenleri
// ─────────────────────────────────────────────────────────────

@Composable
fun GoalBanner(goalText: String, done: Boolean, streak: Int) {
    if (goalText.isBlank()) return
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (done) "✅" else "🎯", fontSize = 18.sp)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Günün hedefi",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(goalText, style = MaterialTheme.typography.bodySmall)
            }
            if (streak > 0) {
                Text(
                    "🔥 $streak",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
fun NoticeBanner(text: String) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text("📌", fontSize = 16.sp)
            Spacer(Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun XpGainChip(xp: Int) {
    if (xp <= 0) return
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.16f)
    ) {
        Text(
            "+$xp XP",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun BadgeRow(ids: List<String>) {
    val badges = ids.mapNotNull { badgeById(it) }
    if (badges.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        badges.forEach { badge ->
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.16f)
            ) {
                Text(
                    "${badge.emoji} ${badge.title}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
