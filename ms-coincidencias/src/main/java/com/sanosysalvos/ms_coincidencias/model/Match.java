package com.sanosysalvos.ms_coincidencias.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "matches")
@Data
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long idMascotaPerdida;
    private Long idAvistamiento;
    private Double porcentajeSimilitud;
    private String estado;
    private LocalDateTime fechaMatch;

    @PrePersist
    public void prePersist() {
        if (fechaMatch == null) {
            fechaMatch = LocalDateTime.now();
        }
    }
}
