package com.sanosysalvos.ms_coincidencias.client;

import com.sanosysalvos.ms_coincidencias.dto.AvistamientoExternoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-reportes")
public interface AvistamientoClient {
    @GetMapping("/api/reportes/{id}")
    AvistamientoExternoDTO obtenerPorId(@PathVariable("id") Long id);
}
