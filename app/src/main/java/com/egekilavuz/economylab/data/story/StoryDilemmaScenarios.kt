package com.egekilavuz.economylab.data.story

/**
 * "İki seçenek de acıtır" tipi ikilem senaryoları.
 * Amaç doğru cevabı bilmek değil, farklı maliyetler arasında seçim yapmak.
 */
val DILEMMA_SCENARIOS: List<StoryEvent> = listOf(

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-raise-time",
        title = "Zam mı, Zaman mı?",
        icon = "⏳",
        scene = listOf(
            "Patron iki seçenek sundu: ayda 4.000 TL zam ama günde 2 saat fazla mesai; ya da aynı maaşla haftada 4 gün çalışma.",
            "İkisi de cazip; ikisi de bir şeyden çalıyor."
        ),
        lesson = "Gelir ile zaman arasında seçim yaparken saatlik değerini hesapla. Fazla mesai her zaman 'bedava para' değildir.",
        choices = listOf(
            StoryChoice(
                id = "dil-raise-take",
                label = "Zamı al, fazla mesaiye kal",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 4_000, wellbeing = -6, boss = 4, knowledge = 1),
                result = "Zam geldi ama akşamların gitti. Hesabında daha çok para, hayatında daha az zaman var."
            ),
            StoryChoice(
                id = "dil-raise-time",
                label = "4 gün çalış, zamanı seç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 6, boss = -2, knowledge = 2),
                result = "Haftada bir gün kazandın. Para artmadı ama dinlenme ve öğrenme için alan açıldı."
            ),
            StoryChoice(
                id = "dil-raise-both",
                label = "İkisini de iste: zam + esnek gün",
                hint = "Bilgi 60+",
                tone = ChoiceTone.Risky,
                requires = ChoiceRequirement(minKnowledge = 60, note = "Bilgi 60+"),
                effect = ChoiceEffect(cash = 2_000, wellbeing = 2, boss = 1, knowledge = 3),
                result = "Patron kısmi zam ve esnek gün verdi. Mükemmel değil ama dengeli bir kazanım."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-far-home",
        title = "Uzak Ev, Ucuz Kira",
        icon = "🚇",
        scene = listOf(
            "Daha ucuz bir ev buldun ama işe 1,5 saat uzakta. Yol parası ve yorgunluk cabası.",
            "Şu anki ev pahalı ama hayatın merkezinde."
        ),
        lesson = "Kira tasarrufu, ulaşım ve zaman maliyetiyle birlikte hesaplanmalı. En düşük kira her zaman en düşük maliyet değildir.",
        choices = listOf(
            StoryChoice(
                id = "dil-far-move",
                label = "Taşın, kiradan tasarruf et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 1_500, wellbeing = -4, knowledge = 3, landlord = -5),
                result = "Daha ucuz eve taşındın. Kira düştü ama günün üç saati yolda geçecek."
            ),
            StoryChoice(
                id = "dil-far-stay",
                label = "Kal, hayatını merkezde tut",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 2, knowledge = 3),
                result = "Taşınmadın. Kira yüksek ama zaman ve enerji sende kaldı."
            ),
            StoryChoice(
                id = "dil-far-remote",
                label = "Uzaktan çalışmayı pazarlık et, taşın",
                hint = "Bilgi 50+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 50, note = "Bilgi 50+"),
                effect = ChoiceEffect(cash = 1_000, wellbeing = -2, knowledge = 4, landlord = -5),
                result = "Uzaktan çalışma pazarlığı yaptın ve taşındın. Yol sorunu büyük ölçüde çözüldü."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-cash-installment",
        title = "Peşin mi, Taksit mi?",
        icon = "🛋️",
        scene = listOf(
            "İhtiyacın olan bir eşya var: 5.000 TL.",
            "Peşin alırsan %10 indirim; taksit yaparsan bu ay nakit elinde kalır ama toplamda daha fazla ödersin."
        ),
        lesson = "Taksit, satın alma kararını kolaylaştırır ama toplam maliyeti gizler. Peşin indirim gerçek bir getiridir.",
        choices = listOf(
            StoryChoice(
                id = "dil-inst-cash",
                label = "Peşin al, %10 indirim",
                hint = "-4.500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -4_500, knowledge = 2),
                result = "4.500 TL ödedin. Nakit azaldı ama toplamda 500 TL kazandın."
            ),
            StoryChoice(
                id = "dil-inst-plan",
                label = "6 taksit yap",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    debt = 5_000, risk = 5,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Taksit ödemesi",
                            days = 9,
                            message = "Eşyanın taksit ödemesi geldi: +500 TL borç",
                            debt = 500
                        )
                    )
                ),
                result = "Bu ay nakit rahatladı ama 6 ay boyunca her ay ek bir ödeme var."
            ),
            StoryChoice(
                id = "dil-inst-skip",
                label = "Vazgeç, eskiyle idare et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 3, wellbeing = -2),
                result = "Almadın. Bütçe korundu, konfor ertelendi."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-insure-cash",
        title = "Sigorta mı, Nakit mi?",
        icon = "🛡️",
        scene = listOf(
            "Kapsamlı bir sigorta paketi yıllık 2.000 TL. Yangın, su baskını ve hırsızlığı kapsıyor.",
            "Ödemezsen elinde 2.000 TL kalır; ama ya bir şey olursa?"
        ),
        lesson = "Sigorta, düşük olasılıklı ama yüksek maliyetli olayları yönetmenin yoludur. Risk her zaman 'bu ay' gerçekleşmez.",
        choices = listOf(
            StoryChoice(
                id = "dil-ins-full",
                label = "Tam sigorta yaptır",
                hint = "-2.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -2_000, risk = -8, knowledge = 3),
                result = "Poliçeyi yaptırdın. 2.000 TL gitti, büyük bir belirsizlik kapandı."
            ),
            StoryChoice(
                id = "dil-ins-partial",
                label = "Sadece temel paketi al",
                hint = "-1.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_000, risk = -3, knowledge = 2),
                result = "Temel koruma var. Büyük hasarların bir kısmı hâlâ sende."
            ),
            StoryChoice(
                id = "dil-ins-skip",
                label = "Yaptırma, nakit bende kalsın",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    risk = 6,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Sigortasız hasar",
                            days = 8,
                            message = "Küçük bir su baskını hasarı kendin ödedin: +2.500 TL borç",
                            debt = 2_500
                        )
                    )
                ),
                result = "2.000 TL cebinde kaldı. Ama risk artık tamamen sende."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-course-trip",
        title = "Kurs mu, Tatil mi?",
        icon = "🎯",
        scene = listOf(
            "Elinde 3.000 TL var. Bir yanda kariyerine katkı sağlayacak kurs, diğer yanda hafta sonu kaçamağı.",
            "İkisi de “kendine yatırım” gibi hissettiriyor."
        ),
        lesson = "Her harcamanın getirisi farklıdır: biri bilgi, diğeri dinlenme. Bütçe, ikisini dengelemekle ilgilidir.",
        choices = listOf(
            StoryChoice(
                id = "dil-ct-course",
                label = "Kursu seç",
                hint = "-3.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -3_000, knowledge = 8, wellbeing = -2),
                result = "Kursa yazıldın. Kısa vadede yorgunluk, uzun vadede daha güçlü bir CV."
            ),
            StoryChoice(
                id = "dil-ct-trip",
                label = "Tatili seç",
                hint = "-3.000 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -3_000, wellbeing = 9),
                result = "Hafta sonu kaçamağı harikaydı. Enerjin yerine geldi, bilgi aynı kaldı."
            ),
            StoryChoice(
                id = "dil-ct-both",
                label = "Ucuz kurs + kısa kaçamak",
                hint = "-1.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_500, knowledge = 4, wellbeing = 4),
                result = "İkisinden de biraz aldın. Mükemmel değil ama dengeli."
            ),
            StoryChoice(
                id = "dil-ct-wait",
                label = "İkisini de ertele",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 2, wellbeing = -2),
                result = "Para elinde kaldı. Fırsatlar kaçtı ama tampon korundu."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-crypto-fund",
        title = "Kripto mu, Acil Fon mu?",
        icon = "🪙",
        scene = listOf(
            "Elinde 5.000 TL var. Kripto son bir ayda %40 yükselmiş; acil fonun ise hâlâ eksik.",
            "İkisi de mantıklı görünüyor, ama farklı riskler taşıyor."
        ),
        lesson = "Yatırım yapmadan önce acil fonunu tamamla. Yüksek getiri, yüksek riskin gizlenmiş hâlidir.",
        choices = listOf(
            StoryChoice(
                id = "dil-cf-crypto",
                label = "Kriptoya gir",
                hint = "-5.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(invest = 5_000, risk = 12, knowledge = 2, setFlags = listOf("crypto", "investor")),
                result = "5.000 TL kriptoya gitti. Portföyün büyüdü, geceleri grafik kontrol etmeye başladın."
            ),
            StoryChoice(
                id = "dil-cf-fund",
                label = "Acil fonu tamamla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, risk = -5, setFlags = listOf("emergency")),
                result = "Parayı acil fon olarak ayırdın. Getirisi yok ama kriz anında borçlanmayacaksın."
            ),
            StoryChoice(
                id = "dil-cf-split",
                label = "Yarısını yatır, yarısını fonda tut",
                hint = "-2.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 2_500, risk = 5, knowledge = 3, setFlags = listOf("investor")),
                result = "Dengeli gittin. Ne tamamen risksiz ne tamamen kumar."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-work-health",
        title = "İş mi, Sağlık mı?",
        icon = "🦷",
        scene = listOf(
            "Diş ağrın var. Aynı gün işte kritik bir sunum var.",
            "Kliniğe gidersen sunumu kaçıracaksın; gitmezsen ağrı ve iltihap büyüyecek."
        ),
        lesson = "Sağlığı ertelemek, kısa vadede işi kurtarır gibi görünür; uzun vadede hem sağlık hem iş kaybı getirebilir.",
        choices = listOf(
            StoryChoice(
                id = "dil-wh-doctor",
                label = "Kliniğe git, sunumu kaçır",
                hint = "-2.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -2_000, wellbeing = 7, boss = -3),
                result = "Tedavi oldun. Sunum kaçtı ama ağrı bitti ve sorun büyümedi."
            ),
            StoryChoice(
                id = "dil-wh-work",
                label = "Sunuma git, ağrıyı ertele",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(boss = 4, wellbeing = -5, risk = 3),
                result = "Sunumu kurtardın. Patron memnun, dişin değil."
            ),
            StoryChoice(
                id = "dil-wh-postpone",
                label = "Ağrı kesiciyle ikisini de idare et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -3, risk = 3, knowledge = 1),
                result = "Günü kurtardın. Ama sorun ertelendi, ertelenen sağlık genelde daha pahalıdır."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-friend-cosign",
        title = "Arkadaş mı, Doğru mu?",
        icon = "✍️",
        scene = listOf(
            "Deniz senden kredi başvurusuna kefil olmanı istiyor. \"Sadece imza, ödemeyi ben yapacağım\" diyor.",
            "Kefil olmak, onun borcunu senin borcun hâline getirir."
        ),
        lesson = "Kefillik duygusal bir iyilik değil, hukuki bir borçtur. Hayır demek ilişkiyi bitirmez; borç bitirebilir.",
        choices = listOf(
            StoryChoice(
                id = "dil-fc-yes",
                label = "Kefil ol",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(friend = 8, risk = 15, knowledge = -2),
                result = "İmzayı attın. Deniz rahatladı; senin bilançonda görünmeyen bir risk var."
            ),
            StoryChoice(
                id = "dil-fc-no",
                label = "Reddet",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(friend = -10, knowledge = 4),
                result = "Hayır dedin. İlişki gerildi ama bütçen ve kredi notun korundu."
            ),
            StoryChoice(
                id = "dil-fc-help",
                label = "Kefil olma, bütçe planı yapmasına yardım et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(friend = 3, knowledge = 5, wellbeing = 1),
                result = "İmza atmadın ama Deniz'e borcunu nasıl yöneteceğini gösterdin. Daha değerli bir yardım."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-tax-honesty",
        title = "Şeffaflık mı, Gizlilik mi?",
        icon = "🧾",
        scene = listOf(
            "Ek işten gelen 6.000 TL'yi beyan etmen gerekiyor. Vergi çıkacak; beyan etmezsen risk var.",
            "İş yerin de ek gelirini öğrenirse tepki verebilir."
        ),
        lesson = "Vergi beyanı bir tercih değil yükümlülüktür. Gizlemek kısa vadede kâr, uzun vadede ceza ve itibar kaybıdır.",
        choices = listOf(
            StoryChoice(
                id = "dil-th-declare",
                label = "Beyan et, vergini öde",
                hint = "-800 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -800, knowledge = 6, risk = -5, boss = 2),
                result = "Vergini ödedin. Elde 5.200 TL kaldı ama kafan rahat."
            ),
            StoryChoice(
                id = "dil-th-hide",
                label = "Beyan etme",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    risk = 10,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Vergi incelemesi",
                            days = 9,
                            message = "Beyan edilmeyen gelir tespit edildi: +2.000 TL ceza",
                            debt = 2_000,
                            setFlags = listOf("tax_penalty")
                        )
                    )
                ),
                result = "6.000 TL cebinde kaldı. Ama bir mektup gelirse şaşırma."
            ),
            StoryChoice(
                id = "dil-th-advisor",
                label = "Mali müşavire sor",
                hint = "-300 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -300, knowledge = 7, risk = -3),
                result = "Müşavir bir indirim buldu. 300 TL ücret ödedin, 800 TL'lik vergiyi 400 TL'ye indirdin."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-debt-invest",
        title = "Erken Öde mi, Yatırım mı?",
        icon = "⚖️",
        scene = listOf(
            "Elinde 6.000 TL var. Kart borcun 6.000 TL; yatırım fırsatı da cazip.",
            "Borç faizi garanti gider, yatırım getirisi garanti değil."
        ),
        lesson = "Yüksek faizli borcu kapatmak, garanti getirili bir yatırımdır. Yatırım getirisi borç faizinden düşükse borç ödemek daha mantıklıdır.",
        choices = listOf(
            StoryChoice(
                id = "dil-di-pay",
                label = "Borcun tamamını kapat",
                hint = "-6.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -6_000, debt = -6_000, knowledge = 3, risk = -4),
                result = "Borç kapandı. Nakit azaldı ama faiz yükü tamamen bitti."
            ),
            StoryChoice(
                id = "dil-di-invest",
                label = "Yatırıma yönlendir",
                hint = "-6.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(invest = 6_000, risk = 8, knowledge = 2),
                result = "6.000 TL yatırıma gitti. Borç duruyor, getiri belirsiz."
            ),
            StoryChoice(
                id = "dil-di-split",
                label = "Yarısını borca, yarısını yatırıma",
                hint = "-6.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -3_000, debt = -3_000, invest = 3_000, knowledge = 3),
                result = "Dengeli böldün. Ne borç tamamen bitti ne de tüm para riskte."
            ),
            StoryChoice(
                id = "dil-di-wait",
                label = "Dokunma, nakit tamponunu koru",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, risk = -1),
                result = "Parayı elinde tuttun. Borç faizi işlemeye devam edecek, ama nakit tamponun var."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-comfort-saving",
        title = "Konfor mu, Tasarruf mu?",
        icon = "🛏️",
        scene = listOf(
            "Daha ucuz bir eve geçmek aylık 2.000 TL kazandırır ama konfor düşer.",
            "Ev arkadaşı almak da bir seçenek; hem tasarruf hem sosyal hayat değişir."
        ),
        lesson = "Tasarruf, yaşam kalitesinden feragat etmek zorunda değil. Ama her konforun bir aylık maliyeti vardır.",
        choices = listOf(
            StoryChoice(
                id = "dil-cs-move",
                label = "Daha ucuz eve geç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 2_000, wellbeing = -3, knowledge = 3, landlord = -5),
                result = "Taşındın. Aylık 2.000 TL cepte, konfor biraz düştü."
            ),
            StoryChoice(
                id = "dil-cs-stay",
                label = "Kal, konforu koru",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = 2, risk = 2, knowledge = 1),
                result = "Taşınmadın. Hayatın rahat ama bütçede tasarruf fırsatı kaçtı."
            ),
            StoryChoice(
                id = "dil-cs-roommate",
                label = "Ev arkadaşı al",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 1_000, wellbeing = -2, knowledge = 2, landlord = 2),
                result = "Kirayı bölüştün. Para rahatladı, özel alan azaldı."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 2..29,
        id = "dil-now-later",
        title = "Bugün mü, Yarın mı?",
        icon = "🕰️",
        scene = listOf(
            "İhtiyacın olan bir ürün bugün indirimde: 2.000 TL. Bir hafta sonra fiyatı 2.500 TL olacak.",
            "Ama bu ay bütçende o kadar boşluk yok."
        ),
        lesson = "İndirim, ihtiyaç varsa fırsattır; yoksa sadece harcamayı öne çeker. Fiyat artışı korkusuyla alışveriş yapmak bütçeyi bozar.",
        choices = listOf(
            StoryChoice(
                id = "dil-nl-buy",
                label = "Bugün al, indirimi kaçırma",
                hint = "-2.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -2_000, wellbeing = 4, knowledge = 1),
                result = "Aldın. 500 TL tasarruf ettin ama bu ay nakit sıkıştı."
            ),
            StoryChoice(
                id = "dil-nl-wait",
                label = "Bekle, bütçeyi toparla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, wellbeing = -2),
                result = "Bekledin. İndirim kaçtı ama bütçen korundu."
            ),
            StoryChoice(
                id = "dil-nl-used",
                label = "İkinci el alternatif ara",
                hint = "-800 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -800, knowledge = 4, wellbeing = 1),
                result = "İkinci el temiz bir alternatif buldun. Hem ihtiyacı giderdin hem bütçeyi korudun."
            )
        )
    )
)
