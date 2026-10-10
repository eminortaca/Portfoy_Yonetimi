package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PortfoyService {
    private final KriptoServisi kriptoServisi;
    private static final Logger LOGGER = Logger.getLogger(PortfoyService.class.getName());
    private final VarlikRepository varlikRepository;
    private ScheduledExecutorService zamanlayici;

    public PortfoyService(VarlikRepository varlikRepository, KriptoServisi kriptoServisi) {
        this.varlikRepository = varlikRepository;
        this.kriptoServisi = kriptoServisi;
    }

    public void varlikEkle(Varlik varlik) {
        varlikRepository.save(varlik);
        LOGGER.log(Level.INFO, "Varlık eklendi: {0}", varlik.getSembol());
    }

    public void varlikFiyatiGuncelle(String sembol, double yeniFiyat) {
        Varlik varlik = varlikRepository.findBySembol(sembol)
                .orElseThrow(() -> new IllegalArgumentException("Varlık bulunamadı: " + sembol));
        varlik.setGuncelFiyat(yeniFiyat);
        LOGGER.log(Level.INFO, "Fiyat güncellendi: {0} -> {1}", new Object[]{varlik.getSembol(), yeniFiyat});
    }

    public List<Varlik> tumVarliklar() {
        return varlikRepository.findAll();
    }

    public double toplamMaliyet() {
        return varlikRepository.findAll().stream()
                .mapToDouble(Varlik::getToplamMaliyet)
                .sum();
    }

    public double toplamDeger() {
        return varlikRepository.findAll().stream()
                .mapToDouble(Varlik::getToplamDeger)
                .sum();
    }

    public double toplamKarZarar() {
        return toplamDeger() - toplamMaliyet();
    }

    // GÜNCELLENEN METOT: N+1 Problemi (Binance API Banlanma Riski) Çözüldü
    public void piyasaFiyatlariniGuncelle() {
        // 1. Önce Binance'ten TÜM piyasa verisini tek seferde sözlük (Map) olarak çek
        Map<String, Double> canliFiyatlar = kriptoServisi.tumFiyatlariGetir();

        if (canliFiyatlar.isEmpty()) {
            LOGGER.warning("Fiyatlar Binance'ten alınamadı, güncelleme atlandı.");
            return;
        }

        // 2. Veritabanındaki kendi varlıklarımızı çek
        List<Varlik> varliklar = varlikRepository.findAll();

        // 3. Kendi varlıklarımızı Binance listesiyle eşleştir
        for (Varlik varlik : varliklar) {
            String sembol = varlik.getSembol().toUpperCase();

            // Sözlükte bizim coin var mı? (Sadece BTC ve ETH kısıtlaması kalktı!)
            if (canliFiyatlar.containsKey(sembol)) {
                double guncelFiyat = canliFiyatlar.get(sembol);

                varlik.setGuncelFiyat(guncelFiyat);
                varlikRepository.guncelle(varlik); // Veritabanını güncelle

                LOGGER.log(Level.INFO, "Piyasa fiyatı güncellendi ({0}): {1}", new Object[]{sembol, guncelFiyat});
            }
        }
    }

    public void otomatikGuncellemeyiBaslat(Runnable arayuzGuncellemeGorevi) {
        zamanlayici = Executors.newSingleThreadScheduledExecutor();

        zamanlayici.scheduleAtFixedRate(() -> {
            piyasaFiyatlariniGuncelle();

            if (arayuzGuncellemeGorevi != null) {
                arayuzGuncellemeGorevi.run();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    public void otomatikGuncellemeyiDurdur() {
        if (zamanlayici != null && !zamanlayici.isShutdown()) {
            zamanlayici.shutdown();
        }
    }
    // YENİ METOT: Arayüzden gelen ham metinleri işler, doğrular ve kaydeder
    public void yeniVarlikIsleVeEkle(String sembol, String miktarStr, String maliyetStr) {
        // 1. Boşluk kontrolü
        if (sembol == null || sembol.trim().isEmpty()) {
            throw new IllegalArgumentException("Sembol alanı boş bırakılamaz!");
        }

        String temizSembol = sembol.trim().toUpperCase();

        // 2. Format düzeltmesi (Kullanıcı virgül girdiyse noktaya çevir)
        if (miktarStr != null) miktarStr = miktarStr.replace(",", ".");
        if (maliyetStr != null) maliyetStr = maliyetStr.replace(",", ".");

        double miktar;
        double maliyet;

        // 3. Sayıya çevirme ve harf/yanlış karakter kontrolü
        try {
            miktar = Double.parseDouble(miktarStr != null && !miktarStr.trim().isEmpty() ? miktarStr : "0");
            maliyet = Double.parseDouble(maliyetStr != null && !maliyetStr.trim().isEmpty() ? maliyetStr : "0");
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Lütfen miktar ve maliyet için geçerli bir sayı girin.");
        }

        // 4. Mantık kuralları (İş kuralı: Miktar 0 olamaz)
        if (miktar <= 0 || maliyet < 0) {
            throw new IllegalArgumentException("Miktar 0'dan büyük olmalı ve maliyet negatif olamaz.");
        }

        // 5. Her şey doğruysa Obje oluşturma (İşi arayüzden aldık)
        Varlik yeniVarlik = new Varlik(temizSembol, miktar, maliyet, 0.0);

        // 6. Mevcut veritabanına kaydetme metodunu çağır
        varlikEkle(yeniVarlik);
    }
    public void varlikSil(String sembol) {
        if (sembol == null || sembol.trim().isEmpty()) {
            throw new IllegalArgumentException("Silinecek sembol geçersiz.");
        }
        varlikRepository.delete(sembol.toUpperCase());
        LOGGER.log(Level.INFO, "Varlık silindi: {0}", sembol);
    }
}