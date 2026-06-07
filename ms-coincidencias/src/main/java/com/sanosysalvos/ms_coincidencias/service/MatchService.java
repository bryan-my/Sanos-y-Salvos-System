package com.sanosysalvos.ms_coincidencias.service;

import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;

import java.util.List;

public interface MatchService {
    MatchDTO crearPendienteDesdeAvistamiento(Long idAvistamiento, Double porcentajeSimilitud);
    List<MatchDTO> listarPendientes();
    MatchDTO actualizarEstado(Long id, String nuevoEstado);
}
