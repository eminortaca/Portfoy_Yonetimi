# 📊 Portföy Yönetim Sistemi (Desktop Application)

Java 21 ve JavaFX kullanılarak geliştirilmiş, N-Tier (Katmanlı) mimariye sahip masaüstü portföy takip ve analiz uygulaması. SQLite veritabanı ile kalıcı veri yönetimi sağlarken, Binance REST API üzerinden anlık piyasa fiyatlarını çekerek kâr/zarar durumunu dinamik olarak hesaplar.

---

## ✨ Özellikler

* **Masaüstü Kullanıcı Arayüzü (JavaFX):** Anlık portföy durumunu, maliyetleri ve güncel piyasa değerlerini listeleyen duyarlı masaüstü tablosu.
* **Kalıcı Veri Saklama (SQLite):** Varlıkların (`sembol`, `miktar`, `ortalama_maliyet`, `guncel_fiyat`) yerel `portfoy.db` veritabanında saklanması ve otomatik senkronizasyonu.
* **Canlı Piyasa Fiyatları (REST API):** Binance Public REST API entegrasyonu (`HttpClient` + Google Gson) ile anlık kripto fiyat güncellemesi.
* **Dinamik Portföy Analizi:** Toplam yatırım maliyeti, anlık portföy değeri ve kâr/zarar farkının otomatik hesaplanması.
* **Katmanlı Mimari (N-Tier Architecture):** Model, Repository, Service ve Controller katmanlarının sorumluluk ayrımı.
* **Birim Testleri (Unit Tests):** Model ve iş mantığı katmanlarının JUnit 5 ile test edilmesi.

---

## 🛠 Kullanılan Teknolojiler

* **Java 21 LTS**
* **JavaFX 21** (Masaüstü Kullanıcı Arayüzü)
* **SQLite & JDBC** (Yerel Veri Saklama)
* **Java 11+ HttpClient & Google Gson** (REST API ve JSON İşleme)
* **Apache Maven** (Bağımlılık ve Derleme Yönetimi)
* **JUnit 5** (Birim Testleri)

---

## 📂 Proje Dizin Yapısı

```text
src/
├── main/java/com/emin/portfoy/
│   ├── controller/
│   │   ├── App.java          # JavaFX Arayüz Bileşenleri ve Tablo Yönetimi
│   │   ├── Launcher.java     # JavaFX Modül Uyumluluğu Başlatıcısı
│   │   └── Main.java         # Konsol Tabanlı Test / Demo Giriş Noktası
│   ├── models/
│   │   └── Varlik.java       # Temel Varlık Modeli (POJO)
│   ├── repository/
│   │   └── VarlikRepository.java # SQLite CRUD ve Tablo İşlemleri
│   └── service/
│       ├── KriptoServisi.java    # Binance REST API İstemcisi
│       └── PortfoyService.java   # Portföy Hesaplamaları ve İş Mantığı
└── test/java/com/emin/portfoy/
    ├── models/
    │   └── VarlikTest.java       # Model Doğrulama Testleri
    └── service/
        └── PortfoyServiceTest.java # Servis ve Hesaplama Birim Testleri
