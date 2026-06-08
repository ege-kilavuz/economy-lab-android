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
