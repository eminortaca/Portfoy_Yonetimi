package com.emin.portfoy.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KriptoServisi {

    public void bitcoinFiyatiniGetir() {
        try {
            // Binance herkese açık, ücretsiz fiyat API'si
            String url = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT";

            // İstek (Request) oluşturma
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            // İsteği gönderip yanıtı (Response) alma
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // Dönen ham JSON metnini ayrıştırma (Parse işlemi)
            JsonObject jsonNesnesi = JsonParser.parseString(response.body()).getAsJsonObject();
            String sembol = jsonNesnesi.get("symbol").getAsString();
            String fiyat = jsonNesnesi.get("price").getAsString();

            System.out.println("Canlı Kripto Verisi -> Sembol: " + sembol + ", Fiyat: " + fiyat);

        } catch (Exception e) {
            System.out.println("Veri çekilirken bir sorun yaşandı: " + e.getMessage());
        }
    }
}