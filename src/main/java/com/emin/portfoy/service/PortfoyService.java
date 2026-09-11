package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortfoyService {

    private static final Logger LOGGER = Logger.getLogger(PortfoyService.class.getName());
    private final VarlikRepository varlikRepository;

    public PortfoyService(VarlikRepository varlikRepository) {
        if (varlikRepository == null) {
            throw new IllegalArgumentException("repository boş olamaz");
        }
        this.varlikRepository = varlikRepository;
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
}
