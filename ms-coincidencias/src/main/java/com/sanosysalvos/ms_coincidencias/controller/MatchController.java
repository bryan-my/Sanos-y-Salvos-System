package com.sanosysalvos.ms_coincidencias.controller;

import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;
import com.sanosysalvos.ms_coincidencias.model.Match;
import com.sanosysalvos.ms_coincidencias.repository.MatchRepository;
import com.sanosysalvos.ms_coincidencias.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/coincidencias")
public class MatchController {

    private final MatchService matchService;
    private final MatchRepository matchRepository;

    public MatchController(MatchService matchService, MatchRepository matchRepository) {
        this.matchService = matchService;
        this.matchRepository = matchRepository;
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<MatchDTO>> listarPendientes() {
        return ResponseEntity.ok(matchService.listarPendientes());
    }

    @GetMapping("/debug/all")
    public ResponseEntity<List<Match>> listarTodosMatches() {
        System.out.println("[MatchController] Devolviendo todos los matches para debug");
        List<Match> matches = matchRepository.findAll();
        System.out.println("[MatchController] Matches encontrados en BD: " + matches.size());
        for (Match m : matches) {
            System.out.println("  - Match ID: " + m.getId() + 
                               ", Estado: " + m.getEstado() + 
                               ", Mascota: " + m.getIdMascotaPerdida() + 
                               ", Avistamiento: " + m.getIdAvistamiento());
        }
        return ResponseEntity.ok(matches);
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<MatchDTO> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(matchService.actualizarEstado(id, nuevoEstado));
    }
}
