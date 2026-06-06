package com.sanosysalvos.ms_reportes.repository;

import com.sanosysalvos.ms_reportes.model.Avistamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvistamientoRepository extends JpaRepository<Avistamiento, Long> {
    List<Avistamiento> findAllByOrderByFechaReporteDesc();
}
