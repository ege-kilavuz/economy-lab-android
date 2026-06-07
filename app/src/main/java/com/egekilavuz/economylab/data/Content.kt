package com.egekilavuz.economylab.data

data class LearnCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String
)

val LEARN_CATEGORIES = listOf(
    LearnCategory("basics", "Temeller", "Finansal okuryazarlık 101", "📖"),
    LearnCategory("budget", "Bütçe", "Bütçe yönetimi ve 50/30/20", "📊"),
    LearnCategory("credit", "Kredi", "Kredi kartı ve borç yönetimi", "💳"),
    LearnCategory("investing", "Yatırım", "Yatırım araçları ve risk", "📈"),
    LearnCategory("stock-patterns", "Borsa", "Hisse senedi ve piyasalar", "🏛️"),
    LearnCategory("macro", "Makro", "Enflasyon, faiz, MB politikaları", "🌍"),
    LearnCategory("tax", "Vergi", "KDV, gelir vergisi, stopaj", "🧾"),
    LearnCategory("credit-score", "Kredi Notu", "Kredi notu ve önemi", "⭐"),
    LearnCategory("crypto", "Kripto", "Blokzincir ve dijital varlıklar", "₿"),
    LearnCategory("global-economy", "Küresel", "Dünya ekonomisi ve ticaret", "🌐"),
    LearnCategory("psychology", "Psikoloji", "Yatırım psikolojisi ve davranış", "🧠"),
    LearnCategory("safety", "Güvenlik", "Dolandırıcılık ve güvenlik", "🛡️"),
)

data class SimulationMeta(
    val id: String,
    val title: String,
    val short: String,
    val minutes: Int,
    val level: String,
    val icon: String
)

val SIMULATIONS = listOf(
    SimulationMeta("inflation", "Enflasyon Lab", "Alım gücü ve enflasyon", 6, "Başlangıç", "📈"),
    SimulationMeta("interest", "Faiz & Kredi", "Borç ve birikim karşılaştırması", 5, "Başlangıç", "💹"),
    SimulationMeta("central", "MB Karar Odası", "Faiz-enflasyon dengesi", 6, "Orta", "🏛️"),
    SimulationMeta("bes", "BES", "Devlet katkılı emeklilik", 5, "Başlangıç", "💰"),
)
