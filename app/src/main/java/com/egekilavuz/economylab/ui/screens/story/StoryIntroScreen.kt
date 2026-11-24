package com.egekilavuz.economylab.ui.screens.story

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.Difficulty
import com.egekilavuz.economylab.data.difficultyLabel
import com.egekilavuz.economylab.data.story.StoryViewModel

@Composable
fun StoryIntroScreen(vm: StoryViewModel) {
    var difficulty by remember { mutableStateOf(vm.state.difficulty) }
    val hasOngoing = vm.hasSave && !vm.state.finished
    val hasFinished = vm.hasSave && vm.state.finished

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
    ) {
        Spacer(Modifier.height(16.dp))

        // ── Kapak ──
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
                Text("🧪", fontSize = 44.sp)
                Spacer(Modifier.height(6.dp))
                Text(
                    "30 Gün: İlk Maaş",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ekonomi Laboratuvarı · Hikâye Modu",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "Ülke seçimi yok, şehir seçimi yok. Türkiye'de yeni mezun bir gençsin ve elinde bir maaş, bir ay var.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Her gün bir olay, her olayda bir seçim. Verdiğin kararlar ayın sonundaki tabloyu belirleyecek.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Nasıl işler ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SectionTitle("Nasıl işler?")
                Spacer(Modifier.height(8.dp))
                IntroBullet("📅", "30 bölüm", "Her gün tek bir olayla karşılaşırsın.")
                IntroBullet("🧭", "Seçim mekaniği", "2-4 seçenekten birini seçersin; her seçim farklı sonuç doğurur.")
                IntroBullet("📊", "6 gösterge", "Nakit, kart borcu, yatırım, bilgi, refah ve risk.")
                IntroBullet("🔒", "Bilgi kilitleri", "Bazı seçenekler yeterli finans bilgisi veya nakitle açılır.")
                IntroBullet("🏁", "Ay sonu", "Puan, arketip ve finansal bilanço ile ayı kapatırsın.")
            }
        }

        Spacer(Modifier.height(14.dp))

        // ── Zorluk ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
            )
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                SectionTitle("Başlangıç koşulları")
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Difficulty.entries.forEach { d ->
                        FilterChip(
                            selected = difficulty == d,
                            onClick = { difficulty = d },
                            label = { Text(difficultyLabel(d)) }
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = when (difficulty) {
                        Difficulty.Easy -> "Borçsuz başlarsın, daha yüksek nakit ve bilgi. Öğrenmek için ideal."
                        Difficulty.Normal -> "Öğrencilikten kalan küçük bir kart borcuyla başlarsın. Dengeli deneyim."
                        Difficulty.Hard -> "Daha az nakit, daha yüksek borç ve daha düşük bilgi. Zorlu bir ay."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        if (hasOngoing) {
            Button(
                onClick = { vm.continueGame() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Devam Et · Gün ${vm.state.day}/30", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = { vm.startNewGame(difficulty) },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Yeni Ay Başlat")
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Yeni ay başlatırsan mevcut kayıt silinir.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Button(
                onClick = { vm.startNewGame(difficulty) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Ayı Başlat", fontWeight = FontWeight.Bold)
            }
            if (hasFinished) {
                Spacer(Modifier.height(8.dp))
                OutlinedButton(
                    onClick = { vm.showEndScreen() },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Son Ayın Özetini Gör")
                }
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            "İpucu: Öğren bölümündeki konular bu oyunda karşına çıkar. Bütçe, enflasyon, kredi, yatırım ve dolandırıcılık...",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun IntroBullet(emoji: String, title: String, text: String) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(emoji, fontSize = 16.sp)
        Spacer(Modifier.width(8.dp))
        Column {
            Text(title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
