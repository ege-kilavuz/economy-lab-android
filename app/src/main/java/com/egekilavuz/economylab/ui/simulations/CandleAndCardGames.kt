package com.egekilavuz.economylab.ui.simulations

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.random.Random

// ─── CANDLE GAME (Borsa Terimleri) ───

private data class CandleRound(
    val title: String,
    val scenario: String,
    val choices: List<String>,
    val answer: String,
    val why: String,
    val tip: String
)

private val CANDLE_ROUNDS = listOf(
    CandleRound("Tek hisseye tüm para", "Arkadaşın tüm birikimini tek bir teknoloji hissesine koymayı düşünüyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Çeşitlendirme",
        "Buradaki ana ders, riski tek bir varlıkta toplamamaktır.", "Tek bir araca yoğunlaşmak riskin tek noktada toplanmasına yol açar."),
    CandleRound("Yüksek kazanç vaadi", "Bir yatırım aracı için 'çok yüksek getiri' deniyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Risk-Getiri",
        "Yüksek getiri beklentisi çoğu zaman daha yüksek risk ile birlikte gelir.", "Beklenen getiri yüksekse oynaklık da yüksek olabilir."),
    CandleRound("Şirket payları açılıyor", "Bir şirket ilk kez paylarını piyasaya sunuyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Halka Arz",
        "Bu tanım doğrudan halka arzı anlatır.", "Halka arz ilgi çekebilir ama otomatik kazanç anlamına gelmez."),
    CandleRound("Şirkete ortak olmak", "Bir yatırımcı bir şirketin payını satın alıyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Hisse Senedi",
        "Şirkete ortaklık payını temsil eden araç hisse senedidir.", "Hisse almak şirketin yapısı ve geleceğiyle ilgilidir."),
    CandleRound("Tek araç mı dağılım mı?", "Parayı tek hisseye mi yoksa birkaç farklı araca mı bölmeli?",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Çeşitlendirme",
        "Farklı araçlara bölmek riski dağıtır.", "Dağılım, aşırı kayıp riskini azaltmanın temel yoludur."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandleGame(onBack: () -> Unit) {
    val order = remember { CANDLE_ROUNDS.shuffled().take(5) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    Scaffold(topBar = {
        TopAppBar(title = { Text("📈 Borsa Oyunu", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp)) {
            if (finished) {
                Text("Skor: $score/${order.size}", style = MaterialTheme.typography.headlineSmall)
                Text(if (score >= 4) "Harika! 💪" else if (score >= 3) "İyi! 📈" else "Tekrar dene 🔄")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { index = 0; selected = -1; score = 0; finished = false }) { Text("Tekrar Oyna") }
            } else if (current != null) {
                Text("Soru ${index + 1}/${order.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Text(current.scenario, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(12.dp))
                current.choices.forEachIndexed { i, choice ->
                    val isCorrect = choice == current.answer
                    val bg = when {
                        selected >= 0 && isCorrect -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        selected == i && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                    }
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = bg),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), onClick = { if (selected < 0) { selected = i; if (isCorrect) score++ } }) {
                        Text(choice, modifier = Modifier.padding(14.dp))
                    }
                }
                if (selected >= 0) {
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                        Column(Modifier.padding(12.dp)) {
                            Text(current.why, style = MaterialTheme.typography.bodySmall)
                            Text("💡 ${current.tip}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (index + 1 >= order.size) finished = true else { index++; selected = -1 }
                    }) { Text(if (index + 1 >= order.size) "Sonuç" else "Sonraki") }
                }
            }
        }
    }
}

// ─── CARD ESCAPE (Kart Kaçışı) ───

private data class CardEvent(
    val title: String, val desc: String,
    val choices: List<Pair<String, String>> // label -> teaching
)

private val CARD_DECK = listOf(
    CardEvent("Kart ekstresi geldi", "20.000 TL borcun var. Ne yaparsın?",
        listOf("Asgari öde (1.000 TL)" to "Asgari ödeme borcu uzatır ve faiz artar.",
              "Mümkün olduğunca çok öde" to "Yüksek ödeme borcu hızlı azaltır ama nakit kalmalı.",
              "Hiç ödeme" to "Ödememek faiz ve ceza getirir.")),
    CardEvent("Acil tamir", "Buzdolabı bozuldu, 3.000 TL tamir. Nakit 2.000 TL var.",
        listOf("Karttan çek" to "Kart borca eklenir, faiz başlar.",
              "Nakit öde ve idare et" to "Nakit azalır ama kart borcu artmaz.",
              "Aileden borç al" to "Faizsiz borç iyi seçenek.")),
    CardEvent("Maaş günü", "Maaş yattı: 15.000 TL. Önceliğin ne?",
        listOf("Önce kira + fatura" to "Zorunlu harcamalar önce gelir.",
              "Yatırım yap" to "Yatırım iyi ama borç varken riskli.",
              "Eğlence" to "Öncelik hatası.")),
    CardEvent("Kampanya", "Kredi kartına 12 taksitli telefon kampanyası. İhtiyacın var mı?",
        listOf("Taksitle al" to "Taksit cazip görünür ama borcu artırır.",
              "İhtiyacım yok, geç" to "Bilinçli karar.",
              "Birikimle al" to "En sağlıklı seçenek.")),
    CardEvent("Faiz artışı", "Merkez Bankası faiz artırdı. Kart faizin de artacak.",
        listOf("Borcu hızla azalt" to "Faiz artışı borcu büyütür, erken öde.",
              "Sadece asgari öde" to "Faiz yükü artar.",
              "Yeni kredi çek" to "Kredi kartı borcuna kredi eklemek kısır döngü.")),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEscapeGame(onBack: () -> Unit) {
    val order = remember { CARD_DECK.shuffled() }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var cash by remember { mutableIntStateOf(5000) }
    var debt by remember { mutableIntStateOf(5000) }
    var finished by remember { mutableStateOf(false) }

    val current = if (index < order.size) order[index] else null

    Scaffold(topBar = {
        TopAppBar(title = { Text("💳 Kart Kaçışı", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SuggestionChip(onClick = {}, label = { Text("💰 $cash TL") })
                SuggestionChip(onClick = {}, label = { Text("💳 $debt TL", fontWeight = FontWeight.Bold) })
            }
            Spacer(Modifier.height(12.dp))

            if (finished) {
                Text("Oyun Bitti!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                Text("Nakit: $cash · Borç: $debt · Skor: $score")
                Text(if (debt <= 0) "🎉 Borçsuz bitirdin!" else if (debt < cash) "✅ İyi durumdasın" else "⚠️ Borç baskısı var")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { index = 0; selected = -1; score = 0; cash = 5000; debt = 5000; finished = false }) { Text("Tekrar Oyna") }
            } else if (current != null) {
                Text("Tur ${index + 1}/${order.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(current.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(current.desc, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(vertical = 4.dp))

                current.choices.forEachIndexed { i, (label, teach) ->
                    val isGood = i == 1 || (i == 2 && !label.contains("Hiç") && !label.contains("yeni")) // rough heuristic
                    val bg = when {
                        selected >= 0 && isGood -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        selected == i && !isGood -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                    }
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = bg),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        onClick = { if (selected < 0) { selected = i; if (isGood) score++ } }) {
                        Text(label, modifier = Modifier.padding(12.dp))
                    }
                }
                if (selected >= 0) {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                        Text(current.choices[selected].second, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = {
                        if (selected == 1) { cash -= 1000; debt -= 2000 } else if (selected == 0) { debt += 500 }
                        if (index + 1 >= order.size) finished = true else { index++; selected = -1 }
                    }) { Text(if (index + 1 >= order.size) "Sonuç" else "Sonraki") }
                }
            }
        }
    }
}
