package com.sanosysalvos.ms_coincidencias.repository;

import com.sanosysalvos.ms_coincidencias.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByEstadoOrderByFechaMatchDesc(String estado);
}
