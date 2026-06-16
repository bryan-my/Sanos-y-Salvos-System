package com.sanosysalvos.ms_coincidencias.dto;

import lombok.Data;

@Data
public class MascotaExternaDTO {
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String color;
    private String tamaño;
    private String fotoUrl;
    private String ultimaUbicacion;
    private String descripcion;
}
