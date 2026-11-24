# v1.5.0 — 30 Gün: İlk Maaş (Oyun baştan yazıldı)

## 🎮 Yeni Oyun Modu
- **Oyun sekmesi sıfırdan yazıldı**: 30 bölümlük hikâye/olay akışı (Türkiye, yeni mezun, ilk maaş ayı)
- Her bölümde **2-4 seçim**; seçimler Nakit, Kart Borcu, Yatırım, Bilgi, Refah ve Risk göstergelerini değiştirir
- **Bilgi ve nakit eşiğiyle açılan seçenekler** (ör. kira pazarlığı, resmi kayıt sorgulama)
- **Dallanan olaylar**: borsa geçmişi, taşınma, kira pazarlığı sonraki bölümleri etkiler
- **Ay sonu**: 100 üzerinden puan, arketip, bilanço, yatırım getirisi, karar günlüğü
- **Otomatik kayıt ve devam etme**
- Liderlik tablosu, XP, ses ve rozet entegrasyonu korundu

## 🧹 Teknik
- Eski 30 günlük bütçe simülasyonu `legacy/old-budget-game/` altına arşivlendi (derlemeye dahil değil)
- Yeni hikâye motoru: `data/story/` (model, içerik, motor, ViewModel)
- Yeni arayüz: `ui/screens/story/` (giriş, oyun, günlük, sonuç)
- Yeni birim testleri: `StoryEngineTest`
- Sürüm 1.5.0 (versionCode 17)

---

# v1.4.0 — XP Sistemi, Ses & İstatistik

## 🚀 Yeni Özellikler
- **XP (Deneyim Puanı) Sistemi**: İçerik okuma, quiz çözme, oyun oynama gibi her aktivite XP kazandırıyor. Seviye atladıkça ünvanlar açılıyor.
- **XP Tabanlı Liderlik**: Liderlik tablosu artık XP'ye göre sıralanıyor, seviye ve ünvan bilgisi gösteriliyor.
- **İstatistik Ekranı 📊**: Kategori bazlı ilerleme, rozetler, okuma/quiz/simülasyon oranları.
- **Ses Efektleri 🔔**: Doğru/yanlış cevap, level atlama ve rozet kazanma sesleri.
- **Animasyonlar ✨**: Bitiş ekranında akıcı giriş, quiz geçişlerinde yumuşak animasyon.

## 📚 İçerik
- 13 finansal okuryazarlık kategorisine **135 yeni soru** eklendi (her kategori 10+ soru)
- Günlük quiz havuzu 120 soru

## ⚙️ Teknik
- kotlinx.serialization ile güvenilir leaderboard verisi
- Dinamik içerik güncelleme (`_version 16`)
- 30 adet unit test (Engine)
- Ses yönetimi için SoundPool entegrasyonu
