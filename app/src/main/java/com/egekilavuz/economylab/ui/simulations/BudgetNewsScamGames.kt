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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.toLocaleString
import kotlin.random.Random

// ─── BUDGET TETRIS (Bütçe Yönetimi) — Parayı istediğin kovaya koy, sen karar ver ───

private data class BudgetBucket(
    val id: Int,
    val title: String,
    val subtitle: String,
    val hint: String,
    val color: Long
)

private val BUDGET_BUCKETS = listOf(
    BudgetBucket(0, "İhtiyaçlar", "Kira, fatura, gıda", "Önce zorunlular — domino etkisini engeller.", 0xFF1D4ED8),
    BudgetBucket(1, "Acil Durum", "Beklenmedik masraf", "Sürpriz giderlerde karta yazmamak için yastık.", 0xFF16A34A),
    BudgetBucket(2, "Hedef", "Birikim, yatırım", "Küçük ama düzenli ilerleme daha sürdürülebilir.", 0xFFF59E0B),
    BudgetBucket(3, "İstekler", "Eğlence, keyfi", "İstekler iyi ama önce zorunlular, yoksa borç riski artar.", 0xFFDC2626),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTetrisGame(onBack: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var turn by remember { mutableIntStateOf(1) }
    var buckets by remember { mutableStateOf(listOf(0, 0, 0, 0)) }
    var currentAmount by remember { mutableIntStateOf(0) }
    var message by remember { mutableStateOf("") }
    var tip by remember { mutableStateOf("İpucu: Önce ihtiyaçları ayır, sonra acil fon ve hedeflere geç.") }
    var finished by remember { mutableStateOf(false) }
    var streak by remember { mutableIntStateOf(0) }
    var totalDropped by remember { mutableIntStateOf(0) }
    var lastBucket by remember { mutableIntStateOf(-1) }
    var feedback by remember { mutableStateOf("") }
    var feedbackColor by remember { mutableStateOf(androidx.compose.ui.graphics.Color.Unspecified) }

    // Milestone tracking
    var milestone6000 by remember { mutableStateOf(false) }
    var milestone8000 by remember { mutableStateOf(false) }
    var milestoneEmergency by remember { mutableStateOf(false) }
    var milestoneGoal by remember { mutableStateOf(false) }

    val total = buckets.sum()
    val milestoneCount = listOf(milestone6000, milestone8000, milestoneEmergency, milestoneGoal).count { it }

    fun newRound() {
        val amounts = listOf(300, 500, 700, 1000, 1200, 1500)
        currentAmount = amounts.random()
        totalDropped++
        message = "+${currentAmount.toLocaleString()} TL — Nereye koyuyorsun?"
        lastBucket = -1
        feedback = ""
    }

    fun dropTo(bucketIndex: Int) {
        val newBuckets = buckets.toMutableList()
        newBuckets[bucketIndex] = newBuckets[bucketIndex] + currentAmount
        buckets = newBuckets

        val needsTotal = newBuckets[0]

        // Skor: önce ihtiyaçlar → daha yüksek puan
        var add = 0
        var fb = ""
        when (bucketIndex) {
            0 -> {
                add = 8
                fb = "✅ İhtiyaçlar — doğru öncelik"
                streak++
            }
            1 -> {
                add = if (needsTotal >= 3000) 6 else 2
                fb = if (needsTotal >= 3000) "✅ Acil fon — ihtiyaçlar yeterli, doğru sıra" else "⚠️ Acil fon — ihtiyaçlar henüz düşük, önce onları tamamla"
                if (needsTotal >= 3000) streak++ else streak = 0
            }
            2 -> {
                add = if (needsTotal >= 4000) 5 else 1
                fb = if (needsTotal >= 4000) "✅ Hedef — ihtiyaçlar tamam, birikim iyi fikir" else "⚠️ Hedef — ihtiyaçlar daha öncelikli olabilir"
                if (needsTotal >= 4000) streak++ else streak = 0
            }
            3 -> {
                add = if (needsTotal >= 5000) 3 else -2
                fb = if (needsTotal >= 5000) "✅ İstekler — zorunlular tamam, keyfe sıra geldi" else "❌ İstekler — önce ihtiyaçları hallet!"
                if (needsTotal >= 5000) streak++ else streak = 0
            }
        }
        score += add
        feedback = fb
        feedbackColor = if (add > 0) androidx.compose.ui.graphics.Color(0xFF22C55E) else androidx.compose.ui.graphics.Color(0xFFEF4444)

        // Milestone bonusları
        if (!milestone6000 && newBuckets[0] >= 6000) { milestone6000 = true; score += 8; tip = "🎯 Bonus +8: İhtiyaçlar 6.000 TL'yi geçti!" }
        if (!milestone8000 && newBuckets[0] >= 8000) { milestone8000 = true; score += 10; tip = "🎯 Bonus +10: İhtiyaçlar 8.000 TL'ye ulaştı!" }
        if (!milestoneEmergency && newBuckets[1] >= 2000) { milestoneEmergency = true; score += 6; tip = "🎯 Bonus +6: Acil fon hedefine ulaştı!" }
        if (!milestoneGoal && newBuckets[2] >= 3000) { milestoneGoal = true; score += 5; tip = "🎯 Bonus +5: Hedef birikimine ulaştın!" }

        // Seviye atlama
        if (turn % 4 == 0) {
            tip = "⬆ Seviye atladın! Bütçe dengesini koru: önce ihtiyaç, sonra acil, sonra hedef."
        }

        lastBucket = bucketIndex
        turn++
        if (turn > 12) {
            finished = true
        } else {
            newRound()
        }
    }

    if (!finished && turn == 1) { LaunchedEffect(Unit) { newRound() } }

    Scaffold(topBar = {
        TopAppBar(title = { Text("🧩 Bütçe Tetris", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Bütçe Tetris", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        SuggestionChip(onClick = {}, label = { Text("Skor $score") },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)))
                    }
                    Text("12 tur. Gelen parayı bütçedeki kovalara yerleştir — sen karar ver!",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuggestionChip(onClick = {}, label = { Text(if (finished) "Bitti" else "Tur $turn/12") })
                        SuggestionChip(onClick = {}, label = { Text("Toplam: ${total.toLocaleString()} TL") })
                        if (streak > 1) SuggestionChip(onClick = {}, label = { Text("Seri: $streak") })
                        SuggestionChip(onClick = {}, label = { Text("🎯 $milestoneCount/4") })
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Para bloğu
            if (!finished) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🪙", fontSize = 32.sp)
                        Text(message, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                        Text("${currentAmount.toLocaleString()} TL", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(Modifier.height(8.dp))

                // Buckets
                BUDGET_BUCKETS.forEach { bucket ->
                    val isLast = lastBucket == bucket.id
                    val borderAlpha = if (isLast) 0.4f else 0.0f
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = colorFromLong(bucket.color).copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        onClick = { dropTo(bucket.id) },
                        border = if (isLast) CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(colorFromLong(bucket.color).copy(alpha = borderAlpha))
                        ) else null
                    ) {
                        Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(bucket.title, fontWeight = FontWeight.Bold)
                                Text(bucket.subtitle, style = MaterialTheme.typography.bodySmall)
                            }
                            Text("${buckets[bucket.id].toLocaleString()} TL", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Feedback
                if (feedback.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = feedbackColor.copy(alpha = 0.1f))
                    ) {
                        Text(feedback, modifier = Modifier.padding(10.dp), style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = feedbackColor)
                    }
                }

                Spacer(Modifier.height(4.dp))
                Text(tip, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // Result
            if (finished) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Oyun Bitti!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Skor: $score", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))

                        // Dağılım görselleştirmesi
                        val totalBuckets = buckets.sum().coerceAtLeast(1)
                        Column(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                            BUDGET_BUCKETS.forEachIndexed { i, bucket ->
                                val pct = (buckets[i] * 100) / totalBuckets
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                    Text(bucket.title, modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodySmall)
                                    LinearProgressIndicator(
                                        progress = { pct / 100f },
                                        modifier = Modifier.weight(1f).height(8.dp).padding(end = 8.dp),
                                        color = colorFromLong(bucket.color),
                                        trackColor = colorFromLong(bucket.color).copy(alpha = 0.15f)
                                    )
                                    Text("%$pct", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        val needsPct = (buckets[0] * 100) / totalBuckets
                        val eval = when {
                            needsPct >= 45 -> "✅ Dengeli bütçe! İhtiyaçlar öncelikli, doğru yoldasın."
                            needsPct >= 30 -> "📈 Fena değil ama ihtiyaçlara daha fazla ayırabilirsin."
                            else -> "⚠️ İhtiyaçlar düşük kalmış. Bütçede önce zorunluları ayırmak sağlıklıdır."
                        }
                        Text(eval, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(Modifier.height(12.dp))
                        Button(onClick = {
                            score = 0; turn = 1; buckets = listOf(0,0,0,0); finished = false; streak = 0
                            milestone6000 = false; milestone8000 = false; milestoneEmergency = false; milestoneGoal = false
                            tip = "İpucu: Önce ihtiyaçları ayır, sonra acil fon ve hedeflere geç."
                            totalDropped = 0; lastBucket = -1
                        }) { Text("Tekrar Oyna", fontWeight = FontWeight.Bold) }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── NEWS NOISE (Haber mi Gürültü mü) — TS ile aynı: 10 round, "Emin değilim", streak, özet ───

private data class NNItem(val title: String, val context: String, val correct: String, val why: String, val rule: String) // correct: "signal" or "noise"

private val NN_POOL = listOf(
    NNItem("TCMB faiz kararı", "TCMB politika faizini değiştirdi", "signal", "Resmî kaynak", "Merkez bankası duyurusu sinyaldir."),
    NNItem("Coin patlayacak!", "Bir Telegram kanalında 'şu an al' yazıyor", "noise", "Anonim kaynak", "Anonim kanallar gürültüdür."),
    NNItem("Enflasyon verisi", "TÜİK aylık enflasyonu açıkladı", "signal", "Resmî istatistik", "Kurumsal veri sinyaldir."),
    NNItem("Son şans %500", "Bit.ly linkiyle gelen 'hemen al' mesajı", "noise", "Kısa link + aciliyet", "Aceleci kısa linkler gürültüdür."),
    NNItem("BIST100 günlük", "Borsa İstanbul günlük bülteni", "signal", "Resmî borsa verisi", "Borsa verisi sinyaldir."),
    NNItem("WhatsApp zinciri", "'Bunu 10 kişiye gönder yoksa...'", "noise", "Zincir mesaj", "Zincir mesajlar gürültüdür."),
    NNItem("Kredi notu sorgulama", "Findeks kredi notunu gösteriyor", "signal", "Resmî sorgu", "Kredi kaydı sinyaldir."),
    NNItem("Ünlüden yatırım tavsiyesi", "Bir fenomen 'şu coin çok iyi' dedi", "noise", "Fenomen görüşü", "Fenomen tavsiyesi gürültüdür."),
    NNItem("Kira artış oranı", "TÜFE 12 aylık ortalaması belli oldu", "signal", "Resmî endeks", "TÜFE verisi sinyaldir."),
    NNItem("Bedava para! (?)", "Bilinmeyen bir site '1000 TL hediye' diyor", "noise", "Şüpheli teklif", "Gerçek olamayacak teklifler gürültüdür."),
    NNItem("Banka genel müdürlük duyurusu", "Bankanın resmî sitesinde yeni güvenlik önlemleri yayımlandı", "signal", "Kurumsal duyuru", "Resmî kurum duyurusu sinyaldir."),
    NNItem("Arkadaş tavsiyesi", "Arkadaşın 'bu hisse çıkacak, duydum' dedi", "noise", "Kulaktan dolma", "Kişisel tavsiyeler gürültüdür."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsNoiseGame(onBack: () -> Unit) {
    val order = remember { NN_POOL.shuffled().take(8) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var lastResult by remember { mutableStateOf<NNItem?>(null) }
    var lastPick by remember { mutableStateOf("") }
    var started by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    fun pick(p: String) {
        val item = current ?: return
        val ok = p == item.correct
        when (p) {
            "unsure" -> { score += 1; streak = 0 }
            else -> if (ok) { score += 4; streak++ } else { score -= 2; streak = 0 }
        }
        lastResult = item
        lastPick = p
    }

    fun nextRound() {
        if (index + 1 >= order.size) {
            finished = true
        } else {
            index++
            lastResult = null
            lastPick = ""
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("📰 Haber mi Gürültü mü?", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Haber mi Gürültü mü?", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        SuggestionChip(onClick = {}, label = { Text("Skor $score") },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)))
                    }
                    Text("${order.size} tur. Amaç: resmî veri, yöntem bilgisi ve söylenti arasındaki farkı hızlı ayırt etmek.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuggestionChip(onClick = {}, label = { Text(if (finished) "Bitti" else "Tur ${index + 1}/${order.size}") })
                        if (streak > 1) SuggestionChip(onClick = {}, label = { Text("Seri: $streak") })
                    }
                    Spacer(Modifier.height(6.dp))
                    if (!started) {
                        Button(onClick = { started = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Text("Başlat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (finished) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Bitti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Skor: $score", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Ders: En iyi savunma \"yavaşlamak\". Acele ettiren içerikler çoğunlukla manipülasyon taşır.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { index = 0; score = 0; streak = 0; started = false; finished = false; lastResult = null }) { Text("Tekrar Oyna") }
                    }
                }
            } else if (started && current != null) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(current.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(current.context, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()

                        if (lastResult == null) {
                            Spacer(Modifier.height(8.dp))
                            Text("Seç:", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { pick("signal") }, modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)) {
                                    Text("Sinyal", fontWeight = FontWeight.Bold)
                                }
                                Button(onClick = { pick("noise") }, modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                                    Text("Gürültü", fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { pick("unsure") }, modifier = Modifier.fillMaxWidth()) {
                                Text("Emin değilim")
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Not: \"Emin değilim\" bazen en doğru karardır. Özellikle resmî veri, yöntem bilgisi veya açık kaynak yoksa.",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (lastResult != null) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            val correctStr = if (current.correct == "signal") "Sinyal" else "Gürültü"
                            val pickStr = when (lastPick) { "signal" -> "Sinyal"; "noise" -> "Gürültü"; else -> "Emin değilim" }
                            Text("Doğru cevap: $correctStr · Sen: $pickStr", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text("Neden: ${current.why}", style = MaterialTheme.typography.bodySmall)
                            Text("📌 ${current.rule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { nextRound() }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (index + 1 >= order.size) "Sonuç" else "Sonraki")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── SCAM RADAR (Dolandırıcılık Radar) — TS ile aynı: 4 seçenek, streak scoring, özet ───

private data class ScamCase(
    val channel: String, val message: String, val detail: String,
    val best: String, val why: String, val rule: String
)

// choice ids: "verify", "ignore", "block", "click"
private val SCAM_POOL = listOf(
    ScamCase("SMS", "Bankacılık hesabınız bloke oldu. Hemen tıklayın: bit.ly/xyz",
        "SMS'te link var ve acil tıklamanız isteniyor.", "block", "Resmî kanaldan doğrula veya engelle.", "Bankalar SMS linkiyle işlem yapmaz."),
    ScamCase("Arama", "Ben polis. Hesabınız terör bağlantılı. Paranızı 'güvenli hesaba' aktarın.",
        "Kendini polis olarak tanıtan bir arama.", "ignore", "Yoksay / kapat.", "Polis arayıp para istemez."),
    ScamCase("E-posta", "Tebrikler! 1.000.000 TL kazandınız. Masrafları ödeyin: link",
        "Büyük ödül vaadi + masraf talebi.", "block", "Engelle / bildir.", "Büyük ödül vaadi dolandırıcılıktır."),
    ScamCase("DM", "Merhaba, ben ünlü yatırımcı X. Sana özel fırsat: 5.000 TL ile 50.000 TL kazan",
        "Sosyal medyada özel mesajla yatırım teklifi.", "ignore", "Yoksay ve engelle.", "Ünlüler DM'den yatırım teklifi göndermez."),
    ScamCase("SMS", "Kargonuz dağıtım merkezinde. Ücret ödemek için linke tıklayın: bit.ly/abc",
        "Teslimat için ek ücret isteyen SMS.", "verify", "Resmî kargo sitesinden kontrol et.", "Kargo SMS'leri resmî kanaldan doğrulanmalı."),
    ScamCase("E-posta", "İş başvurunuz kabul edildi! Ön ödeme: 2.000 TL (iade edilecek)",
        "İş teklifi için ön ödeme isteniyor.", "block", "Engelle / bildir.", "Hiçbir işveren ön ödeme istemez."),
    ScamCase("Arama", "Bu fatura hakkında son ihtar. Ödeme yapılmazsa icra gelir.",
        "Baskıcı bir fatura araması.", "ignore", "Kapat, resmî kanaldan sorgula.", "Faturalar icra tehdidiyle aranarak ödenmez."),
    ScamCase("DM", "Sana özel: 10 katına çıkacak coin! Hemen al, kaçırma.",
        "Sosyal medyada 'garantili' yatırım vaadi.", "block", "Engelle.", "Garantili yatırım vaadi dolandırıcılıktır."),
    ScamCase("SMS", "SGK ödeme iadeniz var. Hemen tıklayın: sgk.devam.link",
        "Kamu kurumu taklidi yapan şüpheli link.", "block", "Resmî SGK sitesinden kontrol et.", "SGK SMS linkiyle ödeme yapmaz."),
    ScamCase("E-posta", "Netflix aboneliğiniz durduruldu. Güncellemek için linke tıklayın.",
        "Tanınmış hizmet taklidi yapan kimlik avı.", "verify", "Resmî Netflix uygulamasından kontrol et.", "Abonelik e-postaları linkle değil, uygulama üzerinden yönetilir."),
)

private val CHOICES = listOf(
    "verify" to "Resmî kanaldan doğrula",
    "ignore" to "Yoksay / bekle",
    "block" to "Engelle / bildir",
    "click" to "Linke tıkla / devam et",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScamRadarGame(onBack: () -> Unit) {
    val order = remember { SCAM_POOL.shuffled().take(8) }
    var index by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var lastResult by remember { mutableStateOf<ScamCase?>(null) }
    var lastChoice by remember { mutableStateOf("") }
    var started by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    fun choose(choiceId: String) {
        val s = current ?: return
        val ok = choiceId == s.best
        if (ok) {
            val add = 4 + minOf(3, streak)
            score += add
            streak++
        } else {
            score -= 2
            streak = 0
        }
        lastResult = s
        lastChoice = choiceId
    }

    fun nextRound() {
        if (index + 1 >= order.size) {
            finished = true
        } else {
            index++
            lastResult = null
            lastChoice = ""
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("🛡️ Dolandırıcılık Radar", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Dolandırıcılık Radar", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        SuggestionChip(onClick = {}, label = { Text("Skor $score") },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)))
                    }
                    Text("${order.size} tur. Amaç: bankacılık ve dijital güvenlikte resmî kanal dışı tuzakları hızlı fark etmek.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuggestionChip(onClick = {}, label = { Text(if (finished) "Bitti" else "Tur ${index + 1}/${order.size}") })
                        if (streak > 1) SuggestionChip(onClick = {}, label = { Text("Seri: $streak") })
                    }
                    Spacer(Modifier.height(6.dp))
                    if (!started) {
                        Button(onClick = { started = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Text("Başlat", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (finished) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Bitti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Skor: $score", style = MaterialTheme.typography.bodyLarge)
                        Spacer(Modifier.height(8.dp))
                        Text("Ders: Hızlı karar istiyorlarsa yavaşla. Kimliği sen doğrula.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { index = 0; score = 0; streak = 0; started = false; finished = false; lastResult = null }) { Text("Tekrar Oyna") }
                    }
                }
            } else if (started && current != null) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        SuggestionChip(onClick = {}, label = { Text(current.channel) })
                        Spacer(Modifier.height(8.dp))
                        Text(current.message, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(current.detail, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        if (lastResult == null) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            Text("Ne yaparsın?", style = MaterialTheme.typography.labelMedium)
                            Spacer(Modifier.height(8.dp))
                            CHOICES.forEach { (id, label) ->
                                OutlinedButton(
                                    onClick = { choose(id) },
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) { Text(label, modifier = Modifier.fillMaxWidth()) }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text("Not: Banka, kamu kurumu veya ödeme sistemi gibi görünen mesajlarda en güvenli yol resmî uygulama ya da resmî numaradan doğrulamaktır.",
                                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        if (lastResult != null) {
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(Modifier.height(8.dp))
                            val bestLabel = CHOICES.find { it.first == current.best }?.second ?: current.best
                            Text("En güvenli: $bestLabel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                            Text("Neden: ${current.why}", style = MaterialTheme.typography.bodySmall)
                            Text("📌 ${current.rule}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { nextRound() }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (index + 1 >= order.size) "Sonuç" else "Sonraki")
                            }
                        }
                    }
                }
            }
        }
    }
}

// Helper to convert Long to Compose Color
private fun colorFromLong(color: Long): Color = Color(color.toInt())
