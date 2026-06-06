package com.sanosysalvos.ms_reportes.controller;

import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;
import com.sanosysalvos.ms_reportes.service.AvistamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class AvistamientoController {

    @Autowired
    private AvistamientoService avistamientoService;

    @PostMapping("/avistamiento")
    public ResponseEntity<AvistamientoDTO> crearAvistamiento(@RequestBody AvistamientoDTO dto) {
        return ResponseEntity.ok(avistamientoService.crear(dto));
    }

    @GetMapping("/recientes")
    public ResponseEntity<List<AvistamientoDTO>> listarRecientes() {
        return ResponseEntity.ok(avistamientoService.listarRecientes());
    }
}
