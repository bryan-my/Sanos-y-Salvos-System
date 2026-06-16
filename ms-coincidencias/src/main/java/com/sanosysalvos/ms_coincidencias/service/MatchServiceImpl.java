package com.sanosysalvos.ms_coincidencias.service;

import com.sanosysalvos.ms_coincidencias.client.AvistamientoClient;
import com.sanosysalvos.ms_coincidencias.client.MascotaClient;
import com.sanosysalvos.ms_coincidencias.dto.AvistamientoExternoDTO;
import com.sanosysalvos.ms_coincidencias.dto.MascotaExternaDTO;
import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;
import com.sanosysalvos.ms_coincidencias.model.Match;
import com.sanosysalvos.ms_coincidencias.repository.MatchRepository;
import org.modelmapper.ModelMapper;
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
    private final MascotaClient mascotaClient;
    private final AvistamientoClient avistamientoClient;

    public MatchServiceImpl(MatchRepository matchRepository, ModelMapper modelMapper, MascotaClient mascotaClient, AvistamientoClient avistamientoClient) {
        this.matchRepository = matchRepository;
        this.modelMapper = modelMapper;
        this.mascotaClient = mascotaClient;
        this.avistamientoClient = avistamientoClient;
    }

    private MatchDTO enrichMatchDTO(MatchDTO matchDTO) {
        if (matchDTO.getIdMascotaPerdida() != null) {
            try {
                System.out.println("Calling MascotaClient for ID: " + matchDTO.getIdMascotaPerdida());
                MascotaExternaDTO mascota = mascotaClient.obtenerPorId(matchDTO.getIdMascotaPerdida());
                System.out.println("Got Mascota: " + mascota);
                matchDTO.setMascota(mascota);
            } catch (Exception e) {
                System.err.println("Error Feign Mascota ID " + matchDTO.getIdMascotaPerdida() + ": " + e.getMessage());
                e.printStackTrace();
                matchDTO.setMascota(null);
            }
        }

        if (matchDTO.getIdAvistamiento() != null) {
            try {
                System.out.println("Calling AvistamientoClient for ID: " + matchDTO.getIdAvistamiento());
                AvistamientoExternoDTO avistamiento = avistamientoClient.obtenerPorId(matchDTO.getIdAvistamiento());
                System.out.println("Got Avistamiento: " + avistamiento);
                matchDTO.setAvistamiento(avistamiento);
            } catch (Exception e) {
                System.err.println("Error Feign Avistamiento ID " + matchDTO.getIdAvistamiento() + ": " + e.getMessage());
                e.printStackTrace();
                matchDTO.setAvistamiento(null);
            }
        }

        return matchDTO;
    }

    @Override
    public MatchDTO crearPendienteDesdeAvistamiento(Long idAvistamiento, Double porcentajeSimilitud) {
        Match match = new Match();
        match.setIdAvistamiento(idAvistamiento);
        match.setPorcentajeSimilitud(porcentajeSimilitud);
        match.setEstado(ESTADO_PENDIENTE);

        Match guardado = matchRepository.save(match);
        MatchDTO matchDTO = modelMapper.map(guardado, MatchDTO.class);
        return enrichMatchDTO(matchDTO);
    }

    @Override
    public List<MatchDTO> listarPendientes() {
        return matchRepository.findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE)
                .stream()
                .map(m -> modelMapper.map(m, MatchDTO.class))
                .map(this::enrichMatchDTO)
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
        MatchDTO matchDTO = modelMapper.map(guardado, MatchDTO.class);
        return enrichMatchDTO(matchDTO);
    }
}
