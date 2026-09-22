package com.emin.portfoy.service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KriptoServisi {

    private final HttpClient client = HttpClient.newHttpClient();

    public double guncelFiyatGetir(String sembol) {
        try {
            // Gelen sembolü Binance formatına çeviriyoruz (Örn: BTC -> BTCUSDT)
            String pair = sembol.toUpperCase().endsWith("USDT") ? sembol.toUpperCase() : sembol.toUpperCase() + "USDT";
            String url = "https://api.binance.com/api/v3/ticker/price?symbol=" + pair;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonObject jsonNesnesi = JsonParser.parseString(response.body()).getAsJsonObject();
                return jsonNesnesi.get("price").getAsDouble();
            } else {
                System.out.println("API Hatası: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Fiyat çekilemedi (" + sembol + "): " + e.getMessage());
        }
        return 0.0;
    }
}