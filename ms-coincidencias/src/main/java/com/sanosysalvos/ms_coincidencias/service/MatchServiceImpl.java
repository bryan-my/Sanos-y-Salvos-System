package com.sanosysalvos.ms_coincidencias.service;

import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;
import com.sanosysalvos.ms_coincidencias.model.Match;
import com.sanosysalvos.ms_coincidencias.repository.MatchRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_DESCARTADO = "DESCARTADO";

    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;

    public MatchServiceImpl(MatchRepository matchRepository, ModelMapper modelMapper) {
        this.matchRepository = matchRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public MatchDTO crearPendienteDesdeAvistamiento(Long idAvistamiento, Double porcentajeSimilitud) {
        Match match = new Match();
        match.setIdAvistamiento(idAvistamiento);
        match.setPorcentajeSimilitud(porcentajeSimilitud);
        match.setEstado(ESTADO_PENDIENTE);

        Match guardado = matchRepository.save(match);
        return modelMapper.map(guardado, MatchDTO.class);
    }

    @Override
    public List<MatchDTO> listarPendientes() {
        return matchRepository.findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE)
                .stream()
                .map(m -> modelMapper.map(m, MatchDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public MatchDTO actualizarEstado(Long id, String nuevoEstado) {
        String estadoNormalizado = nuevoEstado == null ? null : nuevoEstado.trim().toUpperCase();
        if (!ESTADO_PENDIENTE.equals(estadoNormalizado) && !ESTADO_APROBADO.equals(estadoNormalizado) && !ESTADO_DESCARTADO.equals(estadoNormalizado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }

        Match match = matchRepository.findById(id).orElseThrow();
        match.setEstado(estadoNormalizado);
        Match guardado = matchRepository.save(match);
        return modelMapper.map(guardado, MatchDTO.class);
    }
}
