package com.emin.portfoy.repository;

import com.emin.portfoy.models.Varlik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VarlikRepository {

    // Verilerin tutulduğu bellek içi yapı
    private final Map<String, Varlik> varliklar = new HashMap<>();

    public void save(Varlik varlik) {
        if (varlik == null) {
            throw new IllegalArgumentException("Varlık boş olamaz");
        }
        varliklar.put(varlik.getSembol().toUpperCase(), varlik);
    }

    public Optional<Varlik> findBySembol(String sembol) {
        if (sembol == null || sembol.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(varliklar.get(sembol.trim().toUpperCase()));
    }

    // Zaten var olan tümünü getirme metodun:
    public List<Varlik> findAll() {
        return new ArrayList<>(varliklar.values());
    }

    // Fiyatı değişen varlığı Map üzerinde güncelleyen metot:
    public void guncelle(Varlik varlik) {
        if (varlik != null && varliklar.containsKey(varlik.getSembol().toUpperCase())) {
            varliklar.put(varlik.getSembol().toUpperCase(), varlik);
        }
    }
}