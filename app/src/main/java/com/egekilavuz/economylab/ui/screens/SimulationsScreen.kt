package com.egekilavuz.economylab.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.egekilavuz.economylab.data.JsonContent

private val simDefs = listOf(
    Triple("📈", "Enflasyon Lab", "6dk · Başlangıç"),
    Triple("💹", "Faiz & Kredi", "5dk · Başlangıç"),
    Triple("🏛️", "MB Karar Odası", "6dk · Orta"),
    Triple("💰", "BES", "5dk · Başlangıç"),
)

private val miniDefs = listOf(
    Triple("📈", "Borsa Oyunu", "Konsept eşleştirme"),
    Triple("💳", "Kart Kaçışı", "Borç yönetimi"),
    Triple("🧩", "Bütçe Tetris", "Bütçe kovaları"),
    Triple("📰", "Haber mi?", "Sinyal/Gürültü"),
    Triple("🛡️", "Radar", "Dolandırıcılık"),
)

private val simColors = listOf(
    Color(0x2660A5FA), Color(0x2622C55E), Color(0x26FACC15), Color(0x26A855F7),
)

private val miniColors = listOf(
    Color(0x26FB7185), Color(0x26F97316), Color(0x2634D399), Color(0x26A78BFA), Color(0x26EF4444),
)

@Composable
fun SimulationsScreen(content: JsonContent, onSimulationClick: (String) -> Unit) {
    Column(Modifier.fillMaxSize().padding(horizontal = 12.dp).verticalScroll(rememberScrollState())) {
        Spacer(Modifier.height(8.dp))
        Text("🎯 Simülasyonlar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("Kısa finansal senaryolar", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
            items(simDefs) { sim ->
                val idx = simDefs.indexOf(sim)
                SimCardGrid(icon = sim.first, title = sim.second, subtitle = sim.third,
                    color = simColors[idx], onClick = { onSimulationClick("sim-$idx") })
            }
        }

        Spacer(Modifier.height(16.dp))
        Text("🎮 Mini Oyunlar", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Text("2-3 dakikalık finans oyunları", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))

        val gameIds = listOf("candle", "card-escape", "budget-tetris", "news-noise", "scam-radar")
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), userScrollEnabled = false) {
            items(miniDefs) { m ->
                val idx = miniDefs.indexOf(m)
                SimCardGrid(icon = m.first, title = m.second, subtitle = m.third,
                    color = miniColors[idx], onClick = { onSimulationClick(gameIds[idx]) })
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SimCardGrid(icon: String, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    Card(modifier = Modifier.aspectRatio(3 / 2f).fillMaxWidth().clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = color)) {
        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(icon, fontSize = 24.sp, modifier = Modifier.semantics { contentDescription = "$title - $subtitle" })
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center,
                modifier = Modifier.semantics { contentDescription = title })
            Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp), color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1, overflow = TextOverflow.Ellipsis, textAlign = TextAlign.Center)
        }
    }
}
