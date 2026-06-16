package com.sanosysalvos.ms_coincidencias.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionExternaDTO {
    private Long id;
    private Long idMascota;
    private Double latitud;
    private Double longitud;
    private String descripcionLugar;
    private LocalDateTime fechaRegistro;
}
