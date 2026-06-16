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
        System.out.println("[UbicacionController] Recibida solicitud para registrar ubicación: " + dto);
        UbicacionDTO registrada = ubicacionService.registrar(dto);
        System.out.println("[UbicacionController] Ubicación registrada con ID: " + registrada.getId());
        return ResponseEntity.ok(registrada);
    }

    @GetMapping("/mascota/{idMascota}")
    public ResponseEntity<List<UbicacionDTO>> listarPorMascota(@PathVariable Long idMascota) {
        System.out.println("[UbicacionController] Recibida solicitud para listar ubicaciones de mascota ID: " + idMascota);
        return ResponseEntity.ok(ubicacionService.listarPorMascota(idMascota));
    }

    @GetMapping("/mascota/{idMascota}/ultima")
    public ResponseEntity<UbicacionDTO> obtenerUltimaPorMascota(@PathVariable Long idMascota) {
        System.out.println("[UbicacionController] Recibida solicitud para última ubicación de mascota ID: " + idMascota);
        return ubicacionService.obtenerUltimaPorMascota(idMascota)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/mapa")
    public ResponseEntity<List<UbicacionDTO>> listarParaMapa() {
        return ResponseEntity.ok(ubicacionService.listarParaMapa());
    }
}
