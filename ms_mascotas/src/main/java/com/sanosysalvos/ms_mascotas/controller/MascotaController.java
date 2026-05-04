package com.sanosysalvos.ms_mascotas.controller;

import com.sanosysalvos.ms_mascotas.dto.MascotaDTO;
import com.sanosysalvos.ms_mascotas.model.EstadoMascota;
import com.sanosysalvos.ms_mascotas.service.MascotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO mascotaDTO) {
        return ResponseEntity.ok(mascotaService.crearMascota(mascotaDTO));
    }

    @GetMapping("/lista")
    public ResponseEntity<List<MascotaDTO>> listarTodos() {
        return ResponseEntity.ok(mascotaService.obtenerTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mascotaService.obtenerPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<MascotaDTO>> obtenerPorUsuario(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(mascotaService.obtenerPorUsuario(idUsuario));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<MascotaDTO>> obtenerPorEstado(@PathVariable EstadoMascota estado) {
        return ResponseEntity.ok(mascotaService.obtenerPorEstado(estado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MascotaDTO> actualizarMascota(@PathVariable Long id, @RequestBody MascotaDTO mascotaDTO) {
        return ResponseEntity.ok(mascotaService.actualizarMascota(id, mascotaDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMascota(@PathVariable Long id) {
        mascotaService.eliminarMascota(id);
        return ResponseEntity.noContent().build();
    }
}