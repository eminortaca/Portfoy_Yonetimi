package com.emin.portfoy.models;

public class Varlik {

    private int id;
    private String sembol;
    private double miktar;
    private double ortalamaMaliyet;
    private double guncelFiyat;

    public Varlik() {
    }

    public Varlik(String sembol, double miktar, double ortalamaMaliyet, double guncelFiyat) {
        setSembol(sembol);
        setMiktar(miktar);
        setOrtalamaMaliyet(ortalamaMaliyet);
        setGuncelFiyat(guncelFiyat);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
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
