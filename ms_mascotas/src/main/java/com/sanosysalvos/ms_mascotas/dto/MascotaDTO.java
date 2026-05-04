package com.sanosysalvos.ms_mascotas.dto;

import com.sanosysalvos.ms_mascotas.model.EstadoMascota;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MascotaDTO {
    private Long id;
    private String nombre;
    private String especie;
    private String raza;
    private String color;
    private String tamaño;
    private String fotoUrl;
    private String ultimaUbicacion;
    private String descripcion;
    private LocalDateTime fechaSuceso;
    private EstadoMascota estado;
    private Long idUsuario;
    private String fechaRegistro;
}