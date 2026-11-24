package com.egekilavuz.economylab.data.story

/**
 * Günlük akışa karışan genel senaryolar.
 *
 * Her biri belirli bir gün aralığında (`dayRange`) herhangi bir güne denk
 * gelebilir. Seçim deterministik ama oyuncunun seed'ine ve o gün hangi
 * senaryoları gördüğüne göre değişir; böylece her oynayışta farklı olaylar
 * karşına çıkar.
 */
val GENERIC_SCENARIOS: List<StoryEvent> = listOf(

    // ── ERKEN DÖNEM (4-9) ──────────────────────────────────────
    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-subscription",
        title = "Abonelik Tuzağı",
        icon = "📺",
        scene = listOf(
            "Telefon faturanda tanımadığın bir \"dizi paketi\" aboneliği görüyorsun: aylık 300 TL.",
            "Geçen ay bedava denemeyle başlamış, süre bitince sessizce ücretliye dönmüş."
        ),
        lesson = "Ücretsiz denemeler otomatik yenilenir. Deneme başlatmadan önce iptal tarihini not al.",
        choices = listOf(
            StoryChoice(
                id = "gen-sub-cancel",
                label = "Hemen iptal et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, wellbeing = -1),
                result = "Aboneliği iptal ettin. Bu ay 300 TL cepte kaldı; keşke geçen ay fark etseydin."
            ),
            StoryChoice(
                id = "gen-sub-keep",
                label = "Bu ay kalsın, işim var",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -300, wellbeing = 3, risk = 2),
                result = "Bir ay daha izledin. Keyif aldın ama otomatik yenilenen bir giderin oldu."
            ),
            StoryChoice(
                id = "gen-sub-share",
                label = "Aileyle paylaş, masrafı böl",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -150, knowledge = 3, wellbeing = 1),
                result = "Hesabı paylaşıma açtın. Aylık maliyet yarıya indi, hesap şifresini de paylaşman gerekti."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-unexpected-bill",
        title = "Beklenmedik Fatura",
        icon = "⚡",
        scene = listOf(
            "Elektrik faturası bu ay 1.200 TL gelmiş. Geçen ay 600 TL'ydi.",
            "Sebep: eski bir ısıtıcı ve açık kalan prizler."
        ),
        lesson = "Beklenmedik fatura, bütçede tampon olmadığında doğrudan borca dönüşür.",
        choices = listOf(
            StoryChoice(
                id = "gen-bill-cash",
                label = "Nakit öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -1_200, wellbeing = -2, knowledge = 1),
                result = "Faturayı nakit kapattın. Canın sıkıldı ama borç büyümedi."
            ),
            StoryChoice(
                id = "gen-bill-card",
                label = "Kartla öde",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 1_200, risk = 5),
                result = "Faturayı karta yükledin. Bu ay nakit rahat, gelecek ay faizli."
            ),
            StoryChoice(
                id = "gen-bill-save",
                label = "Cihazı değiştir, tüketimi düşür",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -600, knowledge = 4, risk = -2),
                result = "Eski ısıtıcıyı kapattın, prizleri düzenledin. Bu ay 600 TL ödedin, sonrası daha düşük gelecek."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-used-deal",
        title = "İkinci El Fırsatı",
        icon = "🚲",
        scene = listOf(
            "İkinci el ilanlarında temiz bir bisiklet var: 2.500 TL.",
            "Ulaşım masrafını düşürebilir ama şu an bütçende boşluk yok."
        ),
        lesson = "Bir alım \"ihtiyaç\" mı yoksa \"istek\" mi? Cevap, aylık bütçene etkisiyle verilir.",
        choices = listOf(
            StoryChoice(
                id = "gen-used-buy",
                label = "Nakit al",
                hint = "-2.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -2_500, wellbeing = 3, knowledge = 2),
                result = "Bisikleti aldın. Ulaşım masrafın düşecek, ama bu ay nakit sıkıştın."
            ),
            StoryChoice(
                id = "gen-used-card",
                label = "Kartla al",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 2_500, risk = 4, wellbeing = 3),
                result = "Bisikleti karta aldın. Keyif şimdi, taksit sonra."
            ),
            StoryChoice(
                id = "gen-used-pass",
                label = "Vazgeç, önce bütçeyi toparla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, wellbeing = -1),
                result = "İlanı kapattın. İhtiyaç gerçekten acilse fırsat yine gelir."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-bank-bonus",
        title = "Banka Promosyonu",
        icon = "🎁",
        scene = listOf(
            "Banka \"maaş hesabını bize taşı, 500 TL promosyon\" diyor.",
            "Küçük yazıda 12 ay kalma şartı ve otomatik kredi kartı teklifi var."
        ),
        lesson = "Promosyonlar bedava para değildir; taahhüt, otomatik ürün ve gizli şartlar getirebilir.",
        choices = listOf(
            StoryChoice(
                id = "gen-bonus-take",
                label = "Promosyonu al, hesabı taşı",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 500, knowledge = 2, risk = 3),
                result = "500 TL hesabına geçti. 12 ay boyunca bu bankada kalmayı taahhüt ettin."
            ),
            StoryChoice(
                id = "gen-bonus-read",
                label = "Şartları oku, vazgeç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, risk = -2),
                result = "Küçük yazıyı okuyunca vazgeçtin. 500 TL için 12 ay kilitlenmek mantıklı değildi."
            ),
            StoryChoice(
                id = "gen-bonus-card",
                label = "Promosyonu kredi kartıyla birleştir",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = 800, risk = 8, wellbeing = 1),
                result = "Daha fazla avantaj aldın ama elinde yeni bir kredi kartı ve daha fazla harcama alanı var."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-friend-loan",
        title = "Arkadaş Borcu",
        icon = "🤝",
        scene = listOf(
            "Bir arkadaşın \"ay sonuna kadar 1.000 TL\" diyor. Geçen sefer zamanında ödemişti.",
            "Ama bu ay senin de nakit tamponun ince."
        ),
        lesson = "Borç verirken geri gelmeme ihtimalini bütçene yaz. Vereceğin tutar, ilişkini bozmayacak kadar olmalı.",
        choices = listOf(
            StoryChoice(
                id = "gen-loan-full",
                label = "1.000 TL ver",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -1_000, wellbeing = 3, risk = 3),
                result = "Parayı verdin. Arkadaşın rahatladı, senin tamponun inceldi."
            ),
            StoryChoice(
                id = "gen-loan-half",
                label = "500 TL ver, durumunu açıkla",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -500, knowledge = 2, wellbeing = 1),
                result = "Yarısını verdin. Hem destek oldun hem bütçeni korudun."
            ),
            StoryChoice(
                id = "gen-loan-no",
                label = "Reddet, kendi borcunu öncele",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, wellbeing = -2),
                result = "Hayır dedin. Kolay değildi ama kendi bütçen de bir sorumluluk."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-work-expense",
        title = "İş Gideri",
        icon = "🖇️",
        scene = listOf(
            "İş için kırtasiye ve küçük ekipman alman gerekiyor: 600 TL.",
            "Fişi kaybedersen masraf sende kalır."
        ),
        lesson = "İşle ilgili harcamalarda fiş ve onay süreci, paranın geri dönmesini sağlar.",
        choices = listOf(
            StoryChoice(
                id = "gen-work-self",
                label = "Cebinden al, sonra düşünürüm",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -600, wellbeing = -2, knowledge = 1),
                result = "Masrafı kendin karşıladın. Fişi de kaybettin; para geri gelmeyecek."
            ),
            StoryChoice(
                id = "gen-work-claim",
                label = "Fiş al, şirket masrafına yaz",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, wellbeing = 1),
                result = "Fişi aldın ve masraf formunu doldurdun. 600 TL geri ödenecek."
            ),
            StoryChoice(
                id = "gen-work-delay",
                label = "Ertele, kendi malzememle idare et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -1, risk = 2),
                result = "Alımı erteledin. Bu ay para çıkmadı ama iş biraz aksadı."
            )
        )
    ),

    // ── ORTA DÖNEM (11, 14-18) ─────────────────────────────────
    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-credit-score",
        title = "Kredi Notu",
        icon = "📊",
        scene = listOf(
            "Kredi notunu merak ediyorsun. Banka uygulamasında ücretsiz sorgulama var.",
            "Bir arkadaşın \"kredi çekersen notun yükselir\" diyor."
        ),
        lesson = "Kredi notu borç kullanımı, ödeme düzeni ve mevcut borçlarla ilgilidir. Sırf yükselsin diye kredi çekilmez.",
        choices = listOf(
            StoryChoice(
                id = "gen-score-check",
                label = "Ücretsiz sorgula",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6),
                result = "Notunu gördün ve raporundaki bir hatayı fark ettin. Bilgi, gereksiz kredi çekmekten iyidir."
            ),
            StoryChoice(
                id = "gen-score-loan",
                label = "\"Not yükselsin\" diye kredi çek",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = 8_000, debt = 8_000, risk = 10, knowledge = 1),
                result = "8.000 TL kredi çektin. Notun değil, borcun arttı; faizi de cabası."
            ),
            StoryChoice(
                id = "gen-score-ignore",
                label = "Umursama, nasılsa lazım değil",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(risk = 3, knowledge = -1),
                result = "Raporu açmadın. İleride kredi ihtiyacın olduğunda neyle karşılaşacağını bilmiyorsun."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-phishing-sms",
        title = "Şüpheli SMS",
        icon = "📩",
        scene = listOf(
            "\"Kargonuz gümrükte bekliyor, 45 TL ödeme yapın\" diye bir SMS geldi. Link kısaltılmış.",
            "Son zamanlarda sipariş vermiş olabilirsin; bu yüzden inandırıcı geliyor."
        ),
        lesson = "Kargo, banka ve kamu kurumları SMS linkiyle ödeme istemez. Resmi uygulamadan kontrol et.",
        choices = listOf(
            StoryChoice(
                id = "gen-phish-delete",
                label = "Linke tıklama, mesajı sil",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, risk = -4),
                result = "Mesajı sildin. Kargo takibini resmi uygulamadan yaptın; sorun yoktu."
            ),
            StoryChoice(
                id = "gen-phish-click",
                label = "Linke tıkla, 45 TL öde",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -2_000, risk = 12, wellbeing = -4),
                result = "Kart bilgilerin çalındı. 45 TL'lik \"kargo\" 2.000 TL'ye mal oldu."
            ),
            StoryChoice(
                id = "gen-phish-call",
                label = "Kargo firmasını kendin ara",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 7, risk = -3, wellbeing = 1),
                result = "Firmayı resmi numaradan aradın. Böyle bir bildirim olmadığını öğrendin."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-team-dinner",
        title = "İş Yemeği",
        icon = "🍽️",
        scene = listOf(
            "Ekip akşam yemeği organize ediyor. Kişi başı 900 TL.",
            "Katılmamak kariyer açısından sorun olur mu diye düşünüyorsun."
        ),
        lesson = "Sosyal harcamalar kariyerin parçası olabilir ama bütçenin tamamını yememeli. Alternatif sunmak her zaman mümkün.",
        choices = listOf(
            StoryChoice(
                id = "gen-dinner-join",
                label = "Katıl, tam öde",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -900, wellbeing = 5, risk = 1),
                result = "Ekiple güzel bir akşam geçirdin. 900 TL gitti, ilişkiler güçlendi."
            ),
            StoryChoice(
                id = "gen-dinner-skip",
                label = "\"Bütçem yok\" de, katılma",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = -3, knowledge = 3),
                result = "Katılmadın. Bütçen rahatladı ama biraz dışarıda kalmış hissettin."
            ),
            StoryChoice(
                id = "gen-dinner-split",
                label = "Daha uygun bir mekân öner",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -450, wellbeing = 3, knowledge = 2),
                result = "Daha uygun bir yer buldun. Ekip de memnun kaldı, bütçen de yarısını ödedi."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 10..18,
        id = "gen-vacation",
        title = "Tatil Planı",
        icon = "🏖️",
        scene = listOf(
            "Arkadaşların hafta sonu için bir tatil planı yapmış: kişi başı 6.000 TL.",
            "Bu ay bütçende o kadar boşluk yok ama \"bir kere yaşanır\" diyorsun."
        ),
        lesson = "Tatil bir ihtiyaç olabilir; ama borçla yapılan tatil, dönüşte daha büyük bir yük bırakır.",
        choices = listOf(
            StoryChoice(
                id = "gen-vac-full",
                label = "Plana katıl",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -6_000, wellbeing = 10, risk = 3),
                result = "Tatil harikaydı. Döndüğünde bütçen seni bekliyordu."
            ),
            StoryChoice(
                id = "gen-vac-cheap",
                label = "Daha yakın ve ucuz bir kaçamak yap",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -2_000, wellbeing = 6, knowledge = 2),
                result = "Daha mütevazı bir plan yaptın. Hem dinlendin hem bütçeyi tamamen bitirmedin."
            ),
            StoryChoice(
                id = "gen-vac-postpone",
                label = "Ertele, birikimi tamamla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = -4, knowledge = 3),
                result = "Bu kez gitmedin. Canın sıkıldı ama gelecek ay için alan açtın."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-health-check",
        title = "Check-up",
        icon = "🩺",
        scene = listOf(
            "Uzun zamandır kontrole gitmedin. Özel hastanede check-up 1.500 TL.",
            "Devlet hastanesinde ücretsiz ama sıra var."
        ),
        lesson = "Koruyucu sağlık harcaması, hastalık çıktıktan sonraki maliyetten genellikle daha düşüktür.",
        choices = listOf(
            StoryChoice(
                id = "gen-health-private",
                label = "Özelde check-up yaptır",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_500, wellbeing = 6, knowledge = 2),
                result = "Kapsamlı kontrol oldun. Küçük bir eksiklik erken yakalandı."
            ),
            StoryChoice(
                id = "gen-health-state",
                label = "Devlet hastanesine git",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 3, knowledge = 2),
                result = "Sıra bekledin ama kontrol oldun. Cebinden para çıkmadı."
            ),
            StoryChoice(
                id = "gen-health-postpone",
                label = "Ertele",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(wellbeing = -4, risk = 3),
                result = "Kontrolü erteledin. Bu ay para harcamadın ama belirsizlik büyüdü."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-raise-request",
        title = "Zam Pazarlığı",
        icon = "💬",
        scene = listOf(
            "Performansın iyi gidiyor. Yöneticinle zam görüşmesi yapabilirsin.",
            "Hazırlıksız gidersen \"bütçe yok\" cevabıyla dönebilirsin."
        ),
        lesson = "Zam pazarlığında piyasa verisi, somut katkı ve net bir talep en güçlü üç argümandır.",
        choices = listOf(
            StoryChoice(
                id = "gen-raise-prep",
                label = "Hazırlan, verilerle pazarlık yap",
                hint = "Bilgi 50+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 50, note = "Bilgi 50+"),
                effect = ChoiceEffect(cash = 3_000, knowledge = 6, wellbeing = 3),
                result = "Katkılarını ve piyasa verilerini sundun. 3.000 TL zam aldın."
            ),
            StoryChoice(
                id = "gen-raise-wait",
                label = "Kabul et, zamanı gelsin",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 2, wellbeing = -2),
                result = "Görüşmeyi erteledin. Bu ay ek gelir yok."
            ),
            StoryChoice(
                id = "gen-raise-leave",
                label = "Pazarlık yerine iş bakmaya başla",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(risk = 6, knowledge = 3, wellbeing = 1),
                result = "Piyasayı taramaya başladın. Belki daha iyi bir teklif çıkar, belki de mevcut işin kıymetini anlarsın."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-rent-insurance",
        title = "Kira Sigortası",
        icon = "🏠",
        scene = listOf(
            "Ev sahibi \"kiracı sigortası yaptırırsan depozito riski azalır\" diyor.",
            "Yıllık 1.200 TL. Yangın, su baskını ve komşu hasarını kapsıyor."
        ),
        lesson = "Sigorta, büyük ve beklenmedik hasarın maliyetini küçük düzenli ödemeye çevirir.",
        choices = listOf(
            StoryChoice(
                id = "gen-rent-ins-buy",
                label = "Yaptır",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -1_200, risk = -6, knowledge = 4),
                result = "Poliçeyi yaptırdın. Aylık 100 TL'ye büyük bir riski kapattın."
            ),
            StoryChoice(
                id = "gen-rent-ins-ask",
                label = "Ev sahibine kapsamı sor",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 3),
                result = "Poliçeyi inceledin; bazı hasarların ev sahibinin sigortasında olduğunu öğrendin."
            ),
            StoryChoice(
                id = "gen-rent-ins-skip",
                label = "Ertele",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(risk = 5),
                result = "Sigortayı erteledin. Bu ay 1.200 TL cepte; olası bir hasarın maliyeti çok daha yüksek."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-phone-bill",
        title = "Fatura Şoku",
        icon = "📱",
        scene = listOf(
            "Telefon faturan 900 TL gelmiş. Normalde 450 TL ödüyorsun.",
            "Yurt dışı veri kullanımı ve ek paketler devreye girmiş."
        ),
        lesson = "Faturalardaki ek paketleri ve otomatik yenilemeleri düzenli kontrol etmek, sessiz giderleri durdurur.",
        choices = listOf(
            StoryChoice(
                id = "gen-phone-pay",
                label = "Faturayı öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -900, wellbeing = -1),
                result = "Faturayı ödedin. Can sıkıcı ama gecikme faizi yok."
            ),
            StoryChoice(
                id = "gen-phone-card",
                label = "Kartla öde",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 900, risk = 4),
                result = "Karta yükledin. Bu ay nakit rahat, gelecek ay borç."
            ),
            StoryChoice(
                id = "gen-phone-cut",
                label = "Ek paketleri iptal et, tarifeyi düşür",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -500, knowledge = 4, wellbeing = -2),
                result = "Gereksiz paketleri kapattın. Bu ay 500 TL ödedin, sonrası daha düşük."
            )
        )
    ),

    // ── GEÇ DÖNEM (21-26, 28-29) ───────────────────────────────
    StoryEvent(
        day = 0,
        dayRange = 19..29,
        id = "gen-tax",
        title = "Vergi Bildirimi",
        icon = "🧾",
        scene = listOf(
            "Ek gelirlerin için vergi bildirimi zamanı geldi. Tahmini borç 2.000 TL.",
            "Geciktirirsen ceza ve faiz işleyecek."
        ),
        lesson = "Vergi, gelirin bir parçasıdır. Zamanında beyan etmek, sonradan ceza ödemekten her zaman daha ucuzdur.",
        choices = listOf(
            StoryChoice(
                id = "gen-tax-pay",
                label = "Peşin öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -2_000, knowledge = 5, risk = -3),
                result = "Vergiyi peşin ödedin. Ceza yok, kafan rahat."
            ),
            StoryChoice(
                id = "gen-tax-installment",
                label = "Taksitlendir",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(debt = 2_100, knowledge = 2, risk = 3),
                result = "Taksit yaptın. Aylık yük azaldı ama toplamda 100 TL fazla ödeyeceksin."
            ),
            StoryChoice(
                id = "gen-tax-advisor",
                label = "Danışmana sor, doğru beyan et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -800, knowledge = 7, risk = -2),
                result = "Danışman bir indirim kalemi buldu. 800 TL ücret ödedin ama 2.000 TL'lik borcun bir kısmı düştü."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 19..29,
        id = "gen-car-repair",
        title = "Araç Masrafı",
        icon = "🚗",
        scene = listOf(
            "Arabanın fren balataları bitmiş. Tamir 3.500 TL.",
            "Sanayiye göre fiyat değişiyor; birkaç yerden teklif alabilirsin."
        ),
        lesson = "Beklenmedik tamir giderleri için acil fon şart. Teklif almak da tasarrufun parçasıdır.",
        choices = listOf(
            StoryChoice(
                id = "gen-car-cash",
                label = "Nakit yaptır",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -3_500, wellbeing = 2, risk = -2),
                result = "Tamiri nakit yaptırdın. Güvenli sürüş, borçsuz bütçe."
            ),
            StoryChoice(
                id = "gen-car-card",
                label = "Kartla yaptır",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 3_500, risk = 5),
                result = "Karta yükledin. Araç hazır, borç gelecek ay."
            ),
            StoryChoice(
                id = "gen-car-transit",
                label = "Toplu taşımaya geç, tamiri ertele",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 4, wellbeing = -3, risk = 2),
                result = "Bir süre toplu taşıma kullandın. Para cepte kaldı, konfor azaldı."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 19..29,
        id = "gen-newyear-shopping",
        title = "Yılbaşı Alışverişi",
        icon = "🎄",
        scene = listOf(
            "Yılbaşı indirimleri başladı. Sepetinde 2.500 TL'lik ürün var; yarısı gerçekten ihtiyaç.",
            "\"Bir kere yılda\" cümlesi yine kulağında."
        ),
        lesson = "İndirim, ihtiyaç olmayan ürünü almaya bahane değildir. Liste ve bütçe sınırı koy.",
        choices = listOf(
            StoryChoice(
                id = "gen-ny-full",
                label = "Sepeti tamamla",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -2_500, wellbeing = 7, risk = 2),
                result = "Alışverişi tamamladın. Kısa vadeli mutluluk, uzun vadeli pişmanlık ihtimali."
            ),
            StoryChoice(
                id = "gen-ny-handmade",
                label = "Listeyi yarıya indir, el emeği ekle",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -500, knowledge = 3, wellbeing = 4),
                result = "Sadece ihtiyaçları aldın, geri kalanı el emeğiyle hallettin. Bütçe de gönül de rahat."
            ),
            StoryChoice(
                id = "gen-ny-skip",
                label = "Alışverişi tamamen atla",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -4, knowledge = 2),
                result = "Bu yıl alışveriş yapmadın. Bütçen güçlendi, biraz burukluk kaldı."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-home-repair",
        title = "Acil Tamir",
        icon = "🔧",
        scene = listOf(
            "Banyoda su kaçağı var. Tesisatçı 1.800 TL istiyor.",
            "Ertelemek duvarı ve komşuyu da etkileyebilir."
        ),
        lesson = "Küçük ve acil tamirleri ertelemek, hasarı büyütür. Acil fon tam olarak bunun için var.",
        choices = listOf(
            StoryChoice(
                id = "gen-repair-cash",
                label = "Nakit yaptır",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -1_800, wellbeing = 2, risk = -3),
                result = "Tamir yapıldı. Sorun büyümeden çözüldü."
            ),
            StoryChoice(
                id = "gen-repair-card",
                label = "Kartla yaptır",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 1_800, risk = 4),
                result = "Tamir yapıldı ama borç hanesine yazıldı."
            ),
            StoryChoice(
                id = "gen-repair-postpone",
                label = "Ertele, kova koy",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(wellbeing = -5, risk = 4),
                result = "Kovayla idare ettin. Su sızıntısı büyürse maliyet katlanacak."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-invest-offer",
        title = "Yatırım Fırsatı",
        icon = "📈",
        scene = listOf(
            "Bir arkadaşın \"bu fon kesin kazandırıyor\" diyor. Son üç ayda %18 getirmiş.",
            "Ama senin vaden ve risk toleransın ne?"
        ),
        lesson = "Geçmiş getiri gelecek getiriyi garanti etmez. Yatırım kararı vade, risk ve çeşitlendirme ile verilir.",
        choices = listOf(
            StoryChoice(
                id = "gen-invest-big",
                label = "3.000 TL koy",
                hint = "-3.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 3_000, risk = 5, knowledge = 4),
                result = "Fona 3.000 TL koydun. Getiri değişken; artık portföyünü takip edeceksin."
            ),
            StoryChoice(
                id = "gen-invest-small",
                label = "Araştır, 1.000 TL ile başla",
                hint = "-1.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(invest = 1_000, knowledge = 6, risk = 3),
                result = "Fonun içeriğini okudun, küçük bir başlangıç yaptın. Kaybetsen bile bütçen sarsılmaz."
            ),
            StoryChoice(
                id = "gen-invest-pass",
                label = "Vadem kısa, girme",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, risk = -3),
                result = "Kısa vadeli ihtiyaç paranı riskli varlığa koymadın. Bu da bir karar."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-family-visit",
        title = "Aile Ziyareti",
        icon = "🚌",
        scene = listOf(
            "Ailen seni birkaç günlüğüne bekliyor. Otobüs bileti 800 TL.",
            "Bu ay bütçe sıkı; ama ziyaret de önemli."
        ),
        lesson = "İlişkiler de bütçenin parçasıdır. Ucuz ama düzenli ziyaret, pahalı ama seyrek ziyaretten daha sürdürülebilir.",
        choices = listOf(
            StoryChoice(
                id = "gen-family-go",
                label = "Bileti al, git",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -800, wellbeing = 6, risk = 1),
                result = "Ailene gittin. 800 TL gitti, moral depolandı."
            ),
            StoryChoice(
                id = "gen-family-postpone",
                label = "Bu ay ertele, telefonla idare et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -3, knowledge = 2),
                result = "Ziyareti erteledin. Bütçe rahatladı, özlem arttı."
            ),
            StoryChoice(
                id = "gen-family-gift",
                label = "Git ve küçük bir hediye al",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -1_200, wellbeing = 8, risk = 1),
                result = "Hediyele gittin. Masraf biraz daha büyük ama gönüller kazanıldı."
            )
        )
    )
)


