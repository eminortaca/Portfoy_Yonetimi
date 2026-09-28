# 📈 Portföy Yönetim Sistemi

Java ile geliştirilmiş, katmanlı mimariye (N-Tier Architecture) sahip, anlık piyasa verilerini REST API üzerinden çekerek portföy maliyet ve kâr/zarar durumunu dinamik olarak hesaplayan portföy takip uygulaması.

---

## 🚀 Özellikler

* **Katmanlı Mimari:** Model, Repository, Service ve Controller katmanları ile modüler ve sürdürülebilir kod yapısı.
* **Canlı Piyasa Entegrasyonu:** Binance Public API üzerinden `java.net.http.HttpClient` kullanılarak canlı kripto para (BTC/USDT) fiyatlarının çekilmesi.
* **JSON Ayrıştırma (Parsing):** Google Gson kütüphanesi kullanılarak API'den gelen metin tabanlı verilerin Java nesnelerine ve sayısal tiplere dönüştürülmesi.
* **Portföy Hesaplamaları:** Toplam maliyet, güncel piyasa değeri ve anlık kâr/zarar analizleri.
* **Birim Testleri (Unit Tests):** JUnit 5 kullanılarak servis ve model mantığının otomatik test edilmesi.

---

## 🛠 Kullanılan Teknolojiler

* **Java 21**
* **Apache Maven** (Bağımlılık ve proje yönetimi)
* **Java 11+ HttpClient** (REST API iletişimi)
* **Google Gson** (JSON veri işleme)
* **JUnit 5** (Birim testleri)
* **SQLite / JDBC** (Veri saklama altyapısı)

---

## 📦 Proje Yapısı

```text
src/
├── main/java/com/emin/portfoy/
│   ├── controller/      # Uygulama giriş noktası (Main)
│   ├── models/          # Veri modelleri (Varlik POJO)
│   ├── repository/      # Veri erişim katmanı (VarlikRepository)
│   └── service/         # İş mantığı ve API istemcileri (PortfoyService, KriptoServisi)
└── test/java/com/emin/portfoy/
    └── service/         # JUnit test senaryoları (PortfoyServiceTest)
