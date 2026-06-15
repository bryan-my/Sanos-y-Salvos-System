package com.sanosysalvos.ms_reportes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId; // Importación añadida

@Entity
@Table(name = "avistamientos")
@Data
public class Avistamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String especie;
    private String descripcionFisica;
    private String fotoUrl;
    private Double latitud;
    private Double longitud;
    private String nombreReportador;
    private String telefonoContacto;
    private LocalDateTime fechaReporte;

    @PrePersist
    public void prePersist() {
        if (fechaReporte == null) {
            // Uso de ZoneId
            fechaReporte = LocalDateTime.now(ZoneId.systemDefault());
        }
    }
}