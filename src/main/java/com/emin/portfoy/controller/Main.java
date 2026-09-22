package com.emin.portfoy.controller;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import com.emin.portfoy.service.PortfoyService;
import com.emin.portfoy.service.KriptoServisi;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final KriptoServisi KRIPTO_SERVISI = new KriptoServisi();
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        PortfoyService portfoyService = new PortfoyService(new VarlikRepository());

        try {
            // 1. Binance'ten anlık BTC fiyatını çekip bir değişkene atıyoruz
            double btcGuncelFiyat = KRIPTO_SERVISI.guncelFiyatGetir("BTC");

            // 2. Varlığı eklerken son parametreye (güncel fiyata) internetten aldığımız değişkeni veriyoruz
            // Örnek senaryo: 0.05 adet BTC'miz var, ortalama maliyetimiz 80000 dolar, güncel fiyatı ise API belirledi
            portfoyService.varlikEkle(new Varlik("BTC", 0.05, 80000.0, btcGuncelFiyat));

            // 3. Mevcut hisse ve altın örneklerin
            portfoyService.varlikEkle(new Varlik("THYAO", 10, 250.5, 275.75));
            portfoyService.varlikEkle(new Varlik("XAUUSD", 2, 2300.0, 2350.5));

            // Hesaplamalar artık canlı BTC fiyatı üzerinden yapılacak
            System.out.println("Toplam Maliyet: " + portfoyService.toplamMaliyet());
            System.out.println("Toplam Değer: " + portfoyService.toplamDeger());
            System.out.println("Toplam Kar/Zarar: " + portfoyService.toplamKarZarar());

        } catch (IllegalArgumentException ex) {
            LOGGER.log(Level.SEVERE, "İşlem hatası: {0}", ex.getMessage());
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Beklenmeyen hata", ex);
        }
    }
}