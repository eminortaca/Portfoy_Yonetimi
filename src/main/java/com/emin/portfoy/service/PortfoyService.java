package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PortfoyService {

    private static final Logger LOGGER = Logger.getLogger(PortfoyService.class.getName());
    private final VarlikRepository varlikRepository;

    public PortfoyService(VarlikRepository varlikRepository) {
        if (varlikRepository == null) {
            throw new IllegalArgumentException("repository boş olamaz");
        }
        this.varlikRepository = varlikRepository;
    }

    @Transactional
    public void varlikEkle(Varlik varlik) {
        if (varlik == null) {
            throw new IllegalArgumentException("varlık boş olamaz");
        }
        varlikRepository.save(varlik);
        LOGGER.log(Level.INFO, "Varlık eklendi: {0}", varlik.getSembol());
    }

    @Transactional
    public void varlikFiyatiGuncelle(String sembol, double yeniFiyat) {
        String normalizedSembol = normalizeSembol(sembol);
        Varlik varlik = varlikRepository.findBySembol(normalizedSembol)
                .orElseThrow(() -> new IllegalArgumentException("Varlık bulunamadı: " + sembol));
        varlik.setGuncelFiyat(yeniFiyat);
        varlikRepository.save(varlik);
        LOGGER.log(Level.INFO, "Fiyat güncellendi: {0} -> {1}", new Object[]{varlik.getSembol(), yeniFiyat});
    }

    @Transactional(readOnly = true)
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

    @Transactional
    public void varlikSil(String sembol) {
        String normalizedSembol = normalizeSembol(sembol);
        Varlik varlik = varlikRepository.findBySembol(normalizedSembol)
                .orElseThrow(() -> new IllegalArgumentException("Varlık bulunamadı: " + sembol));
        varlikRepository.delete(varlik);
        LOGGER.log(Level.INFO, "Varlık silindi: {0}", normalizedSembol);
    }

    private String normalizeSembol(String sembol) {
        if (sembol == null || sembol.trim().isEmpty()) {
            throw new IllegalArgumentException("sembol boş olamaz");
        }
        return sembol.trim().toUpperCase();
    }
}
