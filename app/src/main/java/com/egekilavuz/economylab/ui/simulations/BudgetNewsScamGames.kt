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
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.egekilavuz.economylab.data.toLocaleString
import kotlin.random.Random

// ─── BUDGET TETRIS (Sürükle-Bırak) — Yukarıdan düşen parayı kovaya sürükle ───

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private data class BtBucket(val id: Int, val title: String, val subt: String, val color: Long)

private val BT_BUCKETS = listOf(
    BtBucket(0, "İhtiyaçlar", "kira/fatura/gıda", 0xFF1D4ED8),
    BtBucket(1, "Acil Durum", "beklenmedik", 0xFF16A34A),
    BtBucket(2, "Hedef", "birikim/yatırım", 0xFFF59E0B),
    BtBucket(3, "İstekler", "eğlence/keyfi", 0xFFDC2626),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetTetrisGame(onBack: () -> Unit) {
    var score by remember { mutableIntStateOf(0) }
    var turn by remember { mutableIntStateOf(1) }
    var buckets by remember { mutableStateOf(listOf(0, 0, 0, 0)) }
    var currentAmount by remember { mutableIntStateOf(500) }
    var finished by remember { mutableStateOf(false) }
    var streak by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf("") }

    var bX by remember { mutableFloatStateOf(0.5f) }
    var bY by remember { mutableFloatStateOf(0f) }
    var dragging by remember { mutableStateOf(false) }
    var droppable by remember { mutableStateOf(true) }
    var areaSize by remember { mutableStateOf(IntSize.Zero) }

    // Milestones
    var m6000 by remember { mutableStateOf(false) }
    var m8000 by remember { mutableStateOf(false) }
    var mEm by remember { mutableStateOf(false) }
    var mGoal by remember { mutableStateOf(false) }

    val total = buckets.sum()
    val mc = listOf(m6000, m8000, mEm, mGoal).count { it }

    // Drop handler - triggered when block hits bottom
    fun handleDrop(idx: Int) {
        if (!droppable) return
        droppable = false
        val nb = buckets.toMutableList()
        nb[idx] += currentAmount
        buckets = nb
        val nt = nb[0]

        var add = 0
        var fb = ""
        when (idx) {
            0 -> { add = 8; fb = "✅ İhtiyaçlar — doğru öncelik"; streak++ }
            1 -> { add = if (nt >= 3000) 6 else 2; fb = if (nt >= 3000) "✅ Acil fon" else "⚠️ Acil fon — önce ihtiyaçları tamamla"; if (nt >= 3000) streak++ else streak = 0 }
            2 -> { add = if (nt >= 4000) 5 else 1; fb = if (nt >= 4000) "✅ Hedef" else "⚠️ Hedef — ihtiyaçlar daha öncelikli"; if (nt >= 4000) streak++ else streak = 0 }
            3 -> { add = if (nt >= 5000) 3 else -2; fb = if (nt >= 5000) "✅ İstekler" else "❌ İstekler — önce ihtiyaçları hallet!"; if (nt >= 5000) streak++ else streak = 0 }
        }
        score += add; feedback = fb
        if (!m6000 && nb[0] >= 6000) { m6000 = true; score += 8 }
        if (!m8000 && nb[0] >= 8000) { m8000 = true; score += 10 }
        if (!mEm && nb[1] >= 2000) { mEm = true; score += 6 }
        if (!mGoal && nb[2] >= 3000) { mGoal = true; score += 5 }
        turn++
        if (turn > 12) { finished = true }
        currentAmount = listOf(300, 500, 700, 1000, 1200, 1500).random()
    }

    // Fall animation
    LaunchedEffect(turn) {
        if (finished) return@LaunchedEffect
        bY = 0f; bX = 0.15f + kotlin.random.Random.nextFloat() * 0.7f; droppable = true; feedback = ""
        while (bY < 1f && droppable) {
            delay(32)
            if (!dragging) bY += 0.012f
            if (bY >= 1f && droppable) {
                handleDrop((bX * 4).toInt().coerceIn(0, 3))
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("🧩 Bütçe Tetris", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Box(Modifier.fillMaxSize().padding(innerPadding)) {
            Column(Modifier.fillMaxSize().padding(horizontal = 10.dp)) {
                Card(shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Bütçe Tetris", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                            SuggestionChip(onClick = {}, label = { Text("Skor $score") })
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            SuggestionChip(onClick = {}, label = { Text("${minOf(turn, 12)}/12") })
                            SuggestionChip(onClick = {}, label = { Text("💰${total.toLocaleString()}") })
                            if (streak > 1) SuggestionChip(onClick = {}, label = { Text("🔥$streak") })
                            SuggestionChip(onClick = {}, label = { Text("🎯$mc/4") })
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))

                // Game area
                Box(Modifier.fillMaxWidth().weight(1f).onSizeChanged { areaSize = it }) {
                    if (!finished && droppable && areaSize.width > 0) {
                        val xPx = bX * areaSize.width
                        val yPx = bY * areaSize.height
                        Box(
                            modifier = Modifier
                                .offset { IntOffset((xPx - 60).roundToInt(), yPx.roundToInt()) }
                                .zIndex(10f)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { dragging = true },
                                        onDragEnd = { dragging = false },
                                        onDragCancel = { dragging = false },
                                        onDrag = { change, amount ->
                                            change.consume()
                                            val newX = (bX * areaSize.width + amount.x) / areaSize.width
                                            bX = newX.coerceIn(0.02f, 0.98f)
                                        }
                                    )
                                }
                        ) {
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = ComposeColor(0xFF3B82F6)),
                            ) {
                                Text("₺${currentAmount.toLocaleString()}",
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 12.dp),
                                    fontWeight = FontWeight.Black, color = ComposeColor.White, fontSize = 15.sp)
                            }
                        }
                    }

                    // Bucket lanes
                    Column(Modifier.align(Alignment.BottomCenter).fillMaxWidth()) {
                        HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                        Row(Modifier.fillMaxWidth().height(100.dp)) {
                            BT_BUCKETS.forEachIndexed { i, bucket ->
                                val hover = !finished && droppable && (bX * 4).toInt().coerceIn(0, 3) == i
                                Box(Modifier.weight(1f).fillMaxHeight()
                                    .background(ComposeColor(bucket.color).copy(alpha = if (hover) 0.25f else 0.08f))
                                    .border(1.dp, ComposeColor(bucket.color).copy(alpha = if (hover) 0.5f else 0.15f)),
                                    contentAlignment = Alignment.TopCenter) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(2.dp)) {
                                        Text(bucket.title, fontWeight = FontWeight.Bold, fontSize = 11.sp, maxLines = 1)
                                        Text(bucket.subt, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                                        Text("${buckets[i].toLocaleString()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                if (feedback.isNotEmpty()) {
                    Text(feedback, modifier = Modifier.padding(4.dp), style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (feedback.contains("✅")) ComposeColor(0xFF22C55E) else ComposeColor(0xFFEF4444))
                }
            }

            if (finished) {
                Card(Modifier.align(Alignment.Center).fillMaxWidth().padding(20.dp), shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Oyun Bitti!", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text("Skor: $score")
                        val tb = buckets.sum().coerceAtLeast(1)
                        BT_BUCKETS.forEachIndexed { i, b ->
                            val pct = (buckets[i] * 100) / tb
                            Row(Modifier.fillMaxWidth().padding(vertical = 1.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(b.title, Modifier.width(65.dp), style = MaterialTheme.typography.bodySmall)
                                LinearProgressIndicator(progress = { pct / 100f }, Modifier.weight(1f).height(6.dp),
                                    color = ComposeColor(b.color), trackColor = ComposeColor(b.color).copy(alpha = 0.15f))
                                Text(" $pct%", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
                            }
                        }
                        val needsPct = (buckets[0] * 100) / tb
                        Text(when { needsPct >= 45 -> "✅ Dengeli bütçe!"; needsPct >= 30 -> "📈 Fena değil"; else -> "⚠️ İhtiyaçlar düşük" })
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = {
                            score = 0; turn = 1; buckets = listOf(0,0,0,0); finished = false; streak = 0
                            m6000 = false; m8000 = false; mEm = false; mGoal = false
                            droppable = true; bY = 0f; feedback = ""
                        }) { Text("Tekrar Oyna") }
                    }
                }
            }
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
private fun colorFromLong(color: Long): ComposeColor = ComposeColor(color.toInt())
