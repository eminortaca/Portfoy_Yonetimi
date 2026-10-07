package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;

import java.util.List;
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

    // Bağımlılıkları (Dependencies) içeri alıyoruz
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

    // Canlı piyasa verilerini çekip hafızadaki varlıkları tazeleyen metot
    public void piyasaFiyatlariniGuncelle() {
        // tumunuGetir() yerine findAll() kullanıyoruz
        List<Varlik> varliklar = varlikRepository.findAll();

        for (Varlik varlik : varliklar) {
            // Şimdilik BTC ve ETH için Binance sorgusu atıyoruz
            if (varlik.getSembol().equalsIgnoreCase("BTC") || varlik.getSembol().equalsIgnoreCase("ETH")) {

                double canliFiyat = kriptoServisi.guncelFiyatGetir(varlik.getSembol());

                if (canliFiyat > 0.0) {
                    varlik.setGuncelFiyat(canliFiyat);
                    varlikRepository.guncelle(varlik);
                    LOGGER.log(Level.INFO, "Piyasa fiyatı güncellendi ({0}): {1}", new Object[]{varlik.getSembol(), canliFiyat});
                }
            }
        }
    }
    // YENİ EKLENEN METOT: Arayüzden bu metoda bir "görev" (callback) gönderilecek
    public void otomatikGuncellemeyiBaslat(Runnable arayuzGuncellemeGorevi) {
        zamanlayici = Executors.newSingleThreadScheduledExecutor();

        zamanlayici.scheduleAtFixedRate(() -> {
            // 1. Kendi işini yap (Fiyatları çek ve veritabanını güncelle)
            piyasaFiyatlariniGuncelle();

            // 2. Arayüzün sana gönderdiği "ekran yenileme" görevini tetikle
            if (arayuzGuncellemeGorevi != null) {
                arayuzGuncellemeGorevi.run();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    // YENİ EKLENEN METOT: Program kapanırken arka plan işçisini durdurmak için
    public void otomatikGuncellemeyiDurdur() {
        if (zamanlayici != null && !zamanlayici.isShutdown()) {
            zamanlayici.shutdown();
        }
    }
}