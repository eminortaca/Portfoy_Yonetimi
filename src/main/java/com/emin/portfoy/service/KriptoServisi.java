package com.emin.portfoy.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class KriptoServisi {

    private final HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1) // yurtta internet sıkıntı olduğu için HTTP/1.1 kullanıyoruz
            .build();

    // YENİ METOT: Tüm piyasayı tek bir hamlede çeker ve haritaya (Map) dönüştürür.
    public Map<String, Double> tumFiyatlariGetir() {
        Map<String, Double> fiyatHaritasi = new HashMap<>();
        try {
            // URL'nin sonundan "?symbol=..." kısmını sildik. Artık tüm listeyi getirecek.
            String url = "https://api.binance.com/api/v3/ticker/price";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Gelen veri artık tek bir obje değil, devasa bir dizi (Array)
                JsonArray jsonDizisi = JsonParser.parseString(response.body()).getAsJsonArray();

                for (JsonElement eleman : jsonDizisi) {
                    String sembol = eleman.getAsJsonObject().get("symbol").getAsString();
                    double fiyat = eleman.getAsJsonObject().get("price").getAsDouble();

                    if (sembol.endsWith("USDT")) {
                        String temizSembol = sembol.replace("USDT", "");
                        fiyatHaritasi.put(temizSembol, fiyat);
                    }
                }
            } else {
                System.out.println("API Hatası: " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("Toplu fiyat çekilemedi: " + e.getMessage());
        }
        return fiyatHaritasi;
    }
}