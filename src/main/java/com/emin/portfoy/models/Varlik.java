package com.emin.portfoy.models;

public class Varlik {

    private int id;
    private String sembol;          // Örn: "THYAO", "TLY", "XAUUSD"
    private double miktar;          // Kaç lot/adet alındığı
    private double ortalamaMaliyet; // Alış fiyatı
    private double guncelFiyat;     // Şu anki piyasa değeri

    public Varlik() {}

    public Varlik(String sembol, double miktar, double ortalamaMaliyet, double guncelFiyat) {
        this.sembol = sembol;
        this.miktar = miktar;
        this.ortalamaMaliyet = ortalamaMaliyet;
        this.guncelFiyat = guncelFiyat;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSembol() { return sembol; }
    public void setSembol(String sembol) { this.sembol = sembol; }

    public double getMiktar() { return miktar; }

    public void setMiktar(double miktar) {
        if (miktar >= 0) {
            this.miktar = miktar;
        } else {
            System.out.println("Hata: Miktar eksi olamaz!");
        }
    }

    public double getOrtalamaMaliyet() { return ortalamaMaliyet; }
    public void setOrtalamaMaliyet(double ortalamaMaliyet) { this.ortalamaMaliyet = ortalamaMaliyet; }

    public double getGuncelFiyat() { return guncelFiyat; }
    public void setGuncelFiyat(double guncelFiyat) { this.guncelFiyat = guncelFiyat; }

    // Kâr/Zarar Hesaplama Metodu (Business kuralı - Read Only)
    public double getKarZararDurumu() {
        double toplamMaliyet = this.miktar * this.ortalamaMaliyet;
        double toplamGuncelDeger = this.miktar * this.guncelFiyat;
        return toplamGuncelDeger - toplamMaliyet;
    }
}