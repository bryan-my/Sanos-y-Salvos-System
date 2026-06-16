package com.sanosysalvos.ms_coincidencias.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvistamientoExternoDTO {
    private Long id;
    private String especie;
    private String descripcionFisica;
    private String fotoUrl;
    private Double latitud;
    private Double longitud;
}
