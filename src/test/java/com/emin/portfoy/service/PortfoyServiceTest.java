package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PortfoyServiceTest {

    @Test
    void toplamlarDogruHesaplanir() {
        PortfoyService service = new PortfoyService(new VarlikRepository());
        service.varlikEkle(new Varlik("THYAO", 10, 100, 120));
        service.varlikEkle(new Varlik("XAUUSD", 1, 2000, 2200));

        assertEquals(3000.0, service.toplamMaliyet(), 0.0001);
        assertEquals(3400.0, service.toplamDeger(), 0.0001);
        assertEquals(400.0, service.toplamKarZarar(), 0.0001);
    }

    @Test
    void olmayanVarliktaFiyatGuncellemeHatasiVerir() {
        PortfoyService service = new PortfoyService(new VarlikRepository());
        assertThrows(IllegalArgumentException.class, () -> service.varlikFiyatiGuncelle("ABC", 10));
    }
}
