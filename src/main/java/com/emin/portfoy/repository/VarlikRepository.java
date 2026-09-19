package com.emin.portfoy.repository;

import com.emin.portfoy.models.Varlik;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VarlikRepository extends JpaRepository<Varlik, Long> {
    Optional<Varlik> findBySembol(String sembol);
    void deleteBySembol(String sembol);
}
