package com.emin.portfoy.repository;

import com.emin.portfoy.models.Varlik;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VarlikRepository {
    private static final String DB_URL = "jdbc:sqlite:portfoy.db";

    public VarlikRepository() {
        tabloOlustur();
    }

    private Connection baglantiAl() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void tabloOlustur() {
        String sql = """
            CREATE TABLE IF NOT EXISTS varliklar (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sembol TEXT NOT NULL UNIQUE,
                miktar REAL NOT NULL,
                ortalama_maliyet REAL NOT NULL,
                guncel_fiyat REAL NOT NULL
            );
            """;

        try (Connection conn = baglantiAl();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Veritabanı tablosu oluşturulamadı: " + e.getMessage(), e);
        }
    }

    public void save(Varlik varlik) {
        String sql = """
            INSERT INTO varliklar (sembol, miktar, ortalama_maliyet, guncel_fiyat)
            VALUES (?, ?, ?, ?)
            ON CONFLICT(sembol) DO UPDATE SET
                miktar = excluded.miktar,
                ortalama_maliyet = excluded.ortalama_maliyet,
                guncel_fiyat = excluded.guncel_fiyat;
            """;

        try (Connection conn = baglantiAl();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, varlik.getSembol());
            pstmt.setDouble(2, varlik.getMiktar());
            pstmt.setDouble(3, varlik.getOrtalamaMaliyet());
            pstmt.setDouble(4, varlik.getGuncelFiyat());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Varlık kaydedilemedi: " + e.getMessage(), e);
        }
    }

    public void guncelle(Varlik varlik) {
        save(varlik);
    }

    public Optional<Varlik> findBySembol(String sembol) {
        String sql = "SELECT * FROM varliklar WHERE sembol = ?";

        try (Connection conn = baglantiAl();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sembol);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Varlik varlik = new Varlik(
                            rs.getString("sembol"),
                            rs.getDouble("miktar"),
                            rs.getDouble("ortalama_maliyet"),
                            rs.getDouble("guncel_fiyat")
                    );
                    varlik.setId(rs.getInt("id"));
                    return Optional.of(varlik);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Varlık sorgulanamadı: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Varlik> findAll() {
        List<Varlik> liste = new ArrayList<>();
        String sql = "SELECT * FROM varliklar";

        try (Connection conn = baglantiAl();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Varlik varlik = new Varlik(
                        rs.getString("sembol"),
                        rs.getDouble("miktar"),
                        rs.getDouble("ortalama_maliyet"),
                        rs.getDouble("guncel_fiyat")
                );
                varlik.setId(rs.getInt("id"));
                liste.add(varlik);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Varlıklar listelenemedi: " + e.getMessage(), e);
        }
        return liste;
    }

    public void delete(String sembol) {
        String sql = "DELETE FROM varliklar WHERE sembol = ?";

        try (Connection conn = baglantiAl();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sembol);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Varlık silinemedi: " + e.getMessage(), e);
        }
    }
}