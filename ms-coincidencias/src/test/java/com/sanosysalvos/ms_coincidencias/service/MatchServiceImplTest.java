package com.sanosysalvos.ms_coincidencias.service;

import com.sanosysalvos.ms_coincidencias.dto.MatchDTO;
import com.sanosysalvos.ms_coincidencias.model.Match;
import com.sanosysalvos.ms_coincidencias.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class MatchServiceImplTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MatchServiceImpl matchService;

    private static final LocalDateTime FECHA_FIJA = LocalDateTime.of(2024, 1, 1, 12, 0, 0);
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_APROBADO = "APROBADO";
    private static final String ESTADO_DESCARTADO = "DESCARTADO";

    private Match matchTest;
    private MatchDTO matchDTOTest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        matchTest = new Match();
        matchTest.setId(1L);
        matchTest.setIdAvistamiento(100L);
        matchTest.setPorcentajeSimilitud(85.5);
        matchTest.setEstado(ESTADO_PENDIENTE);
        matchTest.setFechaMatch(FECHA_FIJA);

        matchDTOTest = new MatchDTO();
        matchDTOTest.setId(1L);
        matchDTOTest.setIdAvistamiento(100L);
        matchDTOTest.setPorcentajeSimilitud(85.5);
        matchDTOTest.setEstado(ESTADO_PENDIENTE);
        matchDTOTest.setFechaMatch(FECHA_FIJA);
    }

    @Test
    void testCrearPendienteDesdeAvistamiento() {
        // Arrange
        when(matchRepository.save(any(Match.class))).thenReturn(matchTest);
        when(modelMapper.map(matchTest, MatchDTO.class)).thenReturn(matchDTOTest);

        // Act
        MatchDTO result = matchService.crearPendienteDesdeAvistamiento(100L, 85.5);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result.getIdAvistamiento());
        assertEquals(85.5, result.getPorcentajeSimilitud());
        assertEquals(ESTADO_PENDIENTE, result.getEstado());
        verify(matchRepository, times(1)).save(any(Match.class));
        verify(modelMapper, times(1)).map(matchTest, MatchDTO.class);
    }

    @Test
    void testListarPendientes() {
        // Arrange
        Match match2 = new Match();
        match2.setId(2L);
        match2.setEstado(ESTADO_PENDIENTE);
        match2.setFechaMatch(FECHA_FIJA.minusDays(1));

        MatchDTO dto2 = new MatchDTO();
        dto2.setId(2L);
        dto2.setEstado(ESTADO_PENDIENTE);

        when(matchRepository.findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE)).thenReturn(Arrays.asList(matchTest, match2));
        when(modelMapper.map(matchTest, MatchDTO.class)).thenReturn(matchDTOTest);
        when(modelMapper.map(match2, MatchDTO.class)).thenReturn(dto2);

        // Act
        List<MatchDTO> result = matchService.listarPendientes();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(matchRepository, times(1)).findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE);
    }

    @Test
    void testListarPendientesVacio() {
        // Arrange
        when(matchRepository.findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE)).thenReturn(Arrays.asList());

        // Act
        List<MatchDTO> result = matchService.listarPendientes();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(matchRepository, times(1)).findByEstadoOrderByFechaMatchDesc(ESTADO_PENDIENTE);
    }

    @Test
    void testActualizarEstadoSuccess() {
        // Arrange
        when(matchRepository.findById(1L)).thenReturn(Optional.of(matchTest));
        when(matchRepository.save(any(Match.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(modelMapper.map(any(Match.class), eq(MatchDTO.class))).thenAnswer(invocation -> {
            Match m = invocation.getArgument(0);
            MatchDTO dto = new MatchDTO();
            dto.setId(m.getId());
            dto.setEstado(m.getEstado());
            return dto;
        });

        // Act
        MatchDTO result = matchService.actualizarEstado(1L, ESTADO_APROBADO);

        // Assert
        assertNotNull(result);
        assertEquals(ESTADO_APROBADO, result.getEstado());
        verify(matchRepository, times(1)).findById(1L);
        verify(matchRepository, times(1)).save(any(Match.class));
    }

    @Test
    void testActualizarEstadoInvalid() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            matchService.actualizarEstado(1L, "INVALIDO");
        });
        assertEquals("Estado inválido: INVALIDO", exception.getMessage());
        verify(matchRepository, never()).findById(anyLong());
        verify(matchRepository, never()).save(any(Match.class));
    }

    @Test
    void testActualizarEstadoNotFound() {
        // Arrange
        when(matchRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(Exception.class, () -> {
            matchService.actualizarEstado(999L, ESTADO_DESCARTADO);
        });
        verify(matchRepository, times(1)).findById(999L);
        verify(matchRepository, never()).save(any(Match.class));
    }
}
