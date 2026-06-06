package com.sanosysalvos.ms_reportes.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AvistamientoDTO {
    private Long id;
    private String especie;
    private String descripcionFisica;
    private String fotoUrl;
    private Double latitud;
    private Double longitud;
    private String nombreReportador;
    private String telefonoContacto;
    private LocalDateTime fechaReporte;
}
