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
import androidx.compose.ui.platform.LocalContext
import com.egekilavuz.economylab.data.MiniGameManager
import com.egekilavuz.economylab.data.toLocaleString
import kotlin.math.roundToInt
import kotlin.random.Random

// ─── CANDLE GAME (Borsa Terimleri) — TS ile aynı: 10 round ───

private data class CandleRound(
    val title: String,
    val scenario: String,
    val choices: List<String>,
    val answer: String,
    val why: String,
    val tip: String
)

private val CANDLE_ROUNDS = listOf(
    CandleRound("Tek hisseye tüm para", "Arkadaşın tüm birikimini tek bir teknoloji hissesine koymayı düşünüyor. \"Çok yükselecek\" diyor ama başka aracı yok.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Çeşitlendirme",
        "Buradaki ana ders, riski tek bir varlıkta toplamamak yani çeşitlendirmedir.",
        "Resmî içerik çizgisi: tek bir araca yoğunlaşmak riskin tek noktada toplanmasına yol açar."),
    CandleRound("Yüksek kazanç vaadi", "Bir yatırım aracı için \"çok yüksek getiri bekleniyor\" deniyor. Ama fiyatlar çok oynak ve zarar ihtimali de ciddi.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Risk-Getiri",
        "Yüksek getiri beklentisi çoğu zaman daha yüksek risk ile birlikte değerlendirilir.",
        "Beklenen getiri yüksekse belirsizlik ve oynaklık da yüksek olabilir."),
    CandleRound("Şirket payları yatırımcıya açılıyor", "Bir şirket ilk kez paylarını yatırımcıların alıp satabileceği şekilde piyasaya sunuyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Halka Arz",
        "Bu tanım doğrudan halka arzı anlatır.",
        "Halka arz ilgi çekebilir ama otomatik kazanç anlamına gelmez."),
    CandleRound("Şirkete ortak olmak", "Bir yatırımcı bir şirketin payını satın alıyor ve şirketin geleceğiyle ilgili beklentilere ortak oluyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Hisse Senedi",
        "Şirkete ortaklık payını temsil eden araç hisse senedidir.",
        "Hisse almak, yalnızca grafik değil şirketin yapısı ve geleceğiyle de ilgilidir."),
    CandleRound("Tek araç mı dağılım mı?", "Aynı parayı ya tek bir hisseye ya da birkaç farklı araca bölme seçeneğin var. Amaç zararı tek noktada toplamamak.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Çeşitlendirme",
        "Soru doğrudan riski yaymaya odaklanıyor.",
        "Çeşitlendirme getiriyi garanti etmez ama tek bir darbenin etkisini azaltabilir."),
    CandleRound("Düşük risk, düşük beklenti", "Bir yatırım aracı daha sakin hareket ediyor; buna karşılık beklenen getirisi de daha sınırlı.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Risk-Getiri",
        "Burada anlatılan temel ilişki risk ve getiri dengesidir.",
        "Rasyonel yatırımcı yalnız getiriyi değil, o getirinin riskini de düşünür."),
    CandleRound("Yeni şirket payı", "Birisi \"Bu şirketin hissesi var\" diyor. Sen de bunun hangi temel kavrama karşılık geldiğini düşünüyorsun.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Hisse Senedi",
        "Şirket payı ifadesi hisse senedini anlatır.",
        "Hisse senedi, yatırımcının şirkete ortaklık payını temsil eder."),
    CandleRound("İlk satış, ilk erişim", "Bir şirketin payları ilk kez yatırımcıların erişimine açılıyor ve herkes bu süreci konuşuyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Halka Arz",
        "Payların ilk kez yatırımcıya sunulması halka arz sürecidir.",
        "İlgi yüksek olabilir; yine de sadece popüler olduğu için karar verilmez."),
    CandleRound("Sadece sosyal medya gazı", "Bir araç sürekli konuşuluyor ama kimse riskinden bahsetmiyor. Sen önce hangi kavramı düşünmelisin?",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Risk-Getiri",
        "Popülerlik tek başına yeterli değildir; risk tarafı da değerlendirilmelidir.",
        "\"Çok konuşuluyor\" demek \"iyi yatırım\" demek değildir."),
    CandleRound("Portföyü yaymak", "Bir öğrenci portföyünü hisse, fon ve farklı araçlara bölerek tek bir düşüşten daha az etkilenmek istiyor.",
        listOf("Risk-Getiri", "Çeşitlendirme", "Halka Arz", "Hisse Senedi"), "Çeşitlendirme",
        "Farklı araçlara yayılmak çeşitlendirme mantığıdır.",
        "Çeşitlendirme, tek bir riskte kilitlenmemeyi öğretir."),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandleGame(onBack: () -> Unit) {
    val context = LocalContext.current
    val mgm = remember { MiniGameManager(context) }
    val bestScore = remember { mgm.getHighScore("candle") }
    val order = remember { CANDLE_ROUNDS.shuffled().take(8) }
    var index by remember { mutableIntStateOf(0) }
    var selected by remember { mutableIntStateOf(-1) }
    var score by remember { mutableIntStateOf(0) }
    var finished by remember { mutableStateOf(false) }
    val current = if (index < order.size) order[index] else null

    // Save high score when game finishes
    LaunchedEffect(finished) {
        if (finished) {
            mgm.saveHighScore("candle", score)
        }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("📈 Borsa Oyunu", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Borsa Kavram Eşleştirme", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        SuggestionChip(onClick = {}, label = { Text("Skor $score") })
                    }
                    Text("${order.size} tur. Borsa kategorisindeki konulara en uygun kavramı seç.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SuggestionChip(onClick = {}, label = { Text(if (finished) "Bitti" else "Tur ${index + 1}/${order.size}") })
                        SuggestionChip(onClick = {}, label = { Text("Kaynak odağı: Borsa") })
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (finished) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bitti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Skorun: $score / ${order.size}", style = MaterialTheme.typography.bodyLarge)
                        if (score == bestScore) {
                            Text("🏆 Yeni Rekor!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else if (bestScore > 0) {
                            Text("Rekor: $bestScore / ${order.size}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(8.dp))
                        Text("Ders: Borsa tarafında önce kavramları doğru ayırmak gerekir: hisse senedi, halka arz, risk-getiri ve çeşitlendirme birbirinin yerine geçmez.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { index = 0; selected = -1; score = 0; finished = false }) { Text("Tekrar Oyna") }
                    }
                }
            } else if (current != null) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(current.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(current.scenario, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        current.choices.forEachIndexed { i, choice ->
                            val isCorrect = choice == current.answer
                            val isSelected = i == selected
                            val bg = when {
                                selected >= 0 && isCorrect -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                                selected >= 0 && isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            }
                            OutlinedButton(
                                onClick = { if (selected < 0) { selected = i; if (isCorrect) score++ } },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp),

                            ) { Text(choice, modifier = Modifier.fillMaxWidth()) }
                        }
                        if (selected >= 0) {
                            Spacer(Modifier.height(8.dp))
                            Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))) {
                                Column(Modifier.padding(12.dp)) {
                                    Text(if (selected == current.choices.indexOf(current.answer)) "✅ Doğru." else "❌ Yanlış. Doğru cevap: ${current.answer}.",
                                        fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                                    Text(current.why, style = MaterialTheme.typography.bodySmall)
                                    Text("💡 ${current.tip}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { if (index + 1 >= order.size) finished = true else { index++; selected = -1 } }) {
                                Text("Devam")
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─── CARD ESCAPE (Kart Kaçışı) — TS versiyonuyla aynı: state machine, 10 turn, interest ───

private data class CardState(
    val turn: Int = 1,
    val cash: Int = 6000,
    val debt: Int = 8000,
    val stress: Int = 25,
    val score: Int = 0,
    val log: List<String> = listOf("Başlangıç: 8.000 TL kart borcu, 6.000 TL nakit.")
)

private data class CardChoice(val label: String, val apply: (CardState) -> CardState, val teach: String)

private data class CardEvent(val title: String, val desc: String, val choices: List<CardChoice>)

private fun mkCardDeck(): List<CardEvent> = listOf(
    CardEvent("Kart ekstresi geldi", "Bu ay borcun var. Ne yapacaksın?", listOf(
        CardChoice("Asgari öde (kolay ama pahalı)", { s ->
            val pay = minOf(s.cash, kotlin.math.max(250, (s.debt * 0.1).roundToInt()))
            s.copy(cash = s.cash - pay, debt = s.debt - pay, score = s.score + 2, stress = (s.stress - 2).coerceIn(0, 100),
                log = listOf("Asgari ödeme: -${pay.toLocaleString()} TL") + s.log)
        }, "Asgari ödeme borcu bitirmez; borcu uzatıp toplam maliyeti büyütebilir."),
        CardChoice("Borcun yarısını öde", { s ->
            val pay = minOf(s.cash, (s.debt * 0.5).roundToInt())
            s.copy(cash = s.cash - pay, debt = s.debt - pay, score = s.score + 6, stress = (s.stress - 5).coerceIn(0, 100),
                log = listOf("Borç ödeme: -${pay.toLocaleString()} TL") + s.log)
        }, "Borcu düzenli ve daha yüksek kapatmak faiz yükünü azaltır."),
        CardChoice("Hiç ödeme (gecikme)", { s ->
            s.copy(score = s.score - 8, stress = (s.stress + 10).coerceIn(0, 100),
                log = listOf("Ödeme gecikti: ceza riski ↑") + s.log)
        }, "Gecikme çoğu zaman ceza/faiz ve stres demektir."),
    )),
    CardEvent("İndirimli alışveriş", "\"Son gün\" indirimi gördün. 2.000 TL. Kartla alabilirsin.", listOf(
        CardChoice("Al (kartla)", { s ->
            s.copy(debt = s.debt + 2000, score = s.score - 2, stress = (s.stress + 4).coerceIn(0, 100),
                log = listOf("İndirim alışverişi: +2.000 TL kart borcu") + s.log)
        }, "İndirim, ihtiyaç değilse \"ucuz\" değil \"ekstra borç\" olabilir."),
        CardChoice("Alma", { s ->
            s.copy(score = s.score + 4, stress = (s.stress - 1).coerceIn(0, 100),
                log = listOf("Alışveriş yapılmadı (kontrol)") + s.log)
        }, "En iyi tasarruf bazen \"almamak\"."),
    )),
    CardEvent("Beklenmedik gider", "Telefon tamiri: 1.500 TL. Nakit yetmezse karta yazılır.", listOf(
        CardChoice("Nakit öde", { s ->
            val c = minOf(s.cash, 1500)
            val remain = 1500 - c
            s.copy(cash = s.cash - c, debt = s.debt + remain, score = s.score + if (remain == 0) 5 else 0,
                stress = (s.stress + 2).coerceIn(0, 100),
                log = listOf("Tamir: -${c.toLocaleString()} TL nakit" + if (remain > 0) ", +${remain.toLocaleString()} TL kart" else "") + s.log)
        }, "Acil fonun varsa sürprizler kart borcuna dönüşmez."),
        CardChoice("Karta yaz", { s ->
            s.copy(debt = s.debt + 1500, score = s.score - 1, stress = (s.stress + 5).coerceIn(0, 100),
                log = listOf("Tamir: +1.500 TL kart borcu") + s.log)
        }, "Karta yazmak kısa vadede kolay, uzun vadede pahalı olabilir."),
    )),
    CardEvent("Ek gelir", "Küçük freelance iş: +1.200 TL geldi.", listOf(
        CardChoice("Borcu azalt", { s ->
            val pay = minOf(s.debt, 1200)
            s.copy(debt = s.debt - pay, cash = s.cash + (1200 - pay), score = s.score + 7,
                stress = (s.stress - 4).coerceIn(0, 100),
                log = listOf("Ek gelir borca gitti: -${pay.toLocaleString()} TL borç") + s.log)
        }, "Ek geliri borca yönlendirmek toplam faiz yükünü azaltır."),
        CardChoice("Harca (keyif)", { s ->
            s.copy(cash = s.cash + 1200, score = s.score + 1, stress = (s.stress - 6).coerceIn(0, 100),
                log = listOf("Ek gelir elde kaldı (harcama riski)") + s.log)
        }, "Keyif harcaması kötü değil; ama borç varken öncelik dikkat ister."),
    )),
    CardEvent("Arkadaş baskısı", "Dışarı çıkalım: 600 TL. Ne yaparsın?", listOf(
        CardChoice("Gitme", { s ->
            s.copy(score = s.score + 3, stress = (s.stress + 1).coerceIn(0, 100),
                log = listOf("Dışarı çıkılmadı (bütçe)") + s.log)
        }, "Bütçe \"hayır\" diyebilmeyi de öğretir."),
        CardChoice("Git (nakit)", { s ->
            if (s.cash < 600) s.copy(debt = s.debt + 600, score = s.score - 2, stress = (s.stress + 4).coerceIn(0, 100),
                log = listOf("Dışarı: +600 TL kart (nakit yetmedi)") + s.log)
            else s.copy(cash = s.cash - 600, score = s.score + 1, stress = (s.stress - 3).coerceIn(0, 100),
                log = listOf("Dışarı: -600 TL nakit") + s.log)
        }, "Keyif harcaması planlı olursa sorun olmaz; plansızsa karta yazılır."),
    )),
    CardEvent("Kira günü", "Kiran 5.000 TL. Ödemezsen ev sahibi arar.", listOf(
        CardChoice("Nakit öde", { s ->
            if (s.cash >= 5000) s.copy(cash = s.cash - 5000, score = s.score + 5, stress = (s.stress - 5).coerceIn(0, 100),
                log = listOf("Kira ödendi: -5.000 TL") + s.log)
            else s.copy(debt = s.debt + 5000, score = s.score - 5, stress = (s.stress + 10).coerceIn(0, 100),
                log = listOf("Kira karta: +5.000 TL borç") + s.log)
        }, "Kira en büyük sabit giderdir; öncelikli olmalı."),
        CardChoice("Geciktir (ertesi gün)", { s ->
            s.copy(score = s.score - 3, stress = (s.stress + 6).coerceIn(0, 100),
                log = listOf("Kira gecikti: ek ücret ve stres") + s.log)
        }, "Kira gecikmesi ek maliyet ve güven kaybı demektir."),
    )),
    CardEvent("Kredi notu sorgulama", "Kredi notunun düştüğünü öğrendin. Sebebi ne olabilir?", listOf(
        CardChoice("Asgari ödemeler", { s ->
            s.copy(score = s.score + 3, stress = (s.stress - 2).coerceIn(0, 100),
                log = listOf("Asgari ödeme kredi notunu düşürebilir") + s.log)
        }, "Düzenli asgari ödeme borcu eritmez, kredi notuna olumsuz yansıyabilir."),
        CardChoice("Limit dolu", { s ->
            s.copy(score = s.score + 2, stress = (s.stress - 1).coerceIn(0, 100),
                log = listOf("Limit kullanım oranı yüksek: kredi notu düşer") + s.log)
        }, "Kredi kartı limitinin %30'undan fazlasını kullanmak notu düşürebilir."),
    )),
    CardEvent("Ekstre: faiz yansıdı", "Borç bakiyene faiz işledi: 400 TL eklendi.", listOf(
        CardChoice("Hemen öde", { s ->
            s.copy(cash = s.cash - 400, score = s.score + 4, stress = (s.stress - 3).coerceIn(0, 100),
                log = listOf("Faiz ödendi: -400 TL") + s.log)
        }, "Faizi hemen ödemek, bileşik etkiyi kırar."),
        CardChoice("Ötele (ertesi aya)", { s ->
            s.copy(debt = s.debt + 400, score = s.score - 3, stress = (s.stress + 5).coerceIn(0, 100),
                log = listOf("Faiz ertelendi: +400 TL borç") + s.log)
        }, "Faizi ötelemek bileşik faiz döngüsüne sokar."),
    )),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardEscapeGame(onBack: () -> Unit) {
    val context = LocalContext.current
    val mgm = remember { MiniGameManager(context) }
    val bestScore = remember { mgm.getHighScore("card-escape") }
    val deck = remember { mkCardDeck() }
    var state by remember { mutableStateOf(CardState()) }
    var rngSeed by remember { mutableIntStateOf(Random.nextInt()) }
    var selectedChoice by remember { mutableIntStateOf(-1) }
    var card by remember { mutableStateOf(deck.random()) }

    val done = state.turn > 10
    val interest = (state.debt * 0.05).roundToInt()
    val net = state.cash - (state.debt + interest)

    // Save high score
    LaunchedEffect(done) {
        if (done) mgm.saveHighScore("card-escape", net)
    }

    fun newCard() {
        rngSeed++
        card = deck.random()
        selectedChoice = -1
    }

    fun reset() {
        state = CardState()
        rngSeed = Random.nextInt()
        selectedChoice = -1
        card = deck.random()
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text("💳 Kart Kaçışı", fontWeight = FontWeight.Bold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri") } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background))
    }) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).padding(12.dp).verticalScroll(rememberScrollState())) {
            // Header
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Text("Kredi Kartı Karar Oyunu", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        SuggestionChip(onClick = {}, label = { Text("Skor ${state.score}") })
                    }
                    Text("10 tur. Amaç: kredi kartı kullanımı, gerçek maliyet ve kredi notu mantığını hissederek öğrenmek.",
                        style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.fillMaxWidth()) {
                        SuggestionChip(onClick = {}, label = { Text("Tur ${minOf(state.turn, 10)}/10") })
                        SuggestionChip(onClick = {}, label = { Text("Nakit: ${state.cash.toLocaleString()} TL") })
                        SuggestionChip(onClick = {}, label = { Text("Borç: ${state.debt.toLocaleString()} TL") })
                    }
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        SuggestionChip(onClick = {}, label = { Text("Stres: ${state.stress}%") })
                        val riskLabel = when {
                            state.debt > 10000 -> "Risk: yüksek borç"
                            state.debt > 7000 -> "Risk: dikkat"
                            else -> "Risk: kontrollü"
                        }
                        val riskColor = when {
                            state.debt > 10000 -> MaterialTheme.colorScheme.error
                            state.debt > 7000 -> MaterialTheme.colorScheme.tertiary
                            else -> MaterialTheme.colorScheme.secondary
                        }
                        SuggestionChip(onClick = {}, label = { Text(riskLabel) },
                            colors = SuggestionChipDefaults.suggestionChipColors(containerColor = riskColor.copy(alpha = 0.15f), labelColor = riskColor))
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (done) {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))) {
                    Column(Modifier.padding(20.dp)) {
                        Text("Bitti", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Ay sonu faiz (temsili): ${interest.toLocaleString()} TL", style = MaterialTheme.typography.bodyMedium)
                        Text("Net durum (nakit - borç - faiz): ${net.toLocaleString()} TL", style = MaterialTheme.typography.bodyMedium)
                        if (bestScore > 0) {
                            Text("En iyi net: $bestScore TL", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        val summaryText = when {
                            net >= 2000 && state.debt < 6000 -> "Harika: borcu kontrol ettin."
                            state.debt >= 12000 -> "Zor: borç büyüdü, asgari tuzağına dikkat."
                            else -> "Fena değil."
                        }
                        Text(summaryText, fontWeight = FontWeight.Bold)
                        Text("Öğrenme notu: kredi kartında asgari ödeme gecikmeyi önleyebilir ama toplam maliyeti azaltmanın en sağlıklı yolu borcu planlı biçimde yüksek oranda kapatmaktır.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { reset() }) { Text("Tekrar Oyna") }
                    }
                }
            } else {
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f))) {
                    Column(Modifier.padding(16.dp)) {
                        Text(card.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text(card.desc, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(8.dp))
                        card.choices.forEachIndexed { i, choice ->
                            val bg = when {
                                selectedChoice >= 0 && i == selectedChoice -> if (state.score > 0 || i == 0) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                            }
                            OutlinedButton(
                                onClick = {
                                    if (selectedChoice < 0) {
                                        selectedChoice = i
                                        var newState = choice.apply(state)
                                        // 3 turda bir faiz baskısı
                                        if (newState.turn % 3 == 0 && newState.debt > 0) {
                                            val inc = (newState.debt * 0.01).roundToInt()
                                            newState = newState.copy(debt = newState.debt + inc, log = listOf("Borç faizi birikti: +${inc.toLocaleString()} TL") + newState.log)
                                        }
                                        newState = newState.copy(log = listOf("Ders: ${choice.teach}") + newState.log, turn = newState.turn + 1)
                                        state = newState
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                shape = RoundedCornerShape(12.dp),
                                
                            ) { Text(choice.label, modifier = Modifier.fillMaxWidth()) }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("Not: Bu oyun eğitim içindir. Rakamlar temsili.",
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (selectedChoice >= 0) {
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = { newCard() }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (state.turn > 10) "Sonuçları Gör" else "Sonraki Tur")
                            }
                        }
                    }
                }

                if (state.log.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Son Olaylar", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            state.log.take(6).forEach { l ->
                                Text("• $l", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}
