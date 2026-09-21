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
        // 1. Canlı kripto verisini test etmek için çağırıyoruz:
        KRIPTO_SERVISI.bitcoinFiyatiniGetir();

        PortfoyService portfoyService = new PortfoyService(new VarlikRepository());

        try {
            portfoyService.varlikEkle(new Varlik("THYAO", 10, 250.5, 275.75));
            portfoyService.varlikEkle(new Varlik("XAUUSD", 2, 2300.0, 2350.5));

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