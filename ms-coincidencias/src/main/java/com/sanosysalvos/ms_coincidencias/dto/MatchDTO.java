package com.sanosysalvos.ms_coincidencias.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MatchDTO {
    private Long id;
    private Long idMascotaPerdida;
    private Long idAvistamiento;
    private Double porcentajeSimilitud;
    private String estado;
    private LocalDateTime fechaMatch;
    // Alias para compatibilidad con frontend
    public LocalDateTime getFecha() {
        return fechaMatch;
    }
    private MascotaExternaDTO mascota;
    private AvistamientoExternoDTO avistamiento;
}
