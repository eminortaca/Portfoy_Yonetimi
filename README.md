# 📊 Portföy Yönetim Sistemi (Desktop Application)

Java 21 ve JavaFX kullanılarak geliştirilmiş, N-Tier (Katmanlı) mimariye sahip masaüstü portföy takip ve analiz uygulaması. SQLite veritabanı ile kalıcı veri yönetimi sağlarken, Binance REST API üzerinden anlık piyasa fiyatlarını çekerek kâr/zarar durumunu dinamik olarak hesaplar.

---

## ✨ Özellikler

* **Masaüstü Kullanıcı Arayüzü (JavaFX):** Temiz, duyarlı ve anlık portföy durumunu özetleyen masaüstü tablosu.
* **Kalıcı Veri Saklama (SQLite):** Varlıkların (`sembol`, `miktar`, `ortalama_maliyet`, `guncel_fiyat`) yerel `portfoy.db` veritabanında saklanması ve otomatik senkronizasyonu.
* **Canlı Piyasa Fiyatları (REST API):** Binance Public REST API entegrasyonu (`HttpClient` + Google Gson) ile anlık kripto fiyat güncellemesi.
* **Dinamik Portföy Analizi:** Toplam yatırım maliyeti, anlık portföy değeri ve kâr/zarar farkının otomatik hesaplanması.
* **Katmanlı Mimari (N-Tier Architecture):** Model, Repository, Service ve Controller katmanlarının bağımsızlığı ve temiz kod (Clean Code) standartları.
* **Birim Testleri (Unit Tests):** Servis ve matematiksel hesaplama katmanının JUnit 5 ile otomatik doğrulanması.

---

## 🛠 Kullanılan Teknolojiler

* **Java 21 LTS**
* **JavaFX 21** (Masaüstü Arayüzü & TableView)
* **SQLite & JDBC** (Yerel İlişkisel Veritabanı)
* **Java 11+ HttpClient & Google Gson** (Asenkron/Senkron REST API ve JSON Ayrıştırma)
* **Apache Maven** (Bağımlılık ve Derleme Yönetimi)
* **JUnit 5** (Otomatik Birim Testleri)

---

## 📂 Proje Dizin Yapısı

```text
src/
├── main/java/com/emin/portfoy/
│   ├── controller/      # Arayüz denetleyicileri ve başlatıcılar (App, Launcher, Main)
│   ├── models/          # Veri modelleri (Varlik POJO)
│   ├── repository/      # SQLite CRUD ve veri erişim katmanı (VarlikRepository)
│   └── service/         # İş mantığı ve Binance istemcisi (PortfoyService, KriptoServisi)
└── test/java/com/emin/portfoy/
    └── service/         # Birim testleri (PortfoyServiceTest)
