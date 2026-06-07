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

// ─── BUDGET TETRIS (Bütçe Kovaları) ───

private val BUDGET_BUCKETS = listOf(
    Triple("İhtiyaçlar", "Kira, fatura, gıda", 0xFF1D4ED8),
    Triple("Acil Durum", "Beklenmedik masraflar", 0xFF16A34A),
    Triple("Hedef", "Birikim, yatırım", 0xFFF59E0B),
    Triple("İstekler", "Eğlence, keyfi", 0xFFDC2626),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTetrisGame(onBack: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var turn by remember { mutableIntStateOf(1) }
    var buckets by remember { mutableStateOf(listOf(0, 0, 0, 0)) }
    var currentAmount by remember { mutableIntStateOf(500) }
    var currentLabel by remember { mutableIntStateOf(0) } // 0=needs, 1=emergency, 2=goal, 3=wants
    var message by remember { mutableStateOf("Parayı doğru kovaya koy!") }
    var finished by remember { mutableStateOf(false) }

    val labels = listOf("ihtiyaç", "acil durum", "hedef", "istek")

    fun newRound() {
        currentAmount = listOf(250, 500, 750, 1000, 1250, 1500).random()
        currentLabel = Random.nextInt(4)
        val correctBucket = when (currentLabel) {
            0 -> 0 // ihtiyaç -> needs
            1 -> 1 // acil durum -> emergency
            2 -> 2 // hedef -> goal/savings
            3 -> 3 // istek -> wants
            else -> 0
        }
        message = "${currentAmount} TL — Bu bir ${
            listOf("kira/fatura", "beklenmedik masraf", "birikim hedefi", "eğlence")
        }[currentLabel]"
    }

    fun dropTo(bucketIndex: Int) {
        val correct = when (currentLabel) {
            0 -> bucketIndex == 0
            1 -> bucketIndex == 1
            2 -> bucketIndex == 2
            3 -> bucketIndex == 3
            else -> false
        }
        if (correct) { score += 10 }
        val newBuckets = buckets.toMutableList()
        newBuckets[bucketIndex] = newBuckets[bucketIndex] + currentAmount
        buckets = newBuckets
        turn++
        if (turn > 10) {
            finished = true
        } else {
            newRound()
        }
    }

    if (!finished) {
        LaunchedEffect(Unit) { newRound() }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("🧩 Bütçe Tetris", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (finished) {
                Text("Oyun Bitti! Skor: $score", style = MaterialTheme.typography.headlineSmall)
                Text("Dağılım: İhtiyaç ${buckets[0]} · Acil ${buckets[1]} · Hedef ${buckets[2]} · İstek ${buckets[3]}")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { score = 0; turn = 1; buckets = listOf(0,0,0,0); finished = false }) { Text("Tekrar Oyna") }
            } else {
                Text("Tur $turn/10 · Skor: $score", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))) {
                    Text("$currentAmount TL", modifier = Modifier.padding(20.dp), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                }
                Spacer(Modifier.height(8.dp))
                Text(message, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(16.dp))
                BUDGET_BUCKETS.forEachIndexed { i, (title, desc, color) ->
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color(color).copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), onClick = { dropTo(i) }) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(title, fontWeight = FontWeight.Bold); Text(desc, style = MaterialTheme.typography.bodySmall) }
                            Text("${buckets[i]} TL", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ─── NEWS NOISE (Haber mi Gürültü mü) ───

private data class NNItem(val title: String, val context: String, val correct: Boolean, val why: String, val rule: String)

private val NN_POOL = listOf(
    NNItem("TCMB faiz kararı", "TCMB politika faizini değiştirdi", true, "Resmî kaynak", "Merkez bankası duyurusu sinyaldir."),
    NNItem("Coin patlayacak!", "Bir Telegram kanalında 'şu an al' yazıyor", false, "Anonim kaynak", "Anonim kanallar gürültüdür."),
    NNItem("Enflasyon verisi", "TÜİK aylık enflasyonu açıkladı", true, "Resmî istatistik", "Kurumsal veri sinyaldir."),
    NNItem("Son şans %500", "Bit.ly linkiyle gelen 'hemen al' mesajı", false, "Kısa link + aciliyet", "Aceleci kısa linkler gürültüdür."),
    NNItem("BIST100 günlük", "Borsa İstanbul günlük bülteni", true, "Resmî borsa verisi", "Borsa verisi sinyaldir."),
    NNItem("WhatsApp zinciri", "'Bunu 10 kişiye gönder yoksa...'", false, "Zincir mesaj", "Zincir mesajlar gürültüdür."),
    NNItem("Kredi notu sorgulama", "Findeks kredi notunu gösteriyor", true, "Resmî sorgu", "Kredi kaydı sinyaldir."),
    NNItem("Ünlüden yatırım tavsiyesi", "Bir fenomen 'şu coin çok iyi' dedi", false, "Fenomen görüşü", "Fenomen tavsiyesi gürültüdür."),
    NNItem("Kira artış oranı", "TÜFE 12 aylık ortalaması belli oldu", true, "Resmî endeks", "TÜFE verisi sinyaldir."),
    NNItem("Bedava para! (?)", "Bilinmeyen bir site '1000 TL hediye' diyor", false, "Şüpheli teklif", "Gerçek olamayacak teklifler gürültüdür."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsNoiseGame(onBack: () -> Unit) {
    val order = remember { NN_POOL.shuffled().take(8) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var answered by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    Scaffold(topBar = {
        TopAppBar(title = { Text("📰 Haber mi Gürültü mü?", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp)) {
            if (finished) {
                Text("Skor: $score/${order.size}", style = MaterialTheme.typography.headlineSmall)
                Text(if (score >= 7) "👏 Finansal okuryazarlığın yüksek!" else "📈 Tekrar dene")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { index = 0; score = 0; answered = false; finished = false }) { Text("Tekrar Oyna") }
            } else if (current != null) {
                Text("${index + 1}/${order.size} · Skor: $score", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(current.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(current.context, style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(16.dp))
                Button(onClick = { if (!answered) { score++; answered = true } }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) { Text("📰 Sinyal (Güvenilir)", fontWeight = FontWeight.Bold) }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { if (!answered) { if (!current.correct) score++; answered = true } }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("📢 Gürültü (Şüpheli)", fontWeight = FontWeight.Bold) }
                if (answered) {
                    Spacer(Modifier.height(12.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                        Column(Modifier.padding(12.dp)) {
                            Text(current.why, fontWeight = FontWeight.Bold)
                            Text("📌 ${current.rule}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (index + 1 >= order.size) finished = true else { index++; answered = false } }) { Text("Sonraki") }
                }
            }
        }
    }
}

// ─── SCAM RADAR (Dolandırıcılık Radar) ───

private data class ScamCase(val channel: String, val message: String, val detail: String, val best: Int, val why: String, val rule: String)

private val SCAM_POOL = listOf(
    ScamCase("SMS", "Bankacılık hesabınız bloke oldu. Hemen tıklayın: bit.ly/xyz",
        "SMS'te link var ve acil tıklamanız isteniyor.", 2, "Resmî kanaldan doğrula veya engelle.", "Bankalar SMS linkiyle işlem yapmaz."),
    ScamCase("Arama", "Ben polis. Hesabınız terör bağlantılı. Paranızı 'güvenli hesaba' aktarın.",
        "Kendini polis olarak tanıtan bir arama.", 1, "Yoksay / kapat.", "Polis arayıp para istemez."),
    ScamCase("E-posta", "Tebrikler! 1.000.000 TL kazandınız. Masrafları ödeyin: link",
        "Büyük ödül vaadi + masraf talebi.", 2, "Engelle / bildir.", "Büyük ödül vaadi dolandırıcılıktır."),
    ScamCase("DM", "Merhaba, ben ünlü yatırımcı X. Sana özel fırsat: 5.000 TL ile 50.000 TL kazan",
        "Sosyal medyada özel mesajla yatırım teklifi.", 1, "Yoksay ve engelle.", "Ünlüler DM'den yatırım teklifi göndermez."),
    ScamCase("SMS", "Kargonuz dağıtım merkezinde. Ücret ödemek için linke tıklayın: bit.ly/abc",
        "Teslimat için ek ücret isteyen SMS.", 0, "Resmî kargo sitesinden kontrol et.", "Kargo SMS'leri resmî kanaldan doğrulanmalı."),
    ScamCase("E-posta", "İş başvurunuz kabul edildi! Ön ödeme: 2.000 TL (iade edilecek)",
        "İş teklifi için ön ödeme isteniyor.", 2, "Engelle / bildir.", "Hiçbir işveren ön ödeme istemez."),
    ScamCase("Arama", "Bu fatura hakkında son ihtar. Ödeme yapılmazsa icra gelir.",
        "Baskıcı bir fatura araması.", 1, "Kapat, resmî kanaldan sorgula.", "Faturalar icra tehdidiyle aranarak ödenmez."),
    ScamCase("DM", "Sana özel: 10 katına çıkacak coin! Hemen al, kaçırma.",
        "Sosyal medyada 'garantili' yatırım vaadi.", 2, "Engelle.", "Garantili yatırım vaadi dolandırıcılıktır."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamRadarGame(onBack: () -> Unit) {
    val order = remember { SCAM_POOL.shuffled().take(8) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    val choices = listOf("Doğrula", "Yoksay", "Engelle")

    Scaffold(topBar = {
        TopAppBar(title = { Text("🛡️ Dolandırıcılık Radar", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp)) {
            if (finished) {
                Text("Skor: $score/${order.size}", style = MaterialTheme.typography.headlineSmall)
                Text(if (score >= 7) "🛡️ Seni dolandırmak zor!" else "📈 Daha dikkatli ol")
                Spacer(Modifier.height(12.dp))
                Button(onClick = { index = 0; score = 0; selected = -1; finished = false }) { Text("Tekrar Oyna") }
            } else if (current != null) {
                Text("Vaka ${index + 1}/${order.size}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text("📧 ${current.channel}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                        Text(current.message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(vertical = 4.dp))
                        Text(current.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(16.dp))
                choices.forEachIndexed { i, label ->
                    val isCorrect = i == current.best
                    val bg = when {
                        selected >= 0 && isCorrect -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        selected == i && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                        else -> MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
                    }
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = bg),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        onClick = { if (selected < 0) { selected = i; if (isCorrect) score++ } }) {
                        Text(label, modifier = Modifier.padding(14.dp), fontWeight = FontWeight.Bold)
                    }
                }
                if (selected >= 0) {
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                        Column(Modifier.padding(12.dp)) {
                            Text(current.why, fontWeight = FontWeight.Bold)
                            Text("📌 ${current.rule}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { if (index + 1 >= order.size) finished = true else { index++; selected = -1 } }) { Text("Sonraki") }
                }
            }
        }
    }
}

// Helper to avoid import clash
private fun Color(color: Long): androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(color)
