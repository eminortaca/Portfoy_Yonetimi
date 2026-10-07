package com.emin.portfoy.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    // Veritabanı yolumuz tek bir merkezde tanımlı
    private static final String DB_URL = "jdbc:sqlite:portfoy.db";

    // Sürekli açık kalacak olan tek bağlantımız
    private static Connection baglanti;

    // Her seferinde yeni bağlantı açmak yerine, var olan açık bağlantıyı verir
    public static Connection getBaglanti() throws SQLException {
        if (baglanti == null || baglanti.isClosed()) {
            baglanti = DriverManager.getConnection(DB_URL);
        }
        return baglanti;
    }
    // Program kapanırken bağlantıyı temelli kapatır
    public static void baglantiyiKapat() {
        try {
            if (baglanti != null && !baglanti.isClosed()) {
                baglanti.close();
            }
        } catch (SQLException e) {
            System.out.println("Veritabanı kapatılırken hata oluştu: " + e.getMessage());
        }
    }
}