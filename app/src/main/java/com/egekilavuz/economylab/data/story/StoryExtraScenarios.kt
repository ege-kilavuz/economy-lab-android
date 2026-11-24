package com.egekilavuz.economylab.data.story

/**
 * Günlük akışa karışan ek genel senaryolar.
 * Hepsi 2-29 arasındaki günlerde çıkabilir; tekrar havuzunu genişletir.
 */
val EXTRA_SCENARIOS: List<StoryEvent> = listOf(

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-bank-fee",
        title = "Banka Masrafı",
        icon = "🏦",
        scene = listOf(
            "Hesap ekstresinde \"hesap işletim ücreti\" diye 250 TL kesinti görüyorsun.",
            "Aynı ücreti geçen ay da almışlar; fark etmemişsin."
        ),
        lesson = "Banka ücretlerini düzenli kontrol et. Çoğu banka, itiraz edince ücreti iade eder.",
        choices = listOf(
            StoryChoice(
                id = "gen-fee-refund",
                label = "Müşteri hizmetlerini ara, iade iste",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 250, knowledge = 4, wellbeing = 1),
                result = "Ücret iade edildi. 250 TL geri geldi, bundan sonra ekstreleri kontrol edeceksin."
            ),
            StoryChoice(
                id = "gen-fee-pay",
                label = "Öde, uğraşma",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -250, wellbeing = -1),
                result = "Ücreti ödedin. Küçük bir tutar gibi görünse de yılda 3.000 TL eder."
            ),
            StoryChoice(
                id = "gen-fee-switch",
                label = "Ücretsiz bankaya geç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, risk = 2),
                result = "Ücretsiz bir hesap araştırdın. Geçiş biraz zahmetli ama uzun vadede kârlı."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-internet-outage",
        title = "İnternet Arızası",
        icon = "📶",
        scene = listOf(
            "İki gündür internet yok. Ne iş ne dizi; her şey durdu.",
            "Operatör \"en kısa sürede\" diyor ama tarih vermiyor."
        ),
        lesson = "Hizmet almadan önce sözleşmedeki kesinti ve tazminat maddelerini oku. Hakkını bilmek para kazandırır.",
        choices = listOf(
            StoryChoice(
                id = "gen-net-wait",
                label = "Bekle",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -3),
                result = "Bekledin. Üçüncü gün internet geldi; bu ay için küçük bir indirim yapıldı."
            ),
            StoryChoice(
                id = "gen-net-mobile",
                label = "Mobil veri paketi al",
                hint = "-200 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -200, wellbeing = 2),
                result = "Mobil veriyle idare ettin. 200 TL gitti ama işler aksamadı."
            ),
            StoryChoice(
                id = "gen-net-complain",
                label = "Şikâyet kaydı aç, tazminat iste",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, risk = -2, wellbeing = 1),
                result = "Kayıt açtın. Sözleşmedeki kesinti maddesi sayesinde faturadan indirim aldın."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-sell-old",
        title = "Elden Satış",
        icon = "📦",
        scene = listOf(
            "Kullanmadığın eski telefon ve birkaç eşya 2.500 TL ediyor.",
            "Satmak nakit yaratır; ama yedek cihaz da lazım olabilir."
        ),
        lesson = "Kullanılmayan eşyaları satmak, evdeki atıl değeri nakde çevirmenin en kolay yoludur.",
        choices = listOf(
            StoryChoice(
                id = "gen-sell-cash",
                label = "Hepsini sat",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 2_500, knowledge = 2, wellbeing = 1),
                result = "Eşyaları sattın. 2.500 TL nakit girdi, ev de ferahladı."
            ),
            StoryChoice(
                id = "gen-sell-trade",
                label = "Telefonu takas et, diğerlerini sat",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 1_500, wellbeing = 2, knowledge = 1),
                result = "Telefonu yeni bir modelle takas ettin, kalanları sattın. 1.500 TL nakit kaldı."
            ),
            StoryChoice(
                id = "gen-sell-keep",
                label = "Telefonu yedek tut, diğerlerini sat",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 800, knowledge = 2),
                result = "Telefonu yedek olarak sakladın. 800 TL geldi, ileride cihaz arızasına karşı güvendesin."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-course",
        title = "Kurs Fırsatı",
        icon = "🎓",
        scene = listOf(
            "Bütçe ve yatırım üzerine online bir kurs indirime girmiş: 1.200 TL.",
            "Ücretsiz kaynaklar da var ama düzenli bir program insanı daha hızlı ilerletiyor."
        ),
        lesson = "Bilgiye yatırım, en yüksek getirili yatırımlardan biridir; yeter ki bitir.",
        choices = listOf(
            StoryChoice(
                id = "gen-course-buy",
                label = "Kursu al",
                hint = "-1.200 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_200, knowledge = 8, wellbeing = 1),
                result = "Kursa kaydoldun. Para gitti ama bütçe ve yatırım konusunda ciddi bir temel attın."
            ),
            StoryChoice(
                id = "gen-course-free",
                label = "Ücretsiz kaynaklarla çalış",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4),
                result = "Ücretsiz içeriklerle ilerledin. Daha yavaş ama bedava."
            ),
            StoryChoice(
                id = "gen-course-later",
                label = "Ertele",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = 1, knowledge = -1),
                result = "Kursu erteledin. Bu ay kafan rahat, öğrenme de ertelendi."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-neighbor",
        title = "Komşu Yardımı",
        icon = "🏘️",
        scene = listOf(
            "Komşun acil bir ihtiyaç için 500 TL istiyor. \"Yarın vereceğim\" diyor.",
            "Tanıdık biri; ama para konusu ilişkiyi zorlayabilir."
        ),
        lesson = "Küçük borçlar bile yazılı olmayınca unutulabilir. Vereceğin tutar, kaybetmeyi göze alabileceğin kadar olmalı.",
        choices = listOf(
            StoryChoice(
                id = "gen-neighbor-give",
                label = "500 TL ver",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -500, wellbeing = 3, risk = 2),
                result = "Verdin. Komşun minnettar; para yarın gelecek mi, göreceğiz."
            ),
            StoryChoice(
                id = "gen-neighbor-half",
                label = "250 TL ver, durumunu açıkla",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -250, knowledge = 2, wellbeing = 1),
                result = "Yarısını verdin. Hem yardım ettin hem bütçeni korudun."
            ),
            StoryChoice(
                id = "gen-neighbor-no",
                label = "Reddet",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, wellbeing = -1),
                result = "Hayır dedin. Biraz mahcup oldun ama bütçen etkilenmedi."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "gen-subscription-audit",
        title = "Abonelik Denetimi",
        icon = "🔍",
        scene = listOf(
            "Telefonundaki abonelikleri listeledin: dört platform, ikisini aylardır açmıyorsun.",
            "Toplam aylık gider 600 TL."
        ),
        lesson = "Düzenli abonelik denetimi, farkına varmadan akan paranın en büyük kaynağını keser.",
        choices = listOf(
            StoryChoice(
                id = "gen-audit-cancel",
                label = "Kullanılmayanları iptal et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, wellbeing = 2),
                result = "İki aboneliği kapattın. Aylık 300 TL tasarruf, yıllık 3.600 TL."
            ),
            StoryChoice(
                id = "gen-audit-keep",
                label = "Hepsini tut",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -400, wellbeing = 3, risk = 2),
                result = "Hiçbirini iptal etmedin. Bu ay 400 TL aboneliklere gitti."
            ),
            StoryChoice(
                id = "gen-audit-share",
                label = "Hesapları paylaşıma aç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -200, knowledge = 2, wellbeing = 2),
                result = "Aile/arkadaşlarla paylaştın. Aylık maliyet 200 TL'ye indi."
            )
        )
    )
)
