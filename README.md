<div align="center">
  <h1>🧪 Ekonomi Laboratuvarı</h1>
  <p><strong>Gençler için oyunlaştırılmış finansal okuryazarlık uygulaması</strong></p>
  <p>
    <img src="https://img.shields.io/badge/Kotlin-2.1.0-purple?logo=kotlin" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Compose-BOM_2025.01-blue?logo=jetpackcompose" alt="Compose" />
    <img src="https://img.shields.io/badge/API-24%2B-brightgreen" alt="API" />
    <img src="https://img.shields.io/badge/sürüm-1.0.0--rc.1-orange" alt="Version" />
    <img src="https://img.shields.io/badge/license-MIT-lightgrey" alt="License" />
  </p>
</div>

---

## 📖 Ne İşe Yarar?

Finansal okuryazarlık öğrenmek isteyen gençler için **oyunlaştırılmış** bir Android uygulaması. 
30 günlük ekonomi simülasyonu, interaktif quizler, mini oyunlar ve görsel simülasyonlarla 
parayı yönetmeyi öğretir.

**Amaç:** Bir gencin finansal terimleri eğlenerek öğrenmesi ve uygulamaya bağlı kalması.

---

## ✨ Özellikler

### 📚 Öğren (12 Kategori)

| Kategori | İçerik |
|---|---|
| 📖 Temeller | Enflasyon, alım gücü, likidite |
| 📊 Bütçe | 50/30/20 kuralı, acil durum fonu |
| 💳 Kredi | Kredi kartı, borç yönetimi, kredi notu |
| 📈 Yatırım | DCA, risk/getiri, çeşitlendirme |
| 🏛️ Borsa | Hisse senedi, halka arz, portföy |
| 🌍 Makro | Enflasyon, faiz, MB politikaları |
| 🧾 Vergi | KDV, gelir vergisi, stopaj |
| ⭐ Kredi Notu | Kredi notu hesaplama |
| ₿ Kripto | Blokzincir, dijital varlıklar |
| 🌐 Küresel | Dünya ekonomisi, ticaret |
| 🧠 Psikoloji | FOMO, sürü psikolojisi |
| 🛡️ Güvenlik | Dolandırıcılık korunma |

Her kategoride: **içerik metinleri, QA kartları, taktik/ipuçları, videolar, uyarılar, quiz**

### 🎮 30 Gün Ekonomi Oyunu

Bir ay boyunca bütçeni yönet:
- 🏦 **Banka** — Kira, fatura, kredi kartı ödemeleri
- 🛒 **Market** — Alışveriş, eğlence, tasarruf
- 📈 **Yatırım** — Altın, dolar, BTC, ETH, hisse al/sat
- 📰 **Haber** — Günlük ekonomi haberleri
- 📊 **Ay Sonu** — Skor, bütçe analizi, enflasyon etkisi
- 🎯 **Günlük Görevler** — Quest sistemi + puan

### 🏛️ Simülasyonlar (4 Adet)

| Simülasyon | Süre | Açıklama |
|---|---|---|
| 📈 Enflasyon Lab | 6 dk | Sepet fiyatı vs maaş (Canvas grafik) |
| 💹 Faiz & Kredi Lab | 5 dk | Borç/birikim karşılaştırması |
| 🏛️ MB Karar Odası | 6 dk | Faiz-enflasyon-istihdam dengesi |
| 💰 BES | 5 dk | Devlet katkılı emeklilik hesabı |

### 🎯 Mini Oyunlar (5 Adet)

| Oyun | Süre | Mekanik |
|---|---|---|
| 📈 Borsa Oyunu | 2 dk | Konsept eşleştirme |
| 💳 Kart Kaçışı | 3 dk | Borç yönetimi simülasyonu |
| 🧩 Bütçe Tetris | 2 dk | Para bloklarını doğru kovaya koy |
| 📰 Haber mi Gürültü mü? | 2 dk | Sinyal/gürültü ayırt etme |
| 🛡️ Dolandırıcılık Radar | 2 dk | Dolandırıcılık senaryo seçimi |

### 🏆 Rozetler & Puan

| Rozet | Şart |
|---|---|
| 👶 İlk Adım | İlk quiz |
| 📚 Finansçı | 5 quiz |
| 🎓 Uzman | 10 quiz |
| 🧠 Her Şeyi Bilen | Tüm quizler |
| 🎮 Oyuncu | Oyunu bitir |
| 💪 Dayanıklı | Zor modda bitir |
| 🐷 Tutumlu | Kartsız bitir |
| 📈 Yatırımcı | 10.000 TL portföy |
| 📖 Okuyucu | 20 içerik oku |
| 🔥 Düzenli | 3 gün streak |
| ⭐ Azimli | 7 gün streak |

### 🏅 Liderlik Tablosu
- Yerel skor kaydı (SharedPreferences)
- GitHub Issue API ile global skor paylaşımı
- Rozet gösterimi

---

## 📊 Değerlendirme

| Kategori | Puan | Not |
|---|---|---|
| 🎨 **UI/UX** | 8/10 | Compose native, karanlık tema, smooth geçişler |
| 🧱 **Mimari** | 8/10 | ViewModel + pure engine, JSON asset içerik |
| ⚡ **Performans** | 9/10 | Native Compose, Canvas grafik, WebView yok |
| ✅ **Test** | 3/10 | Unit test yok, manuel test |
| ♿ **Erişilebilirlik** | 5/10 | contentDescription yok, renk kontrastı iyi |
| 📦 **Boyut** | 7/10 | 6MB APK, JSON asset 66KB |
| 📱 **Android** | 9/10 | API 24+, çentik desteği, edge-to-edge |

**Genel Puan: 7.0/10**

---

## 🗺️ Geliştirme Roadmap

### ✅ Tamamlanan
- [x] Kotlin + Jetpack Compose proje iskeleti
- [x] Oyun motoru portu (engine.ts → Engine.kt)
- [x] 12 öğren kategorisi + içerik + quiz
- [x] 30 Gün Oyunu (6 ekran)
- [x] 4 Simülasyon (Canvas grafik)
- [x] 5 Mini Oyun
- [x] Rozet/Puan sistemi
- [x] Liderlik tablosu (local + GitHub)
- [x] Progress tracking + dark tema + çentik

### 🔄 Kısa Vade
- [ ] **Unit test** — Engine testleri (JUnit + Compose Test)
- [ ] **Erişilebilirlik** — contentDescription, klavye navigasyonu
- [ ] **İçerik güncelleme** — Yeni quiz soruları, video linkleri
- [ ] **Animasyonlar** — Compose animation geçişleri

### 📅 Orta Vade
- [ ] **Çoklu dil** — İngilizce desteği
- [ ] **Kullanıcı profili** — Avatar, seviye, XP çubuğu
- [ ] **Hatırlatma** — Günlük bildirim (oyun/quiz)
- [ ] **Paylaş** — Skor paylaşma (screenshot)
- [ ] **Karanlık/Aydınlık** — Tema seçici

### 🚀 Uzun Vade
- [ ] **Firebase entegrasyonu** — Gerçek zamanlı leaderboard
- [ ] **Öğretmen paneli** — Sınıf yönetimi
- [ ] **Web versiyonu** — Compose Multiplatform
- [ ] **AI chat** — Finansal sorular için yapay zeka asistanı

---

## 📱 Kurulum

### APK İndir
[![Release](https://img.shields.io/github/v/release/ege-kilavuz/economy-lab-android)](https://github.com/ege-kilavuz/economy-lab-android/releases)

```bash
# ADB ile yükleme
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### Geliştirme
```bash
# Projeyi klonla
git clone https://github.com/ege-kilavuz/economy-lab-android.git
cd economy-lab-android

# Build
./gradlew assembleDebug

# Çalıştır
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

---

## 🛠️ Teknik Altyapı

| Bileşen | Teknoloji |
|---|---|
| **Dil** | Kotlin 2.1.0 |
| **UI** | Jetpack Compose (BOM 2025.01) |
| **Navigasyon** | Navigation Compose |
| **State** | ViewModel + mutableStateOf |
| **İçerik** | JSON asset (66KB) |
| **Grafik** | Custom Canvas drawing |
| **Progress** | SharedPreferences |
| **Leaderboard** | GitHub Issues API |
| **Min SDK** | API 24 (Android 7.0) |
| **Build** | Gradle 8.13 + AGP 8.13 |

### Proje Yapısı
```
app/src/main/java/com/egekilavuz/economylab/
├── MainActivity.kt          # Giriş noktası
├── Navigation.kt             # Bottom nav + routing
├── data/
│   ├── GameState.kt          # Oyun state data class
│   ├── Engine.kt             # Pure oyun motoru
│   ├── Content.kt            # JSON serileştirme
│   ├── ContentLoader.kt      # JSON yükleyici
│   ├── GameViewModel.kt      # Oyun ViewModel
│   ├── ProgressManager.kt    # SharedPreferences
│   ├── AchievementSystem.kt  # Rozetler
│   └── LeaderboardManager.kt # GitHub API
├── ui/
│   ├── theme/Theme.kt        # Karanlık tema
│   └── screens/
│       ├── HomeScreen.kt          # Öğren (2 sütun)
│       ├── SimulationsScreen.kt   # Simülasyonlar
│       ├── CategoryScreen.kt      # Kategori detay
│       ├── QuizScreen.kt          # Quiz
│       ├── GameScreen.kt          # Oyun yönlendirici
│       ├── GameHomeScreen.kt      # Oyun içi ana
│       ├── GameBankScreen.kt      # Banka
│       ├── GameMarketInvestScreen.kt # Market/Yatırım
│       └── GameEndScreen.kt       # Ay sonu
└── ui/simulations/
    ├── InflationSimulation.kt     # Enflasyon
    ├── InterestSimulation.kt      # Faiz
    ├── CentralBankSimulation.kt   # MB
    ├── BESSimulation.kt           # BES
    ├── CandleAndCardGames.kt      # Borsa + Kart
    └── BudgetNewsScamGames.kt     # Bütçe/Haber/Radar
```

---

<div align="center">
  <p>
    <a href="https://github.com/ege-kilavuz/economy-lab">🌐 Web Versiyonu</a>
    ·
    <a href="https://github.com/ege-kilavuz/economy-lab-android">📱 Android Versiyonu</a>
  </p>
  <p>
    🧪 <strong>Ekonomi Laboratuvarı</strong> — Ege Kılavuz tarafından geliştirilmiştir.
  </p>
</div>
