package com.emin.portfoy.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VarlikTest {

    @Test
    void sembolBuyukHarfeCevrilir() {
        Varlik varlik = new Varlik("thyao", 1, 100, 120);
        assertEquals("THYAO", varlik.getSembol());
    }

    @Test
    void miktarSifirVeyaEksiOlamaz() {
        assertThrows(IllegalArgumentException.class, () -> new Varlik("THYAO", 0, 100, 120));
    }

    @Test
    void karZararDogruHesaplanir() {
        Varlik varlik = new Varlik("THYAO", 2, 100, 130);
        assertEquals(60.0, varlik.getKarZararDurumu(), 0.0001);
    }
}
