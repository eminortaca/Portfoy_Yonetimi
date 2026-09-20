package com.emin.portfoy.service;

import com.emin.portfoy.models.Varlik;
import com.emin.portfoy.repository.VarlikRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PortfoyServiceTest {

    @Mock
    private VarlikRepository varlikRepository;

    @InjectMocks
    private PortfoyService service;

    @Test
    void toplamlarDogruHesaplanir() {
        when(varlikRepository.findAll()).thenReturn(List.of(
                new Varlik("THYAO", 10, 100, 120),
                new Varlik("XAUUSD", 1, 2000, 2200)
        ));

        assertEquals(3000.0, service.toplamMaliyet(), 0.0001);
        assertEquals(3400.0, service.toplamDeger(), 0.0001);
        assertEquals(400.0, service.toplamKarZarar(), 0.0001);
    }

    @Test
    void olmayanVarliktaFiyatGuncellemeHatasiVerir() {
        when(varlikRepository.findBySembol("ABC")).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.varlikFiyatiGuncelle("ABC", 10));
    }
}
