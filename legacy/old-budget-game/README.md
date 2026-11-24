# Eski 30 Günlük Bütçe Oyunu (Arşiv)

Bu klasör, uygulamanın eski "Oyun" sekmesinde kullanılan 30 günlük bütçe
simülasyonunun kaynak kodunu içerir.

**Durum: kullanım dışı.** Yeni oyun motoru `app/src/main/java/.../data/story/`
ve `app/src/main/java/.../ui/screens/story/` altında yaşıyor. Bu klasör Gradle
kaynak kümesine dahil değildir, yani derlemeye katılmaz.

Neden burada:
- Eski Banka / Market / Yatırım / Haber ekranlarındaki bazı fikirler ileride
  yeni hikâye motoruna taşınabilir.
- Eski denge (balance) değerleri ve iş kuralları referans olarak saklanıyor.

Taşınan dosyalar:
- `data/Engine.kt` — günlük simülasyon, piyasa fiyatları, puanlama
- `data/GameState.kt` — durum modeli ve zorluk dengesi
- `data/GameViewModel.kt` — kayıt/yükleme
- `ui/screens/GameHomeScreen.kt`, `GameScreen.kt`, `GameEndScreen.kt`,
  `GameBankScreen.kt`, `GameMarketInvestScreen.kt`, `GameTrendChart.kt`
- `test/EngineTest.kt` — eski motorun birim testleri

Yeni oyun motorunun testleri:
`app/src/test/java/com/egekilavuz/economylab/data/story/StoryEngineTest.kt`
