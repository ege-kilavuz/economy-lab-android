# v1.6.2 — Senaryo Çeşitliliği & Hafta Özeti Düzeni

## 🎲 Senaryolar
- **26 genel senaryo** eklendi (abonelik tuzağı, beklenmedik fatura, ikinci el, banka promosyonu, phishing SMS, tatil, check-up, zam pazarlığı, vergi, araç masrafı, yılbaşı, acil tamir, yatırım fırsatı, aile ziyareti, banka masrafı, internet arızası, elden satış, kurs, komşu yardımı, abonelik denetimi…)
- Toplam **30 hikâye günü + 26 genel senaryo**; her oyunda farklı bir akış
- Hikâye günleri (kira, dolandırıcılık, kredi, final) ilk oyunda korunur
- **Aynı oyunda tekrar yok**: görülen senaryolar bir daha seçilmez
- **Aynı gün içinde tekrar yok**: görülen senaryolar günlük olarak kaydedilir, yeni oyunda atlanır (havuz yetene kadar)

## 🎨 Düzeltmeler
- Hafta/periyot özeti ekranındaki hizalama düzeltildi: eşit genişlikte özet sütunları, sabit “Gün / Olay / Hedef” sütunları, taşan başlıklar için ellipsis
- Sürüm 1.6.2 (versionCode 20)

---

# v1.6.1 — Seviye Detayı & Liderlik Düzeni

## ✨ Yeni
- Üstteki **seviye barına dokununca** seviye tablosu, XP ilerlemesi, sonraki seviyeye kalan XP, gün serisi ve XP kaynakları açılıyor
- Seviye barında tıklanabilirlik göstergesi (`›`)

## 🎨 Düzeltmeler
- **Liderlik ekranı yeniden düzenlendi**: sabit sütunlar (# / Oyuncu / XP-Puan), hizalı satırlar, taşan isimler için ellipsis
- Liderlikteki gereksiz XP kartı kaldırıldı (üstteki seviye barı ile tekrar ediyordu)
- Yerel/Global geçişi seçilebilir çiplere dönüştürüldü
- Boş durum ve yükleniyor göstergesi düzeltildi
- Sürüm 1.6.1 (versionCode 19)

---

# v1.6.0 — Ritim & Canlı Ekonomi

## 🎯 Ödül döngüsü
- **Bölüm başına XP**: her karar +5 XP, günün hedefi +10, 3’lü seri +15, rozet +20
- **Günün hedefi**: her bölümde en az bir seçenekle tutturulabilen mikro hedef
- **Oyun içi rozetler**: İlk Yatırım, Tetikte, Acil Fon, Bilgi Avcısı, Borçsuz, Sigortalı, Seri Karar
- **Periyot özetleri**: 10. ve 20. günde özet ekranı + 25 XP bonusu
- XP çubuğu artık oyun sırasında anında güncelleniyor

## 📈 Canlı ekonomi
- **Günlük portföy değeri**: yatırım tercihine göre (altın/döviz/borsa/kripto) her gün değişir
- **Haftalık kart faizi**: 7/14/21/28. günlerde borca %1,2 faiz işler
- **Rastgele ara olaylar**: prim, ceza, ikinci el satışı, beklenmedik masraf
- Piyasa şeridine borsa endeksi eklendi

## 🎭 Sonuçlar
- **Çoklu final**: Borç Sarmalı, Pahalı Bir Ders, Yatırımcı, Kariyer Basamağı, Sağlam Temel, Dengeli Hayat, Yolun Başı
- Ay sonu ekranında final hikâyesi, toplam XP, hedef ve rozet özeti

## ⚡ Tempo
- **Hızlı mod**: kısa metin, gizlenebilir ekonomi notu ve otomatik geçiş (duraklatılabilir)
- Seçimlerde tık sesi ve haptik geri bildirim
- Stat barları animasyonlu

## 🧪 Teknik
- 28 birim testi (hedef, rozet, faiz, portföy, final, XP)
- Sürüm 1.6.0 (versionCode 18)

---

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
