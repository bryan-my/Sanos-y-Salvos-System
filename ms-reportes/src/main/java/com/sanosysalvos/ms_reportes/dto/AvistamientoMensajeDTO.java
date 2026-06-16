package com.sanosysalvos.ms_reportes.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AvistamientoMensajeDTO {
    private Long id;
    private String especie;
    private String descripcionFisica;
}
