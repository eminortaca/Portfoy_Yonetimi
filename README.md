# Portföy Yönetimi

## Proje Amacı
Portföydeki varlıkları (sembol, miktar, maliyet, güncel fiyat) yönetmek ve toplam maliyet/değer/kâr-zarar hesaplarını yapmak için basit bir Java uygulamasıdır.

## Özellikler
- Varlık ekleme ve listeleme
- Sembol bazlı fiyat güncelleme
- Toplam maliyet hesaplama
- Toplam güncel değer hesaplama
- Toplam kâr/zarar hesaplama
- Giriş doğrulama ve hata yönetimi

## Kurulum
### Gereksinimler
- Java 17+
- Maven 3.9+

### Adımlar
1. Depoyu klonlayın.
2. Proje dizininde aşağıdaki komutu çalıştırın:
   ```bash
   mvn test
   ```

## Kullanım
Uygulamayı çalıştırmak için:
```bash
mvn -q exec:java -Dexec.mainClass="com.emin.portfoy.controller.Main"
```

Örnek çıktı: portföy toplam değer, maliyet ve kâr/zarar bilgileri.

## Ekran Görüntüleri
- Konsol uygulaması olduğu için ekran görüntüsü yerine örnek çıktı paylaşabilirsiniz.

## Test
Birim testleri çalıştırmak için:
```bash
mvn test
```
