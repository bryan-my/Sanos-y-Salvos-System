package com.sanosysalvos.ms_geolocalizacion.controller;

import com.sanosysalvos.ms_geolocalizacion.dto.UbicacionDTO;
import com.sanosysalvos.ms_geolocalizacion.service.UbicacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/geolocalizacion")
public class UbicacionController {

    @Autowired
    private UbicacionService ubicacionService;

    @PostMapping("/registrar")
    public ResponseEntity<UbicacionDTO> registrar(@RequestBody UbicacionDTO dto) {
        return ResponseEntity.ok(ubicacionService.registrar(dto));
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<UbicacionDTO>> listarPorMascota(@PathVariable Long idMascota) {
        return ResponseEntity.ok(ubicacionService.listarPorMascota(idMascota));
    }

    @GetMapping("/mapa")
    public ResponseEntity<List<UbicacionDTO>> listarParaMapa() {
        return ResponseEntity.ok(ubicacionService.listarParaMapa());
    }
}
