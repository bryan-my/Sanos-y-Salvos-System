package com.sanosysalvos.ms_reportes.service;

import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;

import java.util.List;

public interface AvistamientoService {
    AvistamientoDTO crear(AvistamientoDTO dto);
    List<AvistamientoDTO> listarRecientes();
}
