package com.egekilavuz.economylab.data.story

/**
 * "30 Gün" hikâye içeriği.
 *
 * Tema: Türkiye'de yeni mezun bir gencin ilk maaş ayı.
 * Her gün bir olay, her olayda 2-4 seçim. Seçimler Nakit, Borç, Bilgi,
 * Refah ve Risk göstergelerini değiştirir. Bazı seçimler bilgi eşiği ister.
 *
 * Not: "invest" alanı nakitten düşülüp yatırıma eklenir; ayrıca negatif
 * "cash" yazılmamalıdır.
 */

private fun has(s: StoryState, flag: String) = flag in s.flags

val STORY_EVENTS: List<StoryEvent> = listOf(

    // ── GÜN 1 ─────────────────────────────────────────────────
    StoryEvent(
        day = 1,
        title = "Maaş Günü",
        icon = "💸",
        scene = listOf(
            "22 yaşındasın ve üniversiteden yeni mezun oldun. İlk tam zamanlı işine başladın; bugün net 32.000 TL maaşın hesabına yattı.",
            "Öğrencilikten kalan 8.000 TL birikiminle birlikte elinde 40.000 TL var. Ama kira, faturalar ve o çok sevdiğin kahve zinciri de bu ay seni bekliyor."
        ),
        lesson = "İlk kural: para gelir gelmez bir kısmını görünmez yapmak, ay sonunda onu harcamamayı kolaylaştırır.",
        choices = listOf(
            StoryChoice(
                id = "d1-debt",
                label = "Önce öğrencilikten kalan kart borcunu kapat",
                hint = "4.000 TL borç",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -4_000, debt = -4_000, knowledge = 4, wellbeing = 4, risk = -4),
                result = "4.000 TL'lik kart borcunu kapattın. Faiz işlemeye başlamadan borçtan kurtulmak, ayın en kârlı hamlesi oldu."
            ),
            StoryChoice(
                id = "d1-invest",
                label = "Maaşın %20'sini birikim fonuna ayır",
                hint = "8.000 TL yatırıma",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(invest = 8_000, knowledge = 6, risk = 4, wellbeing = 2, setFlags = listOf("investor")),
                result = "8.000 TL'yi düşük riskli bir para piyasası fonuna koydun. Getirisi mütevazı ama anaparan dalgalanmıyor."
            ),
            StoryChoice(
                id = "d1-plan",
                label = "Parayı olduğu gibi bırak, önce bütçe planı çıkar",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 3, wellbeing = 1, setFlags = listOf("budget_plan")),
                result = "Bir kâğıda 50/30/20 kuralını yazdın: %50 zorunlu gider, %30 yaşam, %20 birikim. Plan hazır, uygulaması sana kaldı."
            )
        )
    ),

    // ── GÜN 2 ─────────────────────────────────────────────────
    StoryEvent(
        day = 2,
        title = "Kira Günü",
        icon = "🏠",
        scene = listOf(
            "Ev sahibinin mesajı sabah 09.00'da geldi: \"Kirayı bugün bekliyorum.\"",
            "Kiran 11.000 TL. Hesabında para var ama ayın daha 28 günü var."
        ),
        lesson = "Kira gibi sabit giderler bütçenin ilk sırasına yazılır; keyif harcamaları kalandan yapılır.",
        choices = listOf(
            StoryChoice(
                id = "d2-cash",
                label = "Kirayı nakit öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -11_000, wellbeing = 2, risk = -2, landlord = 3),
                result = "Kirayı ödedin. Ay başında büyük gideri halletmek, kalan günler için rahat bir nefes aldırdı."
            ),
            StoryChoice(
                id = "d2-card",
                label = "Kartla öde, nakit elimde kalsın",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 11_000, risk = 8, knowledge = 1, landlord = -3),
                result = "Kirayı karta yazdın. Nakit elinde kaldı ama bu borç ay sonunda faizle geri dönecek."
            ),
            StoryChoice(
                id = "d2-roommate",
                label = "Kirayı öde ve ev arkadaşı aramaya başla",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -11_000, knowledge = 4, wellbeing = -3, landlord = 2, setFlags = listOf("roommate_search")),
                result = "Kirayı ödedin ve ilan sitelerine bir ilan bıraktın. Ev arkadaşı bulmak zaman alacak ama gideri yarıya indirebilir."
            )
        )
    ),

    // ── GÜN 3 ─────────────────────────────────────────────────
    StoryEvent(
        day = 3,
        title = "Faturalar",
        icon = "🧾",
        scene = listOf(
            "Elektrik 900 TL, doğalgaz 1.400 TL, internet 400 TL, su 300 TL, aidat 1.200 TL. Toplam 4.200 TL.",
            "Anneannen her zaman \"küçük damlalar göl yapar\" derdi. Bu ay ilk kez ne demek istediğini anlıyorsun."
        ),
        lesson = "Faturalar sabittir ama tüketim alışkanlığı değildir. Küçük ayarlar aylık bütçede gerçek fark yaratır.",
        choices = listOf(
            StoryChoice(
                id = "d3-all",
                label = "Hepsini zamanında öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -4_200, wellbeing = 2),
                result = "Tüm faturaları ödedin. Gecikme faizi yok, iletişim kesintisi yok; kafan rahat."
            ),
            StoryChoice(
                id = "d3-cut",
                label = "İnterneti düşür, kombiyi bir kademe kıs",
                hint = "2.600 TL öde",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -2_600, knowledge = 5, wellbeing = -3, setFlags = listOf("frugal")),
                result = "Daha düşük hızlı pakete geçtin ve termostatı kıstın. 1.600 TL cepte kaldı; akşamlar biraz daha serin."
            ),
            StoryChoice(
                id = "d3-card",
                label = "Faturaları karta yükle",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    debt = 4_200, risk = 5,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Fatura faizi",
                            days = 7,
                            message = "Karta yüklenen faturaların faizi hesabına eklendi: +250 TL",
                            debt = 250
                        )
                    )
                ),
                result = "Faturaları karta yükledin. Bu ay nakit rahatladı ama borç hanesine 4.200 TL daha eklendi."
            )
        )
    ),

    // ── GÜN 4 ─────────────────────────────────────────────────
    StoryEvent(
        day = 4,
        title = "Kredi Kartı Teklifi",
        icon = "💳",
        scene = listOf(
            "Bankanın uygulamasında bir bildirim: \"Sizin için 30.000 TL limitli kredi kartınız hazır.\"",
            "Altında küçük bir yazı: \"Aylık %4,25 faiz, gecikmede %5,50.\""
        ),
        lesson = "Kredi kartı bir ödeme aracıdır, gelir değildir. Limitin yüksek olması harcama gücünün yüksek olduğu anlamına gelmez.",
        choices = listOf(
            StoryChoice(
                id = "d4-no",
                label = "Teklifi reddet",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, risk = -5, wellbeing = -1),
                result = "Teklifi kapattın. Kart hayatını kolaylaştırabilir ama faiz oranı, taşıyamayacağın bir yükü de kolaylaştırır."
            ),
            StoryChoice(
                id = "d4-emergency",
                label = "Sadece acil durum için al, limiti kilitli tut",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 3, risk = 5, setFlags = listOf("has_card")),
                result = "Kartı aldın ve uygulamadan limitini kilitledin. Kartı cüzdanda değil, çekmecede tutacaksın."
            ),
            StoryChoice(
                id = "d4-use",
                label = "Al ve taksit imkânlarını kullan",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    risk = 12, wellbeing = 4, knowledge = 1,
                    setFlags = listOf("has_card", "card_user"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "İlk taksit",
                            days = 10,
                            message = "Taksitli alışverişin ilk ödemesi geldi: +900 TL borç",
                            debt = 900
                        )
                    )
                ),
                result = "Kartı aldın, ilk taksitli alışverişi de yaptın. \"Nasılsa 12 aya bölünüyor\" cümlesi bütçenin en pahalı cümlelerinden biri olabilir."
            )
        )
    ),

    // ── GÜN 5 ─────────────────────────────────────────────────
    StoryEvent(
        day = 5,
        title = "Market Günü",
        icon = "🛒",
        scene = listOf(
            "Buzdolabı boşalmış. Markete gidiyorsun ve raflar seni çağırıyor: indirim etiketleri, yeni çıkan ürünler, çikolata reyonu...",
            "Elinde bir liste var mı, yok mu? Buna sen karar vereceksin."
        ),
        lesson = "Listeyle gitmek, açken gitmemek ve birim fiyata bakmak market harcamasını en çok düşüren üç alışkanlıktır.",
        choices = listOf(
            StoryChoice(
                id = "d5-list",
                label = "Haftalık listeyle planlı alışveriş yap",
                hint = "1.800 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -1_800, knowledge = 3, wellbeing = 2),
                result = "Listeye sadık kaldın, birim fiyatları karşılaştırdın. 1.800 TL ile haftanın temel ihtiyaçlarını aldın."
            ),
            StoryChoice(
                id = "d5-impulse",
                label = "Canın ne isterse al",
                hint = "3.200 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -3_200, wellbeing = 5, risk = 2),
                result = "Sepet doldu, kart da. 3.200 TL gitti; yarısı ihtiyaç, yarısı anlık heves."
            ),
            StoryChoice(
                id = "d5-bulk",
                label = "İndirim gününü bekle, toplu al",
                hint = "1.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_500, knowledge = 4, wellbeing = -2),
                result = "İndirim gününü bekledin ve dayanıklı ürünleri toplu aldın. Buzdolabı dolu, ama iki gün daha idare etmen gerekti."
            ),
            StoryChoice(
                id = "d5-pantry",
                label = "Evde ne varsa onunla idare et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = -5, knowledge = 2),
                result = "Markete hiç gitmedin; dolaptaki artıklarla birkaç gün idare ettin. Para cebinde kaldı, keyfin biraz kaçtı."
            )
        )
    ),

    // ── GÜN 6 ─────────────────────────────────────────────────
    StoryEvent(
        day = 6,
        title = "Arkadaş Buluşması",
        icon = "🍽️",
        scene = listOf(
            "Grup sohbetinde herkes cumayı soruyor: \"Yeni mekân açılmış, gidelim mi?\"",
            "Menüye baktın: kişi başı 900 TL. Bu ay bütçende o kadar rahat bir yer yok."
        ),
        lesson = "Sosyal hayat bütçenin düşmanı değildir; planlanmamış sosyal harcama düşmandır. Alternatif sunmak hem ilişkiyi hem bütçeyi korur.",
        choices = listOf(
            StoryChoice(
                id = "d6-out",
                label = "Dışarıda yemek ye, hepsiyle buluş",
                hint = "900 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -900, wellbeing = 8, risk = 1, friend = 3),
                result = "Mekânda harika bir akşam geçirdin. 900 TL gitti ama moral depolandı; yalnız kalmak da bir maliyettir."
            ),
            StoryChoice(
                id = "d6-home",
                label = "Evde toplanmayı teklif et",
                hint = "250 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -250, wellbeing = 5, knowledge = 2, friend = 5),
                result = "Evde makarna ve film gecesi kurdun. Herkes hem eğlendi hem 650 TL cepte kaldı."
            ),
            StoryChoice(
                id = "d6-skip",
                label = "\"Bu ay bütçem yok\" de, gitme",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -6, knowledge = 4, friend = -2),
                result = "Buluşmaya gitmedin. Doğru karar olabilir ama kendini dışarıda bırakmış hissettin; bütçe kadar aidiyet de önemli."
            )
        )
    ),

    // ── GÜN 7 ─────────────────────────────────────────────────
    StoryEvent(
        day = 7,
        title = "Enflasyon Kapıda",
        icon = "📈",
        scene = listOf(
            "Haberlerde yıllık enflasyon %38,4 açıklandı. Market fişlerin geçen aya göre kabarmış.",
            "Paran bankada duruyor. Ama \"duran para\" gerçekten duruyor mu?"
        ),
        lesson = "Enflasyon, nakdin satın alma gücünü eritir. Birikimin en az enflasyon kadar getiri sağlaması gerekir; ama yüksek getiri vaadi riski de büyütür.",
        choices = listOf(
            StoryChoice(
                id = "d7-deposit",
                label = "Vadeli mevduata koy",
                hint = "10.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(invest = 10_000, knowledge = 5, risk = 1),
                result = "10.000 TL'yi vadeli hesaba koydun. Getirisi enflasyonun biraz altında kalsa da anaparan garanti."
            ),
            StoryChoice(
                id = "d7-gold",
                label = "Altına çevir",
                hint = "10.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 10_000, knowledge = 4, risk = 5, setFlags = listOf("investor", "gold")),
                result = "10.000 TL ile gram altın aldın. Altın enflasyona karşı klasik korunma aracıdır; yine de fiyatı dalgalanır."
            ),
            StoryChoice(
                id = "d7-spend",
                label = "Fiyatlar artmadan harcamayı öne çek",
                hint = "6.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -6_000, wellbeing = 6, risk = 5, knowledge = -2),
                result = "Beyaz eşya ve kıyafet alışverişini öne çektin. Enflasyonda \"şimdi al\" bazen mantıklıdır ama ihtiyaç değilse sadece harcamayı öne çekmiş olursun."
            ),
            StoryChoice(
                id = "d7-wait",
                label = "Hiçbir şey yapma, nakit tamponunu koru",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, risk = -2),
                result = "Yatırım yapmadın. Enflasyon birikimini eritiyor ama elinde nakit tamponu var; fırsat maliyeti de bir maliyettir."
            )
        )
    ),

    // ── GÜN 8 ─────────────────────────────────────────────────
    StoryEvent(
        day = 8,
        title = "Telefon Bozuldu",
        icon = "📱",
        scene = listOf(
            "Telefonun ekranı sabah aniden karardı. Tamirci \"ekran değişimi 2.500 TL\" dedi.",
            "Yeni model 18.000 TL. İkinci el temiz bir cihaz 7.000 TL."
        ),
        lesson = "Beklenmedik giderler için acil durum fonu tam olarak bunun içindir. Taksit, gideri yok etmez; gelecek aylara böler.",
        choices = listOf(
            StoryChoice(
                id = "d8-new",
                label = "Yeni telefonu taksitle al",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    debt = 18_000, wellbeing = 7, risk = 10,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Telefon taksiti",
                            days = 9,
                            message = "Telefonun aylık taksiti hesabına yansıdı: +1.500 TL borç",
                            debt = 1_500
                        )
                    )
                ),
                result = "Yeni telefon elinde. 18.000 TL borç 12 taksitle gelecek aylara yayıldı; aylık bütçende kalıcı bir delik açtı."
            ),
            StoryChoice(
                id = "d8-repair",
                label = "Tamir ettir",
                hint = "2.500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -2_500, knowledge = 3, wellbeing = 1),
                result = "Telefon tamir edildi. En ucuz çözüm her zaman kötü değildir; cihaz işini görüyor."
            ),
            StoryChoice(
                id = "d8-used",
                label = "İkinci el temiz bir cihaz al",
                hint = "7.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -7_000, knowledge = 2, wellbeing = 3),
                result = "İkinci el cihazı kontrol ettirip aldın. Yeni modelin havası yok ama fiyatının üçte birine aynı işi görüyor."
            )
        )
    ),

    // ── GÜN 9 ─────────────────────────────────────────────────
    StoryEvent(
        day = 9,
        title = "Yatırım Dünyası",
        icon = "📊",
        scene = listOf(
            "Bir arkadaşın \"paramı altına koydum\" diyor, bir diğeri \"fon aldım\". Banka uygulaman da sana yatırım ürünlerini gösteriyor.",
            "Ama hangisi sana uygun? Risk, getiri ve vade... Bu üçünü bilmeden yatırım yapmak, gözü kapalı yola çıkmak gibidir."
        ),
        lesson = "Yatırımda ilk adım ürün seçmek değil, hedef ve vade belirlemektir. Kısa vadeli ihtiyaç parası riskli varlıklara konmaz.",
        choices = listOf(
            StoryChoice(
                id = "d9-gold",
                label = "Altın al",
                hint = "5.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 5_000, knowledge = 4, risk = 4, setFlags = listOf("investor", "gold")),
                result = "5.000 TL'lik altın aldın. Enflasyona karşı koruma sağlar; kısa vadede fiyatı düşebilir."
            ),
            StoryChoice(
                id = "d9-fund",
                label = "Endeks fonu al",
                hint = "5.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 5_000, knowledge = 5, risk = 7, setFlags = listOf("investor", "stocks")),
                result = "5.000 TL ile endeks fonu aldın. Tek bir şirkete değil, sepete yatırım yaptın; yine de piyasa dalgalanır."
            ),
            StoryChoice(
                id = "d9-emergency",
                label = "Önce acil durum fonumu tamamla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, risk = -5, setFlags = listOf("emergency")),
                result = "Yatırımı erteleyip 3 aylık giderini karşılayacak bir acil fon hedefi koydun. Bu fon, kriz anında borca düşmeni engeller."
            )
        )
    ),

    // ── GÜN 10 ────────────────────────────────────────────────
    StoryEvent(
        day = 10,
        title = "Kripto Daveti",
        icon = "🪙",
        scene = listOf(
            "Bir arkadaşın telefonunu uzatıyor: \"Bak, iki haftada %80 yaptı. Sen de girsen keşke.\"",
            "Grafik yeşil, yorumlar coşkulu. Ama senin bildiğin tek şey, bu varlığın bir haftada %40 da düşebildiği."
        ),
        lesson = "Yüksek getiri vaadi her zaman yüksek risk taşır. Anlamadığın varlığa, kaybetmeyi göze alamayacağın parayla girme.",
        choices = listOf(
            StoryChoice(
                id = "d10-in",
                label = "5.000 TL ile gir",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(invest = 5_000, risk = 14, wellbeing = 3, knowledge = 2, setFlags = listOf("crypto", "investor")),
                result = "5.000 TL'lik kripto aldın. İlk gün hesabın %6 arttı; bu heyecan, riski olduğundan küçük gösterebilir."
            ),
            StoryChoice(
                id = "d10-watch",
                label = "Sadece izle ve araştır",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 5, risk = -2),
                result = "Girmedin ama takip listesine ekledin. Bir hafta sonra fiyatın %30 düştüğünü görünce iyi ki bekledim dedin."
            ),
            StoryChoice(
                id = "d10-no",
                label = "\"Bu bir kumar\" de, uzak dur",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 3, risk = -4),
                result = "Kripto senin için değil, dedin. Bu bir kayıp değil; anlamadığın şeyden uzak durmak da bir stratejidir."
            )
        )
    ),

    // ── GÜN 11 ────────────────────────────────────────────────
    StoryEvent(
        day = 11,
        title = "Fazla Mesai mi, Kurs mu?",
        icon = "⏰",
        scene = listOf(
            "Yöneticin \"bu hafta fazla mesai var, 4.500 TL ek ödeme\" diyor.",
            "Aynı akşamlarda başlayan bir finans kursu var; ücreti 1.500 TL. İkisini aynı anda yapamazsın."
        ),
        lesson = "Gelir artırmak kadar, gelirini artıracak beceriye yatırım yapmak da önemlidir. Kısa vadeli kazanç ile uzun vadeli kapasite arasında denge kur.",
        choices = listOf(
            StoryChoice(
                id = "d11-overtime",
                label = "Fazla mesaiyi al",
                hint = "+4.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 4_500, wellbeing = -5, knowledge = 2, boss = 5),
                result = "Hafta boyunca geç saate kadar çalıştın. 4.500 TL hesabına geçti ama uyku ve sosyal hayat faturayı ödedi."
            ),
            StoryChoice(
                id = "d11-course",
                label = "Akşam kursuna yazıl",
                hint = "-1.500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(
                    cash = -1_500, knowledge = 8, wellbeing = -2,
                    setFlags = listOf("course"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Kurs tamamlandı",
                            days = 5,
                            message = "Akşam kursunu bitirdin: bütçe ve yatırım bilgin arttı.",
                            knowledge = 3
                        )
                    )
                ),
                result = "Kursa başladın. Bu ay para kazanmadın ama bütçe, yatırım ve vergi konularında ciddi bir temel attın."
            ),
            StoryChoice(
                id = "d11-rest",
                label = "İkisini de reddet, dinlen",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 5, knowledge = -1, boss = -3),
                result = "Bu hafta kendine zaman ayırdın. Tükenmiş bir zihin ne para yönetir ne de öğrenir."
            )
        )
    ),

    // ── GÜN 12 ────────────────────────────────────────────────
    StoryEvent(
        day = 12,
        title = "Şüpheli Telefon",
        icon = "📞",
        scene = listOf(
            "\"Merhaba, bankanızdan arıyoruz. Hesabınızda şüpheli bir işlem tespit ettik. Güvenlik için kart bilgilerinizi doğrulamamız gerekiyor.\"",
            "Ses kaydı resmi, numara tanıdık görünüyor. Ama içindeki ses seni acele ettiriyor."
        ),
        lesson = "Bankalar telefonla kart bilgisi, şifre veya SMS kodu istemez. Şüphelendiğinde telefonu kapat ve bankayı resmi numarasından kendin ara.",
        choices = listOf(
            StoryChoice(
                id = "d12-safe",
                label = "Bilgi verme, telefonu kapat ve bankayı kendin ara",
                hint = "Doğru hamle",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 8, risk = -6, wellbeing = 2, setFlags = listOf("scam_safe")),
                result = "Telefonu kapattın ve kartın arkasındaki numarayı aradın. Banka böyle bir arama yapmadığını söyledi; dolandırıcılık girişimini atlattın."
            ),
            StoryChoice(
                id = "d12-give",
                label = "Kart bilgilerini doğrula",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 15_000, risk = 20, wellbeing = -10, setFlags = listOf("scammed")),
                result = "Bilgileri verdikten 20 dakika sonra telefonuna 15.000 TL'lik harcama bildirimi düştü. Parayı geri almak için bankayla haftalarca uğraşacaksın."
            ),
            StoryChoice(
                id = "d12-link",
                label = "Mesajdaki linke tıkla, uygulamayı indir",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -12_000, risk = 18, wellbeing = -8, setFlags = listOf("scammed")),
                result = "İndirdiğin uygulama hesabına erişti ve 12.000 TL başka bir hesaba aktarıldı. Meğer o link hiç bankaya ait değildi."
            )
        )
    ),

    // ── GÜN 13 ────────────────────────────────────────────────
    StoryEvent(
        day = 13,
        title = "Kira Artışı",
        icon = "📨",
        scene = listOf(
            "Ev sahibin aradı: \"Enflasyon var, kirayı gelecek ay 15.950 TL yapıyorum.\"",
            "Sözleşmende artış oranı yazıyor ama pazarlık kapısı da tamamen kapanmış değil."
        ),
        lesson = "Kira artışında yasal sınırları ve sözleşmeyi bilmek pazarlık gücü verir. Taşınmanın depozito ve taşınma maliyeti de hesaba katılmalı.",
        choices = listOf(
            StoryChoice(
                id = "d13-accept",
                label = "Kabul et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -3, landlord = 2, setFlags = listOf("rent_high")),
                result = "Artışı kabul ettin. Gelecek ay kira 15.950 TL olacak; bu, bütçendeki en büyük kalemi daha da büyütüyor."
            ),
            StoryChoice(
                id = "d13-negotiate",
                label = "Pazarlık yap, %25'te anlaş",
                hint = "Bilgi 45+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 45, note = "Bilgi 45+"),
                effect = ChoiceEffect(knowledge = 5, wellbeing = -1, landlord = 4, setFlags = listOf("rent_mid")),
                result = "Sözleşmedeki artış sınırını ve emsal kiraları gösterdin. Ev sahibi %25'te anlaştı; gelecek ay kira 13.800 TL."
            ),
            StoryChoice(
                id = "d13-move",
                label = "Taşınmayı araştır",
                hint = "-2.000 TL masraf",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -2_000, knowledge = 3, wellbeing = -4, landlord = -8, setFlags = listOf("moved")),
                result = "Yeni ev baktın, depozito ve taşınma için 2.000 TL harcadın. Daha uygun bir ev buldun; gelecek ay kira 10.500 TL olacak."
            )
        )
    ),

    // ── GÜN 14 ────────────────────────────────────────────────
    StoryEvent(
        day = 14,
        title = "Diş Ağrısı",
        icon = "🦷",
        scene = listOf(
            "Gece yarısı diş ağrısıyla uyandın. Sabahı zor ettin.",
            "Özel klinik 4.000 TL istiyor. Devlet hastanesinde sıra var ama birkaç gün bekleyebilir."
        ),
        lesson = "Sağlık harcaması ertelenebilir bir gider değildir. Küçük bir sorun büyüyünce hem sağlık hem bütçe maliyeti artar.",
        choices = listOf(
            StoryChoice(
                id = "d14-private",
                label = "Özel kliniğe git",
                hint = "-4.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -4_000, wellbeing = 8, knowledge = 1),
                result = "Aynı gün tedavi oldun. 4.000 TL gitti ama ağrı bitti ve sorun büyümeden çözüldü."
            ),
            StoryChoice(
                id = "d14-state",
                label = "Devlet hastanesine git, sıra bekle",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 4, knowledge = 2),
                result = "Birkaç gün ağrıyla bekledin ama tedavi oldun. Cebinden para çıkmadı, sabrından çıktı."
            ),
            StoryChoice(
                id = "d14-delay",
                label = "Ağrı kesiciyle idare et",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(wellbeing = -8, risk = 4),
                result = "Ağrıyı erteledin. Bir hafta sonra iltihap büyüdü ve tedavi maliyeti iki katına çıktı."
            )
        )
    ),

    // ── GÜN 15 ────────────────────────────────────────────────
    StoryEvent(
        day = 15,
        title = "Kuzenin Borç İsteği",
        icon = "🤝",
        scene = listOf(
            "Kuzenin aradı: \"Acil 5.000 TL lazım, gelecek ay kesin veririm.\"",
            "Sevdiğin biri. Ama geçen sefer de \"gelecek ay\" demişti."
        ),
        lesson = "Yakınlara borç verirken geri gelmeme ihtimalini hesaba kat. Vereceğin tutar, ilişkiyi bozmayacak kadar olmalı.",
        choices = listOf(
            StoryChoice(
                id = "d15-give",
                label = "5.000 TL ver",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(
                    cash = -5_000, wellbeing = 6, risk = 5,
                    setFlags = listOf("family_helped"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Kuzenin ödemesi",
                            days = 8,
                            message = "Kuzenin borcunun bir kısmını ödedi: +500 TL",
                            cash = 500,
                            friend = 2
                        )
                    )
                ),
                result = "Parayı verdin. Kuzenin rahatladı; senin bütçende ise 5.000 TL'lik bir boşluk oluştu."
            ),
            StoryChoice(
                id = "d15-partial",
                label = "1.000 TL ver, durumunu açıkla",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_000, wellbeing = 2, knowledge = 4),
                result = "Verebileceğin kadarını verdin ve kendi bütçeni de dürüstçe anlattın. İlişki de bütçe de korundu."
            ),
            StoryChoice(
                id = "d15-no",
                label = "Reddet, bütçe planı öner",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, wellbeing = -3),
                result = "Para vermedin ama ona bir bütçe planı çıkardın. Belki de istediği para değil, bir çıkış yoluydu."
            )
        )
    ),

    // ── GÜN 16 (borsa varsa) ──────────────────────────────────
    StoryEvent(
        day = 16,
        title = "Borsa Sarsıntısı",
        icon = "📉",
        scene = listOf(
            "Aldığın endeks fonu bir haftada %12 düştü. Telefonundaki kırmızı grafik içini sıkıştırıyor.",
            "Sosyal medyada herkes \"çıkın\" diyor. Ama senin vaden neydi?"
        ),
        condition = { has(it, "stocks") },
        lesson = "Piyasa düşüşleri yatırımın doğal parçasıdır. Panik satışı, zararı gerçeğe dönüştürür; plana sadık kalmak ise disiplin işidir.",
        choices = listOf(
            StoryChoice(
                id = "d16-panic",
                label = "Panikle sat",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(divest = 5_000, wellbeing = -6, risk = -4, knowledge = 2),
                result = "Fonu sattın ve zararı kilitledin. Rahatladın ama düşüşten sonraki toparlanmayı da kaçırdın."
            ),
            StoryChoice(
                id = "d16-hold",
                label = "Dokunma, plana sadık kal",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, wellbeing = -2, risk = 1, setFlags = listOf("held")),
                result = "Hiçbir şey yapmadın. Bir hafta sonra fon %8 toparlandı; panik yapmamak bu kez kazandırdı."
            ),
            StoryChoice(
                id = "d16-buy",
                label = "Düşüşte ek alım yap",
                hint = "-2.500 TL",
                tone = ChoiceTone.Balanced,
                requires = ChoiceRequirement(minCash = 2_500, note = "Nakit 2.500+"),
                effect = ChoiceEffect(invest = 2_500, knowledge = 4, risk = 8),
                result = "Düşüşü fırsat gördün ve 2.500 TL daha ekledin. Maliyetin düştü ama riskin de arttı."
            )
        )
    ),

    // ── GÜN 16 (borsa yoksa) ──────────────────────────────────
    StoryEvent(
        day = 16,
        title = "Herkes Kazanıyor mu?",
        icon = "📈",
        scene = listOf(
            "Sosyal medyada herkes borsada kazandığını gösteriyor: yeşil grafikler, \"kolay para\" başlıkları.",
            "Senin hesabında ise sadece maaş ve dikkatli bir bütçe var. Bir an \"geç kaldım\" hissi geliyor."
        ),
        condition = { !has(it, "stocks") },
        lesson = "Sosyal medyada gösterilen kazançlar seçici bir örnektir; kaybedenler paylaşmaz. Yatırım kararı, başkalarının ekran görüntüsüyle değil kendi planınla verilir.",
        choices = listOf(
            StoryChoice(
                id = "d16-fomo-in",
                label = "Hemen gir, trendi kaçırma",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(invest = 6_000, risk = 12, wellbeing = 2, setFlags = listOf("investor", "stocks")),
                result = "6.000 TL ile girdin. İlk hafta kazandın, ikinci hafta düştü; trende yetişmek trendden kazanmak değil."
            ),
            StoryChoice(
                id = "d16-fomo-small",
                label = "Araştır, küçük bir miktarla başla",
                hint = "-2.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 2_000, knowledge = 5, risk = 4, setFlags = listOf("investor", "stocks")),
                result = "Önce fon türlerini okudun, sonra 2.000 TL ile küçük bir başlangıç yaptın. Kaybetsen bile bütçen sarsılmaz."
            ),
            StoryChoice(
                id = "d16-fomo-no",
                label = "Uzak dur, bildiğin yerde kal",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, risk = -3),
                result = "Trende katılmadın. Bu sefer kaçırdığın bir kazanç olmadı; sadece gereksiz bir risk almadın."
            )
        )
    ),

    // ── GÜN 17 ────────────────────────────────────────────────
    StoryEvent(
        day = 17,
        title = "Kolay Para Tuzağı",
        icon = "🎰",
        scene = listOf(
            "Bir reklam: \"Kredi çek, yatırıma koy, faizini yatırım kazancıyla öde.\"",
            "Matematik kulağa çok mantıklı geliyor. Peki ya yatırım kazanmazsa?"
        ),
        lesson = "Borçla yatırım yapmak kaldıraçtır: kazancı da zararı da büyütür. Kredi faizi garanti giderdir, yatırım getirisi ise garanti değildir.",
        choices = listOf(
            StoryChoice(
                id = "d17-leverage",
                label = "10.000 TL kredi çekip yatır",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    cash = 10_000, debt = 10_000, invest = 10_000, risk = 20, wellbeing = 2,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Kredi taksiti",
                            days = 10,
                            message = "Kaldıraçlı yatırımın kredi taksiti geldi: +1.200 TL borç",
                            debt = 1_200
                        )
                    )
                ),
                result = "10.000 TL kredi çekip yatırıma yönlendirdin. Aylık kredi taksidi artık bütçende sabit bir gider."
            ),
            StoryChoice(
                id = "d17-ignore",
                label = "Bütçeni koru, sadece izle",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, wellbeing = -2),
                result = "Reklamı kapattın. Kaldıraçlı yatırımın nasıl çalıştığını okudun ve \"bana göre değil\" dedin."
            ),
            StoryChoice(
                id = "d17-small",
                label = "Kendi paranla küçük bir miktar dene",
                hint = "-1.500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 1_500, risk = 5, knowledge = 2),
                result = "Borçsuz, kendi paranla 1.500 TL yatırdın. Kaybetsen de borç faizi ödemeyeceksin."
            )
        )
    ),

    // ── GÜN 18 ────────────────────────────────────────────────
    StoryEvent(
        day = 18,
        title = "Ek İş Teklifi",
        icon = "💻",
        scene = listOf(
            "Eski stajyer arkadaşın bir freelance iş teklif ediyor: 6.000 TL, iki hafta sonunda teslim.",
            "\"Fatura mı? Kim uğraşacak canım\" diyor. Sen ise vergi konusunu merak ediyorsun."
        ),
        lesson = "Kayıt dışı gelir kısa vadede cazip görünse de ileride ceza, faiz ve kredi notu sorunlarına dönüşebilir. Vergi, gelirin bir parçasıdır.",
        choices = listOf(
            StoryChoice(
                id = "d18-tax",
                label = "İşi al, fatura kes ve vergini öde",
                hint = "+5.000 TL net",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 5_000, knowledge = 6, wellbeing = -2, setFlags = listOf("side_income")),
                result = "İşi tamamladın, fatura kestin ve vergini ayırdın. Elinde 5.000 TL net kaldı; gönlün de rahat."
            ),
            StoryChoice(
                id = "d18-offbook",
                label = "İşi al, vergi verme",
                hint = "+6.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    cash = 6_000, risk = 12, knowledge = 1,
                    setFlags = listOf("tax_risk"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Vergi cezası",
                            days = 9,
                            message = "Beyan etmediğin ek gelir tespit edildi: +2.500 TL ceza",
                            debt = 2_500,
                            setFlags = listOf("tax_penalty")
                        )
                    )
                ),
                result = "6.000 TL'yi aldın. Kısa vadede 1.000 TL kârdasın ama ileride vergi cezası ve faiziyle karşılaşma riskin var."
            ),
            StoryChoice(
                id = "d18-no",
                label = "Yorgunum, reddet",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 4),
                result = "Teklifi reddettin. Bu ay ekstra gelir yok ama enerjini korudun."
            )
        )
    ),

    // ── GÜN 19 ────────────────────────────────────────────────
    StoryEvent(
        day = 19,
        title = "Terfi Teklifi",
        icon = "🪜",
        scene = listOf(
            "Yöneticin seni odasına çağırdı: \"İşini beğeniyoruz. Daha fazla sorumluluk ve 6.000 TL zam teklif ediyoruz.\"",
            "Ama aynı zamanda bir eğitim bütçesi de isteyebilirsin; şirket bunu duymaya açık görünüyor."
        ),
        lesson = "Kariyer kararları yalnızca maaşla ölçülmez. Öğrenme, esneklik ve iş-yaşam dengesi de toplam getirinin parçasıdır.",
        choices = listOf(
            StoryChoice(
                id = "d19-promotion",
                label = "Terfiyi kabul et",
                hint = "+6.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = 6_000, wellbeing = 5, knowledge = 3, boss = 5, setFlags = listOf("promoted")),
                result = "Terfiyi kabul ettin. Bu ay ek 6.000 TL geldi ama sorumluluk ve stres de arttı."
            ),
            StoryChoice(
                id = "d19-education",
                label = "Maaş yerine eğitim bütçesi iste",
                hint = "+1.500 TL + eğitim",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(
                    cash = 1_500, knowledge = 10, boss = 3,
                    setFlags = listOf("promoted_edu"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Eğitim bütçesi",
                            days = 6,
                            message = "Şirket eğitim bütçesini onayladı: bilgi puanın arttı.",
                            knowledge = 4
                        )
                    )
                ),
                result = "Şirket eğitim bütçesini onayladı ve küçük bir iyileştirme yaptı. Uzun vadede bilgi, en yüksek getirili yatırımlardan biri."
            ),
            StoryChoice(
                id = "d19-decline",
                label = "Reddet, iş-yaşam dengesini koru",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 5, knowledge = -2, boss = -5),
                result = "Teklifi reddettin. Kariyer basamağı bekleyecek ama bu ay kendine ve hayatına yer açtın."
            )
        )
    ),

    // ── GÜN 20 (taşındıysa) ───────────────────────────────────
    StoryEvent(
        day = 20,
        title = "Taşınma Faturası",
        icon = "📦",
        scene = listOf(
            "Yeni evin anahtarlarını aldın. Ama taşınma sadece kiradan ibaret değil: nakliye, depozito farkı ve eksik eşyalar derken 4.000 TL'lik ek bir fatura çıktı.",
            "Eski evden çıkarken birkaç hasar için de para isteniyor."
        ),
        condition = { has(it, "moved") },
        lesson = "Taşınmanın gerçek maliyeti yalnızca kira değildir: depozito, nakliye, aidat ve düzen kurma giderleri de hesaba katılmalı.",
        choices = listOf(
            StoryChoice(
                id = "d20-move-cash",
                label = "Taşınma faturasını nakit öde",
                hint = "-4.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -4_000, wellbeing = 1),
                result = "Taşınma masraflarını nakit kapattın. Yeni evin düzeni kuruldu, bütçede iz kaldı ama borç yok."
            ),
            StoryChoice(
                id = "d20-move-card",
                label = "Karta yükle, nakit bende kalsın",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 4_000, risk = 5),
                result = "Taşınma masraflarını karta yükledin. Bu ay nakit rahat, gelecek ay borç var."
            ),
            StoryChoice(
                id = "d20-move-cheap",
                label = "İkinci el eşyayla idare et",
                hint = "-1.200 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_200, wellbeing = -4, knowledge = 3),
                result = "Eksik eşyaları ikinci el aldın. Ev biraz ''öğrenci evi'' gibi görünüyor ama 2.800 TL cepte kaldı."
            )
        )
    ),

    // ── GÜN 20 (pazarlık) ─────────────────────────────────────
    StoryEvent(
        day = 20,
        title = "Kira Farkı",
        icon = "🏠",
        scene = listOf(
            "Pazarlıkla kira 13.800 TL oldu. Gelecek ay bütçende 2.800 TL'lik ek bir boşluk var.",
            "Bu farkı şimdiden planlamak, gelecek ayı borçsuz geçirmenin en kolay yolu."
        ),
        condition = { has(it, "rent_mid") },
        lesson = "Kira artışı geldiğinde farkı önceden bütçeye yerleştirmek, kart borcuna düşmeyi engeller.",
        choices = listOf(
            StoryChoice(
                id = "d20-mid-cash",
                label = "Farkı şimdiden birikime ayır",
                hint = "-2.800 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(invest = 2_800, knowledge = 5, wellbeing = 1),
                result = "Kira farkını şimdiden ayırdın. Gelecek ay zammı ödemek seni sarsmayacak."
            ),
            StoryChoice(
                id = "d20-mid-cut",
                label = "Harcamaları kıs, farkı bütçeden çıkar",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 4, wellbeing = -4),
                result = "Abonelikleri ve dışarıda yemeyi kıstın. Biraz can sıkıcı ama kira farkı bütçeye sığdı."
            ),
            StoryChoice(
                id = "d20-mid-card",
                label = "Şimdilik kartla köprüle",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 2_800, risk = 6),
                result = "Farkı karta yükledin. Sorun çözülmedi, sadece bir ay ertelendi ve faiz eklendi."
            )
        )
    ),

    // ── GÜN 20 (artış kabul) ──────────────────────────────────
    StoryEvent(
        day = 20,
        title = "Kira Farkı",
        icon = "📨",
        scene = listOf(
            "Yeni kira 15.950 TL. Gelecek ay bütçende 4.950 TL'lik bir boşluk var.",
            "Bunu şimdiden planlamazsan kart borcu kaçınılmaz görünüyor."
        ),
        condition = { !has(it, "moved") && !has(it, "rent_mid") },
        lesson = "Kira bütçenin en büyük kalemiyse, diğer kalemleri buna göre planlamak gerekir. Gelirin %35'inden fazlası kiraya gidiyorsa risk büyüyor.",
        choices = listOf(
            StoryChoice(
                id = "d20-high-cash",
                label = "Farkı şimdiden birikime ayır",
                hint = "-4.950 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(invest = 4_950, knowledge = 5, wellbeing = 1),
                result = "Kira farkını şimdiden kenara koydun. Gelecek ayın en büyük riskini bugünden kapattın."
            ),
            StoryChoice(
                id = "d20-high-cut",
                label = "Harcamaları kıs, farkı bütçeden çıkar",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(knowledge = 4, wellbeing = -4),
                result = "Abonelikleri iptal ettin, dışarıda yemeyi azalttın. Sıkı bir ay ama kira farkı bütçeye oturdu."
            ),
            StoryChoice(
                id = "d20-high-card",
                label = "Kartla köprüle",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 4_950, risk = 7),
                result = "Farkı karta yükledin. Kira sorunu çözülmedi; faiziyle birlikte gelecek aylara taşındı."
            )
        )
    ),

    // ── GÜN 21 ────────────────────────────────────────────────
    StoryEvent(
        day = 21,
        title = "Kredi Teklifi",
        icon = "🏦",
        scene = listOf(
            "Bankadan mesaj: \"Size özel 20.000 TL ihtiyaç kredisi, 12 ay vadeli, aylık %4,5 faiz.\"",
            "Kredi bir araç. Ama ne için kullanacağın, aracı faydaya mı yükse dönüştüreceğine karar veriyor."
        ),
        lesson = "Kredi yalnızca getirisi faizinden yüksek ve riski yönetilebilir bir amaç için mantıklıdır. Tüketim kredisi, gelecekteki geliri bugünden harcamaktır.",
        choices = listOf(
            StoryChoice(
                id = "d21-invest",
                label = "Krediyi çek, yatırıma yönlendir",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    cash = 20_000, debt = 23_000, invest = 20_000, risk = 18, knowledge = 3,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Kredi taksiti",
                            days = 10,
                            message = "İhtiyaç kredisinin ilk taksiti geldi: +1.800 TL borç",
                            debt = 1_800
                        )
                    )
                ),
                result = "Krediyi çekip yatırıma koydun. 23.000 TL geri ödeme yükü sabit; yatırım getirisi ise hiç garanti değil."
            ),
            StoryChoice(
                id = "d21-no",
                label = "Reddet, borcunu maaşla öde",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 5, risk = -5),
                result = "Krediyi reddettin. Faiz ödemeden, kendi hızında borç kapatmak daha yavaş ama daha güvenli."
            ),
            StoryChoice(
                id = "d21-limit",
                label = "Kredi yerine kart limitini artır",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(risk = 8, wellbeing = 2),
                result = "Kart limitin arttı. Kullanmadığın sürece sorun yok; ama erişilebilir limit, harcama ihtimalini de artırır."
            )
        )
    ),

    // ── GÜN 22 ────────────────────────────────────────────────
    StoryEvent(
        day = 22,
        title = "Bayram Hazırlığı",
        icon = "🎁",
        scene = listOf(
            "Bayram yaklaşıyor. Aile ziyaretleri, hediyeler, ikramlar...",
            "Herkesin gönlünü almak istiyorsun ama bütçe sınırlı."
        ),
        lesson = "Hediye ve bayram harcamaları planlanmazsa bütçeyi sessizce aşar. Anlamlı bir hediye, pahalı olandan daha değerli olabilir.",
        choices = listOf(
            StoryChoice(
                id = "d22-gift",
                label = "Aileye hediye al",
                hint = "-3.000 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -3_000, wellbeing = 8),
                result = "Herkes için düşünülmüş hediyeler aldın. 3.000 TL gitti ama bayram sofrasındaki mutluluk paha biçilemez."
            ),
            StoryChoice(
                id = "d22-handmade",
                label = "El emeği hediye hazırla",
                hint = "-500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -500, wellbeing = 5, knowledge = 3),
                result = "Kendi hazırladığın hediyelerle gittin. Maliyeti düşük, değeri yüksek bir bayram oldu."
            ),
            StoryChoice(
                id = "d22-visit",
                label = "Hediye alma, sadece ziyaret et",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -4, knowledge = 2),
                result = "Hediye almadın ama ziyaret ettin. Bazı akrabalar yüz ifadesiyle bunu belli etti; bütçen ise rahatladı."
            )
        )
    ),

    // ── GÜN 23 ────────────────────────────────────────────────
    StoryEvent(
        day = 23,
        title = "Sigorta Konuşması",
        icon = "🛡️",
        scene = listOf(
            "Bir arkadaşın deprem sigortası yaptırdığını söylüyor. Diğeri \"boşa para\" diyor.",
            "Sigorta, kötü ihtimalin maliyetini küçük ve düzenli bir ödemeye çevirir."
        ),
        lesson = "Sigorta bir yatırım değil, korumadır. Beklenmedik büyük kayıpları karşılamak için küçük bir prim ödersin.",
        choices = listOf(
            StoryChoice(
                id = "d23-full",
                label = "Konut ve kasko sigortası yaptır",
                hint = "-2.500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -2_500, risk = -10, knowledge = 5, setFlags = listOf("insured")),
                result = "Konut ve kasko sigortanı yaptırdın. 2.500 TL ödedin; büyük bir hasar durumunda bu primin karşılığı çok farklı olacak."
            ),
            StoryChoice(
                id = "d23-min",
                label = "Sadece zorunlu sigortayı yaptır",
                hint = "-1.200 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -1_200, risk = -3),
                result = "Zorunlu sigortayı yaptırdın. Temel koruma var ama konut hasarını kapsamıyor."
            ),
            StoryChoice(
                id = "d23-skip",
                label = "Ertele, \"bana bir şey olmaz\"",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    risk = 8, knowledge = -3, wellbeing = 2,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Beklenmedik hasar",
                            days = 8,
                            message = "Sigortasız olduğun için küçük bir hasarı kendin ödedin: +1.500 TL borç",
                            debt = 1_500
                        )
                    )
                ),
                result = "Sigortayı erteledin. Bu ay 2.500 TL cebinde kaldı; ama olası bir hasarın maliyeti bunun kat kat üstünde."
            )
        )
    ),

    // ── GÜN 24 ────────────────────────────────────────────────
    StoryEvent(
        day = 24,
        title = "İkinci Gelir",
        icon = "🛵",
        scene = listOf(
            "Hafta sonun boş. Bir yandan dinlenmek istiyorsun, bir yandan da ek gelir cazip.",
            "Kurye işi 5.000 TL getiriyor. Online ders vermek 4.000 TL ama daha az yorucu."
        ),
        lesson = "Ek gelir, bütçeyi rahatlatır ama tükenmişlik yaratırsa sağlık maliyeti olarak geri döner. Sürdürülebilir olanı seç.",
        choices = listOf(
            StoryChoice(
                id = "d24-courier",
                label = "Hafta sonu kurye olarak çalış",
                hint = "+5.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = 5_000, wellbeing = -7, knowledge = 2),
                result = "Hafta sonu boyunca trafikteydin. 5.000 TL kazandın ama pazartesi gününe yorgun başladın."
            ),
            StoryChoice(
                id = "d24-tutor",
                label = "Online ders ver",
                hint = "+4.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(
                    cash = 4_000, wellbeing = -3, knowledge = 5,
                    setFlags = listOf("side_income"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Ek ders ödemesi",
                            days = 5,
                            message = "İkinci ders grubunun ödemesi geldi: +1.000 TL",
                            cash = 1_000
                        )
                    )
                ),
                result = "Haftada birkaç saat ders verdin. 4.000 TL kazandın, hem de bildiğin konuları pekiştirdin."
            ),
            StoryChoice(
                id = "d24-rest",
                label = "Dinlen, enerjini topla",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = 5, knowledge = 1),
                result = "Bu hafta sonu ek gelir yoktu. Ama dinlenmiş bir hafta, daha az hata ve daha iyi kararlar demek."
            )
        )
    ),

    // ── GÜN 25 ────────────────────────────────────────────────
    StoryEvent(
        day = 25,
        title = "Zam Geldi",
        icon = "🏷️",
        scene = listOf(
            "Markette etiketler değişmiş: temel gıdada %15 zam var.",
            "Kasanın önünde durup hesap yapıyorsun. Aynı para, artık daha az ürün alıyor."
        ),
        lesson = "Enflasyonla mücadelede panik alışverişi çözüm değildir. Planlı stok, ucuz alternatif ve israfı azaltmak gerçek tasarruf sağlar.",
        choices = listOf(
            StoryChoice(
                id = "d25-stock",
                label = "Dayanıklı ürünlerden stok yap",
                hint = "-3.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -3_000, knowledge = 2, wellbeing = 3),
                result = "Bakliyat, deterjan ve konserve stokladın. Peşin 3.000 TL ödedin ama gelecek zamlardan korundun."
            ),
            StoryChoice(
                id = "d25-cheap",
                label = "Ucuz markete ve yerel üreticiye geç",
                hint = "-1.500 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -1_500, knowledge = 5, wellbeing = -2),
                result = "Semt pazarına ve indirim marketine yöneldin. Biraz zaman kaybettin ama aynı ürünleri daha ucuza aldın."
            ),
            StoryChoice(
                id = "d25-nothing",
                label = "Hiçbir şey yapma, aynı yerden al",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(risk = 4, wellbeing = -3),
                result = "Alışkanlığını değiştirmedin. Enflasyon sessizce bütçenden payını aldı."
            )
        )
    ),

    // ── GÜN 26 ────────────────────────────────────────────────
    StoryEvent(
        day = 26,
        title = "Düğün Daveti",
        icon = "💍",
        scene = listOf(
            "Yakın bir arkadaşın düğünü var. Takı, kıyafet, yol...",
            "Davetiye elinde; bütçen ise \"bir düşünelim\" diyor."
        ),
        lesson = "Sosyal zorunluluklar bütçeyi en çok zorlayan gizli kalemlerdir. Nazik bir sınır koymak, borca girmekten daha iyidir.",
        choices = listOf(
            StoryChoice(
                id = "d26-full",
                label = "2.000 TL takı tak",
                hint = "-2.000 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -2_000, wellbeing = 3, risk = 2, friend = 3),
                result = "Düğüne gittin ve 2.000 TL taktın. Arkadaşın çok mutlu oldu; bütçen ise bir süre sıkı geçecek."
            ),
            StoryChoice(
                id = "d26-modest",
                label = "500 TL ile katıl",
                hint = "-500 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(cash = -500, wellbeing = 1, knowledge = 1, friend = 4),
                result = "Düğüne gittin, gönlünden kopanı taktın. Az ama samimi bir katılım oldu."
            ),
            StoryChoice(
                id = "d26-skip",
                label = "Gitme, video mesaj gönder",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(wellbeing = -5, knowledge = 2, friend = -4),
                result = "Düğüne gitmedin, içten bir video mesaj gönderdin. Arkadaşın anlayış gösterdi; sen yine de biraz mahcup oldun."
            )
        )
    ),

    // ── GÜN 27 ────────────────────────────────────────────────
    StoryEvent(
        day = 27,
        title = "Garantili Getiri",
        icon = "🚩",
        scene = listOf(
            "Bir tanıdık, \"garantili aylık %30 getiri\" sunan bir yatırım grubuna davet ediyor.",
            "Grup fotoğrafları lüks arabalarla dolu. Sözleşme yok, garanti sadece sözlü."
        ),
        lesson = "Garantili yüksek getiri vaadi, finansın en eski kırmızı bayrağıdır. Getiri garantisi veren her yapıyı resmi kayıtlardan sorgula.",
        choices = listOf(
            StoryChoice(
                id = "d27-join",
                label = "10.000 TL ile gir",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -10_000, risk = 20, wellbeing = -4, setFlags = listOf("scammed")),
                result = "İlk ay küçük bir ödeme aldın, sonra grup sessizleşti. 10.000 TL gitti; geri dönmesi çok zor."
            ),
            StoryChoice(
                id = "d27-research",
                label = "Reddet, resmi kayıtlardan araştır",
                hint = "Bilgi 60+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 60, note = "Bilgi 60+"),
                effect = ChoiceEffect(knowledge = 6, risk = -6),
                result = "Şirketin SPK kaydı olmadığını gördün ve teklifi reddettin. Bu bilgi, 10.000 TL'den değerliydi."
            ),
            StoryChoice(
                id = "d27-small",
                label = "Küçük bir miktarla dene",
                hint = "-1.000 TL",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(cash = -1_000, risk = 8),
                result = "1.000 TL ile denedin. İki hafta sonra iletişim kesildi; kaybın büyük değil ama ders büyük."
            ),
            StoryChoice(
                id = "d27-no",
                label = "Reddet, uzak dur",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, risk = -4),
                result = "Teklife hayır dedin. Garantili yüksek getiri diye bir şey yoktur; risk gizlenmişse daha büyüktür."
            )
        )
    ),

    // ── GÜN 28 ────────────────────────────────────────────────
    StoryEvent(
        day = 28,
        title = "Ay Sonu Bilançosu",
        icon = "🧮",
        scene = listOf(
            "Ay bitmek üzere. Hesabına, kart ekstresine ve yatırımına bakıyorsun.",
            "Şimdi küçük bir ayar, gelecek ayın tamamını değiştirebilir."
        ),
        lesson = "Ay sonu kontrolü, sadece geçmişi görmek değil gelecek ayı planlamaktır. Borç, birikim ve harcama aynı tabloda okunmalı.",
        choices = listOf(
            StoryChoice(
                id = "d28-debt",
                label = "Kart borcunun bir kısmını kapat",
                hint = "Bilgi gerekmez · Nakit 6.000+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minCash = 6_000, note = "Nakit 6.000+"),
                effect = ChoiceEffect(cash = -6_000, debt = -6_000, wellbeing = 5, knowledge = 5),
                result = "Kart borcunun 6.000 TL'sini kapattın. Faiz yükü azaldı ve gelecek ay daha rahat başlayacaksın."
            ),
            StoryChoice(
                id = "d28-invest",
                label = "Birikimi yatırıma ekle",
                hint = "-5.000 TL",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 5_000, knowledge = 3, risk = 4),
                result = "5.000 TL daha yatırıma ekledin. Birikim büyüyor ama nakitteki tamponun inceldi."
            ),
            StoryChoice(
                id = "d28-reward",
                label = "Kendine küçük bir ödül ver",
                hint = "-3.000 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -3_000, wellbeing = 8),
                result = "Bir aylık emeğin için kendine ödül aldın. Motivasyon da bir kaynaktır; ölçülü ödül bütçeyi bitirmez."
            ),
            StoryChoice(
                id = "d28-wait",
                label = "Harcama yapmadan tabloyu izle",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 4, wellbeing = 1),
                result = "Ek bir hamle yapmadın, bilançoyu olduğu gibi kabul ettin. Bazen en iyi karar beklemektir."
            )
        )
    ),

    // ── GÜN 29 ────────────────────────────────────────────────
    StoryEvent(
        day = 29,
        title = "Acil Durum",
        icon = "🚑",
        scene = listOf(
            "Annen rahatsızlandı ve özel hastanede tedavi gerekiyor: 12.000 TL.",
            "Acil fonun var mı, yoksa borç mu? Bu an, bütün ay boyunca verdiğin kararların sınavı."
        ),
        lesson = "Acil durum fonu, kriz anında karar verme özgürlüğüdür. 3-6 aylık gideri karşılayan fon, borçlanma ihtimalini düşürür.",
        choices = listOf(
            StoryChoice(
                id = "d29-cash",
                label = "Nakit öde",
                hint = "-12.000 TL",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = -12_000, wellbeing = -2, knowledge = 2),
                result = "Tedaviyi nakit karşıladın. Bütçen zorlandı ama borçlanmadın; sevdiklerin için doğru karar."
            ),
            StoryChoice(
                id = "d29-card",
                label = "Kartla öde",
                hint = "+12.000 TL borç",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(debt = 12_000, risk = 8, wellbeing = -4),
                result = "Tedaviyi kartla ödedin. Sağlık sorunu çözüldü ama borç, gelecek aylara taşındı."
            ),
            StoryChoice(
                id = "d29-state",
                label = "Devlet hastanesine götür, sıra bekle",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(wellbeing = -6, knowledge = 3),
                result = "Devlet hastanesinde sıra beklediniz. Süreç yorucuydu ama tedavi tamamlandı ve bütçen korundu."
            )
        )
    ),

    // ── GÜN 30 ────────────────────────────────────────────────
    StoryEvent(
        day = 30,
        title = "Ayı Kapat",
        icon = "🌙",
        scene = listOf(
            "Takvim 30'u gösteriyor. Bir ay boyunca para, borç, yatırım ve hayat arasında kararlar verdin.",
            "Son bir hamle daha yapabilirsin. Sonra bilanço senin."
        ),
        lesson = "Ay kapanışı bir bitiş değil, bir sonraki ayın başlangıcıdır. Kalan parayı bilinçli yönlendirmek, alışkanlığı kalıcı yapar.",
        choices = listOf(
            StoryChoice(
                id = "d30-fund",
                label = "Kalanı acil durum fonuna ayır",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(knowledge = 6, wellbeing = 4, setFlags = listOf("emergency")),
                result = "Kalan parayı acil durum fonu olarak ayırdın. Bir sonraki beklenmedik gider artık seni korkutmayacak."
            ),
            StoryChoice(
                id = "d30-debt",
                label = "Kalanla borcunu kapat",
                hint = "Nakit 4.000+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minCash = 4_000, note = "Nakit 4.000+"),
                effect = ChoiceEffect(cash = -4_000, debt = -4_000, wellbeing = 3, knowledge = 3),
                result = "Kalan parayla borcunu kapattın. Faiz ödemeden borçsuz bir aya başlamak, en iyi kapanış."
            ),
            StoryChoice(
                id = "d30-celebrate",
                label = "Küçük bir kutlama yap",
                hint = "-1.500 TL",
                tone = ChoiceTone.Social,
                effect = ChoiceEffect(cash = -1_500, wellbeing = 8),
                result = "Bir aylık emeği küçük bir kutlamayla kapattın. Bütçe disiplini, kendini tamamen unutmak değildir."
            )
        )
    )
)

/** Verilen gün için aday olaylar (günün senaryosu + aralığa uyan genel senaryolar). */
fun eventsForDay(day: Int, state: StoryState): List<StoryEvent> {
    val scripted = STORY_EVENTS.filter { it.day == day && it.condition(state) }
    val generic = (
        GENERIC_SCENARIOS + EXTRA_SCENARIOS +
            CHARACTER_SCENARIOS + DILEMMA_SCENARIOS
        ).filter { day in it.dayRange && it.condition(state) }
    return scripted + generic
}

/** Bir olayın tekrar kontrolünde kullanılan benzersiz anahtar. */
fun eventKey(event: StoryEvent): String =
    event.id.ifBlank { "${event.day}:${event.title}" }

/** İlk oyunda her zaman gösterilen, hikâyenin omurgası olan günler. */
private val ANCHOR_DAYS = setOf(1, 2, 3, 10, 12, 13, 19, 20, 27, 30)

/**
 * Günün olayını seçer.
 *
 * - Aynı oyunda veya aynı takvim gününde görülen senaryolar mümkünse atlanır.
 * - Hikâye günleri (kira, dolandırıcılık, final vb.) ilk oyunda korunur;
 *   aynı gün tekrar oynanırsa yerine görülmemiş bir genel senaryo seçilir.
 * - Diğer günlerde günün senaryosu ile genel senaryolar arasında ~yarı yarıya seçim yapılır.
 */
fun eventForDay(day: Int, state: StoryState): StoryEvent? {
    val candidates = eventsForDay(day, state)
    if (candidates.isEmpty()) return null

    val scripted = candidates.filter { it.day == day }
    val generic = candidates.filter { it.day == 0 }

    val unseenScripted = scripted.filter { eventKey(it) !in state.seenScenarios }
    val unseenGeneric = generic.filter { eventKey(it) !in state.seenScenarios }

    val rng = StoryRandom(state.seed + day * 7919 + state.seenScenarios.size * 131)

    fun pick(list: List<StoryEvent>): StoryEvent = list[rng.nextInt(list.size)]

    val chosen = when {
        // Hikâye günü ve henüz görülmemişse onu koru
        unseenScripted.any { day in ANCHOR_DAYS } -> pick(unseenScripted.filter { day in ANCHOR_DAYS })
        unseenScripted.isNotEmpty() && unseenGeneric.isNotEmpty() ->
            if (rng.next() < 0.5) pick(unseenScripted) else pick(unseenGeneric)
        unseenScripted.isNotEmpty() -> pick(unseenScripted)
        unseenGeneric.isNotEmpty() -> pick(unseenGeneric)
        scripted.isNotEmpty() && generic.isNotEmpty() ->
            if (rng.next() < 0.5) pick(scripted) else pick(generic)
        scripted.isNotEmpty() -> pick(scripted)
        else -> pick(generic)
    }
    return chosen.copy(day = day)
}

fun totalChapters(): Int = 30
