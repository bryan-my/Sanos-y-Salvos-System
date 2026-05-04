package com.sanosysalvos.ms_mascotas.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "mascotas")
@Data
public class Mascota {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String especie;
    
    private String raza;
    
    @Column(nullable = false)
    private String color;
    
    @Column(nullable = false)
    private String tamaño;
    
    private String fotoUrl;
    private String ultimaUbicacion;
    private String descripcion;
    private LocalDateTime fechaSuceso;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMascota estado;

    @Column(nullable = false)
    private Long idUsuario;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro = LocalDateTime.now();
}