package com.sanosysalvos.ms_geolocalizacion.service;

import com.sanosysalvos.ms_geolocalizacion.dto.UbicacionDTO;

import java.util.List;

public interface UbicacionService {
    UbicacionDTO registrar(UbicacionDTO dto);

    List<UbicacionDTO> listarPorMascota(Long idMascota);

    List<UbicacionDTO> listarParaMapa();
}
