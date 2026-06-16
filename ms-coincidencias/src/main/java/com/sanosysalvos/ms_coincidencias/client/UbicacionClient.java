package com.sanosysalvos.ms_coincidencias.client;

import com.sanosysalvos.ms_coincidencias.dto.UbicacionExternaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-geolocalizacion")
public interface UbicacionClient {
    @GetMapping("/api/geolocalizacion/mascota/{idMascota}/ultima")
    ResponseEntity<UbicacionExternaDTO> obtenerUltimaPorMascota(@PathVariable("idMascota") Long idMascota);
}
