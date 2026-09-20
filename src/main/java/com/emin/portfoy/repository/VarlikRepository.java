package com.emin.portfoy.repository;

import com.emin.portfoy.models.Varlik;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VarlikRepository {

    private final Map<String, Varlik> varliklar = new HashMap<>();

    public void save(Varlik varlik) {
        if (varlik == null) {
            throw new IllegalArgumentException("varlık boş olamaz");
        }
        varliklar.put(varlik.getSembol(), varlik);
    }

    public Optional<Varlik> findBySembol(String sembol) {
        if (sembol == null || sembol.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(varliklar.get(sembol.trim().toUpperCase()));
    }

    public List<Varlik> findAll() {
        return new ArrayList<>(varliklar.values());
    }
}
