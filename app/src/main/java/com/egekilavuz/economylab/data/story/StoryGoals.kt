package com.egekilavuz.economylab.data.story

// ─────────────────────────────────────────────────────────────
//  Günün hedefi (mikro görev)
// ─────────────────────────────────────────────────────────────

data class StoryGoal(
    val id: String,
    val text: String,
    val rewardXp: Int = 10,
    val check: (before: StoryState, after: StoryStats, choice: StoryChoice) -> Boolean
)

val STORY_GOALS: List<StoryGoal> = listOf(
    StoryGoal("no-debt", "Bugün borç ekleme") { before, after, _ ->
        after.debt <= before.stats.debt
    },
    StoryGoal("spend-low", "Bugün 3.000 TL'den az harca") { before, after, _ ->
        before.stats.cash - after.cash <= 3_000
    },
    StoryGoal("learn", "Bugün bilgi kazan") { before, after, _ ->
        after.knowledge > before.stats.knowledge
    },
    StoryGoal("risk-low", "Bugün riski artırma") { before, after, _ ->
        after.risk <= before.stats.risk
    },
    StoryGoal("keep-wellbeing", "Refahını koru") { before, after, _ ->
        after.wellbeing >= before.stats.wellbeing
    },
    StoryGoal("keep-cash", "Nakdi koru") { before, after, _ ->
        after.cash >= before.stats.cash
    },
    StoryGoal("invest", "Bugün yatırım yap") { before, after, _ ->
        after.invested > before.stats.invested
    },
    StoryGoal("pay-debt", "Bugün borcunu azalt") { before, after, _ ->
        after.debt < before.stats.debt
    }
)

/**
 * O gün en az bir (karşılanabilir) seçenekle tutturulabilecek bir hedef seçer.
 * Böylece hedef asla "imkânsız" olmaz.
 */
fun goalForDay(state: StoryState, event: StoryEvent?): StoryGoal? {
    if (event == null) return null
    val affordable = event.choices.filter { canAffordChoice(state, it) }
    if (affordable.isEmpty()) return null

    val achievable = STORY_GOALS.filter { goal ->
        affordable.any { choice ->
            goal.check(state, previewStats(state, choice), choice)
        }
    }
    if (achievable.isEmpty()) return null
    val index = (state.seed + state.day * 37).mod(achievable.size)
    return achievable[index]
}

// ─────────────────────────────────────────────────────────────
//  Oyun içi rozetler
// ─────────────────────────────────────────────────────────────

data class RunBadge(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String,
    val condition: (StoryState) -> Boolean
)

val RUN_BADGES: List<RunBadge> = listOf(
    RunBadge("first-invest", "İlk Yatırım", "📈", "İlk yatırımını yaptın") {
        it.stats.invested > 0
    },
    RunBadge("scam-safe", "Tetikte", "🕵️", "Dolandırıcılık girişimini atlattın") {
        "scam_safe" in it.flags
    },
    RunBadge("emergency", "Acil Fon", "🧯", "Acil durum fonu oluşturdun") {
        "emergency" in it.flags
    },
    RunBadge("knowledge", "Bilgi Avcısı", "🧠", "Bilgi puanın 60'ı geçti") {
        it.stats.knowledge >= 60
    },
    RunBadge("debt-free", "Borçsuz", "🐷", "10. günden sonra borçsuz kaldın") {
        it.stats.debt == 0 && it.day >= 10
    },
    RunBadge("insured", "Sigortalı", "🛡️", "Sigorta yaptırdın") {
        "insured" in it.flags
    },
    RunBadge("streak", "Seri Karar", "🔥", "3 hedefi üst üste tutturdun") {
        it.goalStreak >= 3
    }
)

fun badgeById(id: String): RunBadge? = RUN_BADGES.firstOrNull { it.id == id }

fun evaluateBadges(state: StoryState): List<String> =
    RUN_BADGES.filter { it.condition(state) }.map { it.id }

// ─────────────────────────────────────────────────────────────
//  Rastgele ara olaylar (gün geçişinde)
// ─────────────────────────────────────────────────────────────

data class RandomEvent(
    val text: String,
    val cash: Int = 0,
    val wellbeing: Int = 0
)

val RANDOM_EVENTS: List<RandomEvent> = listOf(
    RandomEvent("Yemek kartına 750 TL yükleme yapıldı.", cash = 750, wellbeing = 1),
    RandomEvent("Telefon faturan beklediğinden 350 TL yüksek geldi.", cash = -350, wellbeing = -1),
    RandomEvent("Kullanmadığın eski eşyaları sattın: +900 TL.", cash = 900, wellbeing = 1),
    RandomEvent("Eczane masrafı çıktı: -400 TL.", cash = -400, wellbeing = -1),
    RandomEvent("İş yerinden küçük bir prim geldi: +600 TL.", cash = 600, wellbeing = 2),
    RandomEvent("Trafik cezası geldi: -800 TL.", cash = -800, wellbeing = -2),
    RandomEvent("Arkadaşın borcunu erken ödedi: +1.000 TL.", cash = 1_000, wellbeing = 1),
    RandomEvent("Evdeki musluk tamiri: -550 TL.", cash = -550, wellbeing = -1)
)

// ─────────────────────────────────────────────────────────────
//  Çoklu final (ayın hikâyesi)
// ─────────────────────────────────────────────────────────────

data class StoryEnding(
    val id: String,
    val emoji: String,
    val title: String,
    val tagline: String,
    val paragraphs: List<String>
)

fun endingFor(state: StoryState): StoryEnding {
    val stats = state.stats
    val portfolio = investmentValue(state)

    return when {
        stats.debt >= 30_000 -> StoryEnding(
            id = "debt-spiral",
            emoji = "🔗",
            title = "Borç Sarmalı",
            tagline = "Ay bitti ama borç bitmedi.",
            paragraphs = listOf(
                "Otuz günün sonunda kart borcun ${formatTL(stats.debt)} seviyesinde. Faiz her hafta biraz daha büyüyecek ve gelecek ayın kararlarını senin yerine vermeye başlayacak.",
                "İyi haber: bu bir son değil. İlk adım yeni harcamayı durdurmak, ikincisi en yüksek faizli borcu önce kapatmak. Öğrendiklerin bir sonraki ayda işe yarayacak."
            )
        )
        "scammed" in state.flags && stats.debt >= 15_000 -> StoryEnding(
            id = "scam-lesson",
            emoji = "🕵️",
            title = "Pahalı Bir Ders",
            tagline = "Dolandırıcılık bütçede iz bıraktı.",
            paragraphs = listOf(
                "Ayı borç ve kayıpla kapattın. Yaşadığın olay, finansal okuryazarlığın neden önemli olduğunu acı bir şekilde gösterdi.",
                "Bir daha aynı tuzağa düşmeyeceksin: telefonla kart bilgisi verilmez, garantili yüksek getiri olmaz ve acele ettiren herkes şüphelidir."
            )
        )
        stats.invested >= 12_000 && portfolio > stats.invested -> StoryEnding(
            id = "investor",
            emoji = "📈",
            title = "Yatırımcı",
            tagline = "Para senin için çalıştı.",
            paragraphs = listOf(
                "Yatırdığın ${formatTL(stats.invested)} ay sonunda ${formatTL(portfolio)} oldu. Risk aldın, ama planlı ve dağıtılmış risk aldın.",
                "Unutma: bu ay kazanmak, her ay kazanacağın anlamına gelmez. Vade ve çeşitlendirme olmadan tek bir iyi ay bir strateji değildir."
            )
        )
        "promoted" in state.flags && stats.knowledge >= 60 -> StoryEnding(
            id = "career",
            emoji = "🪜",
            title = "Kariyer Basamağı",
            tagline = "Hem maaş hem bilgi büyüdü.",
            paragraphs = listOf(
                "Terfi ve öğrenme yatırımı bir araya geldi. Gelirini artırmak, gideri kısmak kadar güçlü bir bütçe aracıdır.",
                "Şimdi bu ek geliri yaşam standardına değil, birikime yönlendirme zamanı. Aksi halde maaş artışı sessizce buharlaşır."
            )
        )
        "emergency" in state.flags && stats.debt == 0 -> StoryEnding(
            id = "solid-base",
            emoji = "🧯",
            title = "Sağlam Temel",
            tagline = "Borçsuz ve tamponlu bir ay.",
            paragraphs = listOf(
                "Acil durum fonu ve borçsuz bir bilanço ile ayı kapattın. Beklenmedik bir gider artık seni paniğe sokmaz.",
                "Bir sonraki hedef: bu fonu 3-6 aylık gider seviyesine çıkarmak ve birikimi enflasyona karşı korumak."
            )
        )
        stats.wellbeing >= 70 && stats.debt < 10_000 -> StoryEnding(
            id = "balanced",
            emoji = "🌿",
            title = "Dengeli Hayat",
            tagline = "Para hayatın merkezinde değil, aracı.",
            paragraphs = listOf(
                "Bütçeyi sıkı ama yaşanabilir tuttun. Refahını korurken borcu da kontrol altında tuttun.",
                "Bu denge sürdürülebilir olanıdır: kendini tamamen kısmadan tasarruf etmek, uzun vadede en kalıcı yöntemdir."
            )
        )
        else -> StoryEnding(
            id = "beginning",
            emoji = "🌱",
            title = "Yolun Başı",
            tagline = "İlk ay tamam, öğrenme yeni başlıyor.",
            paragraphs = listOf(
                "Otuz gün boyunca kararlar verdin, hatalar yaptın ve sonuçlarını gördün. Bütçe yönetimi bir sınav değil, tekrar eden bir alışkanlıktır.",
                "Bir sonraki ay aynı olaylar farklı seçeneklerle gelecek. Hedeflerini ve harcama alışkanlıklarını gözden geçirerek başla."
            )
        )
    }
}

// ─────────────────────────────────────────────────────────────
//  Periyot (akt) özeti
// ─────────────────────────────────────────────────────────────

fun actOf(day: Int): Int = ((day - 1) / 10) + 1

fun actTitle(act: Int): String = when (act) {
    1 -> "1. Hafta · Uyum"
    2 -> "2. Hafta · Baskı"
    else -> "3. Hafta · Kapanış"
}

fun actSubtitle(act: Int): String = when (act) {
    1 -> "Kira, fatura, kart ve ilk yatırım kararları"
    2 -> "Beklenmedik giderler, borç faizi ve sosyal baskı"
    else -> "Bilanço, acil durum ve ay sonu kararları"
}

data class ActSummary(
    val act: Int,
    val entries: List<StoryLogEntry>,
    val xp: Int,
    val goals: Int,
    val totalGoals: Int,
    val badges: List<RunBadge>
)

fun actSummaryFor(state: StoryState, act: Int): ActSummary {
    val from = (act - 1) * 10 + 1
    val to = act * 10
    val entries = state.log.filter { it.day in from..to }
    val actBadges = state.earnedBadges.mapNotNull { badgeById(it) }
    return ActSummary(
        act = act,
        entries = entries,
        xp = entries.sumOf { it.xp },
        goals = entries.count { it.goalDone },
        totalGoals = entries.size,
        badges = actBadges
    )
}
