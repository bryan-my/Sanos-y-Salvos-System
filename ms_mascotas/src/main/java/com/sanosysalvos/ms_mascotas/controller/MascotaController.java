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

    private final MascotaService mascotaService;

    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    @PostMapping
    public ResponseEntity<MascotaDTO> crearMascota(@RequestBody MascotaDTO mascotaDTO) {
        System.out.println("[MascotaController] Recibida solicitud para crear mascota: " + mascotaDTO);
        MascotaDTO creada = mascotaService.crearMascota(mascotaDTO);
        System.out.println("[MascotaController] Mascota creada con ID: " + creada.getId());
        return ResponseEntity.ok(creada);
    }

    @GetMapping("/lista")
    public ResponseEntity<List<MascotaDTO>> listarTodos() {
        System.out.println("[MascotaController] Recibida solicitud para listar todas las mascotas");
        List<MascotaDTO> mascotas = mascotaService.obtenerTodos();
        System.out.println("[MascotaController] Mascotas encontradas: " + mascotas.size());
        return ResponseEntity.ok(mascotas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MascotaDTO> obtenerPorId(@PathVariable Long id) {
        System.out.println("[MascotaController] Recibida solicitud para mascota ID: " + id);
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

    @DeleteMapping("/usuario/{idUsuario}")
    public ResponseEntity<Void> eliminarPorUsuario(@PathVariable Long idUsuario) {
        mascotaService.eliminarPorUsuario(idUsuario);
        return ResponseEntity.noContent().build();
    }
}
