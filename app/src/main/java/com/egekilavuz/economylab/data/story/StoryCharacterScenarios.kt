package com.egekilavuz.economylab.data.story

/**
 * Tekrar eden karakterlerin olayları.
 * İlişki puanı yeterliyse (veya düşükse) günlük akışa karışır.
 */
val CHARACTER_SCENARIOS: List<StoryEvent> = listOf(

    StoryEvent(
        day = 0,
        dayRange = 14..18,
        id = "char-landlord-offer",
        title = "Necmi Bey'in Teklifi",
        icon = "🏠",
        condition = { it.stats.landlord >= 60 },
        scene = listOf(
            "Ev sahibi Necmi Bey kapıya geldi: \"Kirayı düzenli ödüyorsun. 12 ay sabit kira yapalım, %10 indirim veririm.\"",
            "Teklif cazip ama 12 ay boyunca taşınamayacaksın."
        ),
        lesson = "İyi kiracı olmak pazarlık gücü verir. Uzun sözleşme indirim getirir ama esnekliği azaltır.",
        choices = listOf(
            StoryChoice(
                id = "char-landlord-lock",
                label = "12 ay sabit kira, %10 indirim",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(
                    landlord = 6, knowledge = 3, wellbeing = 2,
                    setFlags = listOf("lease_discount"),
                    delayed = listOf(
                        DelayedEffect(
                            title = "Kira indirimi",
                            days = 10,
                            message = "Sabit kira anlaşmasının indirimi hesabına yansıdı: +800 TL",
                            cash = 800
                        )
                    )
                ),
                result = "Anlaşmayı imzaladın. Kira 12 ay sabit; indirim birkaç gün içinde hesabına yansıyacak."
            ),
            StoryChoice(
                id = "char-landlord-flex",
                label = "Esnek kal, indirimi geç",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(landlord = -2, knowledge = 4, wellbeing = 1),
                result = "Teklifi nazikçe reddettin. Esnekliğini korudun; belki başka bir fırsat çıkar."
            ),
            StoryChoice(
                id = "char-landlord-trial",
                label = "6 ay deneme pazarlığı yap",
                hint = "Bilgi 45+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 45, note = "Bilgi 45+"),
                effect = ChoiceEffect(
                    landlord = 3, cash = 500, knowledge = 3,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Pazarlık bonusu",
                            days = 7,
                            message = "6 aylık deneme anlaşmasının küçük indirimi yansıdı: +400 TL",
                            cash = 400
                        )
                    )
                ),
                result = "6 ay deneme anlaşması yaptın. Hem indirim aldın hem tamamen kilitlenmedin."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 19..24,
        id = "char-boss-pressure",
        title = "Patronun Baskısı",
        icon = "💼",
        condition = { it.stats.boss <= 45 },
        scene = listOf(
            "Patronun cuma akşamı yazdı: \"Hafta sonu bu işi bitirmeliyiz. Mesai ücreti yok ama takdir ediyorum.\"",
            "İş bitmezse pazartesi seni zor bir toplantı bekliyor."
        ),
        lesson = "Ücretsiz fazla mesai kariyer için kısa vadeli bir yatırım gibi görünebilir; ama sınır koymamak tükenmişliğe yol açar.",
        choices = listOf(
            StoryChoice(
                id = "char-boss-accept",
                label = "Kabul et, hafta sonu çalış",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(
                    boss = 6, wellbeing = -6, knowledge = 1,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Gecikmeli prim",
                            days = 7,
                            message = "Hafta sonu mesaisinin küçük primi yattı: +1.200 TL",
                            cash = 1_200
                        )
                    )
                ),
                result = "Hafta sonunu çalışarak geçirdin. Patron memnun; prim birkaç gün sonra gelecek."
            ),
            StoryChoice(
                id = "char-boss-refuse",
                label = "Reddet, sınırını koru",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(boss = -5, wellbeing = 3, risk = 2),
                result = "Hayır dedin. Patron bozuldu ama hafta sonu sana kaldı."
            ),
            StoryChoice(
                id = "char-boss-negotiate",
                label = "Pazarlık et: işi paylaş, karşılık iste",
                hint = "Bilgi 50+",
                tone = ChoiceTone.Safe,
                requires = ChoiceRequirement(minKnowledge = 50, note = "Bilgi 50+"),
                effect = ChoiceEffect(boss = 2, cash = 1_000, knowledge = 4, wellbeing = -2),
                result = "İşi paylaştırdın ve karşılığında küçük bir ödeme aldın. Sınır koymak her zaman 'hayır' demek değil."
            )
        )
    ),

    StoryEvent(
        day = 0,
        dayRange = 21..27,
        id = "char-friend-return",
        title = "Deniz'in Geri Dönüşü",
        icon = "🤝",
        condition = { it.stats.friend >= 60 },
        scene = listOf(
            "Arkadaşın Deniz aradı: \"Geçen ay verdiğin parayı getirdim. Bir de yanımda küçük bir iş var, ister misin?\"",
            "Parayı almak rahatlatıcı; yan gelir ise hafta sonunu yiyecek."
        ),
        lesson = "Güven ilişkisi, maddi faydaya dönüşebilir. Ama her fırsat, zaman ve enerji maliyeti taşır.",
        choices = listOf(
            StoryChoice(
                id = "char-friend-cash",
                label = "Parayı al, teşekkür et",
                tone = ChoiceTone.Safe,
                effect = ChoiceEffect(cash = 1_000, friend = 3),
                result = "1.000 TL geri geldi. Deniz'e güvendiğin için pişman olmadın."
            ),
            StoryChoice(
                id = "char-friend-invest",
                label = "Parayı doğrudan yatırıma yönlendir",
                tone = ChoiceTone.Balanced,
                effect = ChoiceEffect(invest = 1_000, friend = 2, knowledge = 3),
                result = "Gelen parayı harcamadan portföyüne ekledin. Küçük ama disiplinli bir hamle."
            ),
            StoryChoice(
                id = "char-friend-gig",
                label = "Parayı al ve yan geliri de kabul et",
                tone = ChoiceTone.Risky,
                effect = ChoiceEffect(
                    cash = 1_000, friend = 4, wellbeing = -2,
                    delayed = listOf(
                        DelayedEffect(
                            title = "Yan gelir ödemesi",
                            days = 5,
                            message = "Deniz'in işinin ödemesi geldi: +1.500 TL",
                            cash = 1_500
                        )
                    )
                ),
                result = "Parayı aldın ve işi kabul ettin. Hafta sonu yorucu geçecek ama ödeme yolda."
            )
        )
    )
)
