package com.sanosysalvos.ms_coincidencias.service;

import com.sanosysalvos.ms_coincidencias.client.AvistamientoClient;
import com.sanosysalvos.ms_coincidencias.client.MascotaClient;
import com.sanosysalvos.ms_coincidencias.client.UbicacionClient;
import com.sanosysalvos.ms_coincidencias.dto.AvistamientoExternoDTO;
import com.sanosysalvos.ms_coincidencias.dto.MascotaExternaDTO;
import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;
import com.sanosysalvos.ms_coincidencias.dto.UbicacionExternaDTO;
import com.sanosysalvos.ms_coincidencias.model.Match;
import com.sanosysalvos.ms_coincidencias.repository.MatchRepository;
import com.sanosysalvos.ms_coincidencias.util.GeospatialUtils;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_DESCARTADO = "DESCARTADO";
    private static final double MAX_RADIUS_KM = 10.0;

    private final MatchRepository matchRepository;
    private final ModelMapper modelMapper;
    private final MascotaClient mascotaClient;
    private final AvistamientoClient avistamientoClient;
    private final UbicacionClient ubicacionClient;

    public MatchServiceImpl(MatchRepository matchRepository, ModelMapper modelMapper, MascotaClient mascotaClient, AvistamientoClient avistamientoClient, UbicacionClient ubicacionClient) {
        this.matchRepository = matchRepository;
        this.modelMapper = modelMapper;
        this.mascotaClient = mascotaClient;
        this.avistamientoClient = avistamientoClient;
        this.ubicacionClient = ubicacionClient;
    }

    private MatchDTO enrichMatchDTO(MatchDTO matchDTO) {
        System.out.println("[enrichMatchDTO] INICIO - Match ID: " + matchDTO.getId());
        if (matchDTO.getIdMascotaPerdida() != null) {
            try {
                System.out.println("[enrichMatchDTO] Llamando a MascotaClient para ID: " + matchDTO.getIdMascotaPerdida());
                MascotaExternaDTO mascota = mascotaClient.obtenerPorId(matchDTO.getIdMascotaPerdida());
                System.out.println("[enrichMatchDTO] Mascota obtenida: " + mascota);
                matchDTO.setMascota(mascota);
            } catch (Exception e) {
                System.err.println("[enrichMatchDTO] ERROR al obtener mascota ID " + matchDTO.getIdMascotaPerdida());
                e.printStackTrace();
                matchDTO.setMascota(null);
            }
        } else {
            System.out.println("[enrichMatchDTO] No hay idMascotaPerdida");
        }

        if (matchDTO.getIdAvistamiento() != null) {
            try {
                System.out.println("[enrichMatchDTO] Llamando a AvistamientoClient para ID: " + matchDTO.getIdAvistamiento());
                AvistamientoExternoDTO avistamiento = avistamientoClient.obtenerPorId(matchDTO.getIdAvistamiento());
                System.out.println("[enrichMatchDTO] Avistamiento obtenido: " + avistamiento);
                matchDTO.setAvistamiento(avistamiento);
            } catch (Exception e) {
                System.err.println("[enrichMatchDTO] ERROR al obtener avistamiento ID " + matchDTO.getIdAvistamiento());
                e.printStackTrace();
                matchDTO.setAvistamiento(null);
            }
        } else {
            System.out.println("[enrichMatchDTO] No hay idAvistamiento");
        }

        System.out.println("[enrichMatchDTO] FIN - Match ID: " + matchDTO.getId());
        return matchDTO;
    }

    @Override
    public MatchDTO crearPendienteDesdeAvistamiento(Long idAvistamiento, Double porcentajeSimilitud) {
        System.out.println("[crearPendienteDesdeAvistamiento] Iniciando para Avistamiento ID: " + idAvistamiento);
        Match match = new Match();
        match.setIdAvistamiento(idAvistamiento);
        match.setPorcentajeSimilitud(porcentajeSimilitud);
        match.setEstado(ESTADO_PENDIENTE);

        Match guardado = matchRepository.save(match);
        System.out.println("[crearPendienteDesdeAvistamiento] Match guardado con ID: " + guardado.getId());
        MatchDTO matchDTO = modelMapper.map(guardado, MatchDTO.class);
        return enrichMatchDTO(matchDTO);
    }

    @Override
    public List<MatchDTO> listarPendientes() {
        System.out.println("[listarPendientes] INICIO");
        List<Match> matchesFromDb = matchRepository.findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE);
        System.out.println("[listarPendientes] Matches pendientes en BD: " + matchesFromDb.size());
        for (Match m : matchesFromDb) {
            System.out.println("  - Match ID: " + m.getId());
        }
        
        List<MatchDTO> result = matchesFromDb.stream()
                .map(m -> {
                    System.out.println("[listarPendientes] Mapeando Match ID: " + m.getId());
                    return modelMapper.map(m, MatchDTO.class);
                })
                .map(this::enrichMatchDTO)
                .collect(Collectors.toList());
        
        System.out.println("[listarPendientes] FIN - Resultado: " + result.size() + " matches");
        return result;
    }

    @Override
    public MatchDTO actualizarEstado(Long id, String nuevoEstado) {
        System.out.println("[actualizarEstado] Actualizando Match ID " + id + " a " + nuevoEstado);
        String estadoNormalizado = nuevoEstado == null ? null : nuevoEstado.trim().toUpperCase();
        if (!ESTADO_PENDIENTE.equals(estadoNormalizado) && !ESTADO_APROBADO.equals(estadoNormalizado) && !ESTADO_DESCARTADO.equals(estadoNormalizado)) {
            throw new IllegalArgumentException("Estado inválido: " + nuevoEstado);
        }

        Match match = matchRepository.findById(id).orElseThrow();
        match.setEstado(estadoNormalizado);
        Match guardado = matchRepository.save(match);
        System.out.println("[actualizarEstado] Estado actualizado correctamente a " + guardado.getEstado());
        MatchDTO matchDTO = modelMapper.map(guardado, MatchDTO.class);
        return enrichMatchDTO(matchDTO);
    }

    @Override
    public void procesarNuevoAvistamiento(Long idAvistamiento) {
        System.out.println("==============================================");
        System.out.println("[procesarNuevoAvistamiento] INICIANDO");
        System.out.println("[procesarNuevoAvistamiento] Avistamiento ID: " + idAvistamiento);
        System.out.println("==============================================");
        
        try {
            System.out.println("[procesarNuevoAvistamiento] Paso 1: Obtener avistamiento");
            AvistamientoExternoDTO avistamiento = avistamientoClient.obtenerPorId(idAvistamiento);
            System.out.println("[procesarNuevoAvistamiento] Avistamiento completo: " + avistamiento);
            
            if (avistamiento == null) {
                System.err.println("[procesarNuevoAvistamiento] ERROR: Avistamiento es null");
                return;
            }
            
            if (avistamiento.getLatitud() == null || avistamiento.getLongitud() == null) {
                System.err.println("[procesarNuevoAvistamiento] ERROR: Coordenadas null - Lat: " + avistamiento.getLatitud() + ", Lon: " + avistamiento.getLongitud());
                return;
            }

            System.out.println("[procesarNuevoAvistamiento] Coordenadas avistamiento: Lat=" + avistamiento.getLatitud() + ", Lon=" + avistamiento.getLongitud());
            
            System.out.println("[procesarNuevoAvistamiento] Paso 2: Obtener todas las mascotas");
            List<MascotaExternaDTO> todasMascotas = mascotaClient.obtenerTodos();
            System.out.println("[procesarNuevoAvistamiento] Total mascotas: " + todasMascotas.size());
            
            List<MascotaExternaDTO> mascotasPerdidas = todasMascotas.stream()
                    .filter(m -> {
                        boolean isPerdida = "PERDIDA".equalsIgnoreCase(m.getEstado());
                        System.out.println("[procesarNuevoAvistamiento] Mascota ID " + m.getId() + " - Estado: " + m.getEstado() + " - Perdida? " + isPerdida);
                        return isPerdida;
                    })
                    .collect(Collectors.toList());

            System.out.println("[procesarNuevoAvistamiento] Mascotas perdidas: " + mascotasPerdidas.size());

            for (MascotaExternaDTO mascota : mascotasPerdidas) {
                System.out.println("------------------------------------------");
                System.out.println("[procesarNuevoAvistamiento] Evaluando Mascota ID: " + mascota.getId() + " (" + mascota.getNombre() + ")");
                
                try {
                    System.out.println("[procesarNuevoAvistamiento] Paso 3: Obtener ubicacion de mascota ID " + mascota.getId());
                    ResponseEntity<UbicacionExternaDTO> ubicacionResponse =
                            ubicacionClient.obtenerUltimaPorMascota(mascota.getId());
                    if (!ubicacionResponse.getStatusCode().is2xxSuccessful()) {
                        System.out.println("[procesarNuevoAvistamiento] Saltando: sin ubicacion registrada para mascota ID " + mascota.getId());
                        continue;
                    }

                    UbicacionExternaDTO ubicacionMascota = ubicacionResponse.getBody();
                    System.out.println("[procesarNuevoAvistamiento] Ubicacion obtenida: " + ubicacionMascota);

                    if (ubicacionMascota == null
                            || ubicacionMascota.getLatitud() == null
                            || ubicacionMascota.getLongitud() == null) {
                        System.out.println("[procesarNuevoAvistamiento] Saltando: ubicacion o coordenadas null");
                        continue;
                    }

                    double latitudMascota = ubicacionMascota.getLatitud();
                    double longitudMascota = ubicacionMascota.getLongitud();

                    System.out.println("[procesarNuevoAvistamiento] Coordenadas mascota: Lat=" + latitudMascota + ", Lon=" + longitudMascota);
                    System.out.println("[procesarNuevoAvistamiento] Paso 4: Calcular distancia");

                    double distancia = GeospatialUtils.calculateDistance(
                            avistamiento.getLatitud(),
                            avistamiento.getLongitud(),
                            latitudMascota,
                            longitudMascota);
                    
                    System.out.println("[procesarNuevoAvistamiento] Distancia calculada: " + distancia + " km");
                    System.out.println("[procesarNuevoAvistamiento] Radio máximo: " + MAX_RADIUS_KM + " km");

                    if (distancia <= MAX_RADIUS_KM) {
                        System.out.println("[procesarNuevoAvistamiento] ¡¡¡MATCH ENCONTRADO!!! Distancia OK");
                        
                        double porcentajeSimilitud = 100 - (distancia * 5);
                        porcentajeSimilitud = Math.max(0, Math.min(100, porcentajeSimilitud));
                        System.out.println("[procesarNuevoAvistamiento] Porcentaje similitud: " + porcentajeSimilitud + "%");

                        Match match = new Match();
                        match.setIdMascotaPerdida(mascota.getId());
                        match.setIdAvistamiento(idAvistamiento);
                        match.setPorcentajeSimilitud(porcentajeSimilitud);
                        match.setEstado(ESTADO_PENDIENTE);
                        
                        Match matchGuardado = matchRepository.save(match);
                        System.out.println("[procesarNuevoAvistamiento] Match GUARDADO EXITOSAMENTE con ID: " + matchGuardado.getId());
                    } else {
                        System.out.println("[procesarNuevoAvistamiento] No hay match: distancia > radio máximo");
                    }
                } catch (Exception e) {
                    System.err.println("[procesarNuevoAvistamiento] ERROR al procesar Mascota ID " + mascota.getId());
                    e.printStackTrace();
                    continue;
                }
                System.out.println("------------------------------------------");
            }
        } catch (Exception e) {
            System.err.println("[procesarNuevoAvistamiento] ERROR GLOBAL");
            e.printStackTrace();
        }
        
        System.out.println("==============================================");
        System.out.println("[procesarNuevoAvistamiento] FINALIZADO");
        System.out.println("==============================================");
    }
}
