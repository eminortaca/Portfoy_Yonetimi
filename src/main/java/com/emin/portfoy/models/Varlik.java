package com.emin.portfoy.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "varliklar")
public class Varlik {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sembol;

    @Column(nullable = false)
    private double miktar;

    @Column(nullable = false)
    private double ortalamaMaliyet;

    @Column(nullable = false)
    private double guncelFiyat;

    public Varlik() {
    }

    public Varlik(String sembol, double miktar, double ortalamaMaliyet, double guncelFiyat) {
        setSembol(sembol);
        setMiktar(miktar);
        setOrtalamaMaliyet(ortalamaMaliyet);
        setGuncelFiyat(guncelFiyat);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("id eksi olamaz");
        }
        this.id = id;
    }

    public String getSembol() {
        return sembol;
    }

    public void setSembol(String sembol) {
        if (sembol == null || sembol.trim().isEmpty()) {
            throw new IllegalArgumentException("sembol boş olamaz");
        }
        this.sembol = sembol.trim().toUpperCase();
    }

    public double getMiktar() {
        return miktar;
    }

    public void setMiktar(double miktar) {
        if (miktar <= 0) {
            throw new IllegalArgumentException("miktar sıfırdan büyük olmalıdır");
        }
        this.miktar = miktar;
    }

    public double getOrtalamaMaliyet() {
        return ortalamaMaliyet;
    }

    public void setOrtalamaMaliyet(double ortalamaMaliyet) {
        if (ortalamaMaliyet < 0) {
            throw new IllegalArgumentException("ortalama maliyet eksi olamaz");
        }
        this.ortalamaMaliyet = ortalamaMaliyet;
    }

    public double getGuncelFiyat() {
        return guncelFiyat;
    }

    public void setGuncelFiyat(double guncelFiyat) {
        if (guncelFiyat < 0) {
            throw new IllegalArgumentException("güncel fiyat eksi olamaz");
        }
        this.guncelFiyat = guncelFiyat;
    }

    public double getKarZararDurumu() {
        double toplamMaliyet = this.miktar * this.ortalamaMaliyet;
        double toplamGuncelDeger = this.miktar * this.guncelFiyat;
        return toplamGuncelDeger - toplamMaliyet;
    }

    public double getToplamMaliyet() {
        return this.miktar * this.ortalamaMaliyet;
    }

    public double getToplamDeger() {
        return this.miktar * this.guncelFiyat;
    }
}
