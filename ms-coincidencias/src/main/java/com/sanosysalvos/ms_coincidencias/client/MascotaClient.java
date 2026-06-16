package com.sanosysalvos.ms_coincidencias.client;

import com.sanosysalvos.ms_coincidencias.dto.MascotaExternaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-mascotas")
public interface MascotaClient {
    @GetMapping("/api/mascotas/{id}")
    MascotaExternaDTO obtenerPorId(@PathVariable("id") Long id);
}
