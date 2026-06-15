package com.sanosysalvos.ms_reportes.service;

import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;
import com.sanosysalvos.ms_reportes.model.Avistamiento;
import com.sanosysalvos.ms_reportes.repository.AvistamientoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AvistamientoServiceImplTest {

    @Mock
    private AvistamientoRepository avistamientoRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private AvistamientoServiceImpl avistamientoService;

    
    private final LocalDateTime fechaFija = LocalDateTime.of(2026, Month.JUNE, 15, 12, 0);

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCrearExitosamente() {
        // Arrange
        AvistamientoDTO dto = new AvistamientoDTO();
        dto.setEspecie("Perro");
        dto.setDescripcionFisica("Perro negro de tamaño mediano");
        dto.setLatitud(40.7128);
        dto.setLongitud(-74.0060);
        dto.setNombreReportador("Juan Pérez");
        dto.setTelefonoContacto("123456789");

        Avistamiento avistamiento = new Avistamiento();
        avistamiento.setEspecie("Perro");
        avistamiento.setDescripcionFisica("Perro negro de tamaño mediano");
        avistamiento.setLatitud(40.7128);
        avistamiento.setLongitud(-74.0060);
        avistamiento.setNombreReportador("Juan Pérez");
        avistamiento.setTelefonoContacto("123456789");
        avistamiento.setFechaReporte(fechaFija);

        Avistamiento guardado = new Avistamiento();
        guardado.setId(1L);
        guardado.setEspecie("Perro");
        guardado.setDescripcionFisica("Perro negro de tamaño mediano");
        guardado.setLatitud(40.7128);
        guardado.setLongitud(-74.0060);
        guardado.setNombreReportador("Juan Pérez");
        guardado.setTelefonoContacto("123456789");
        guardado.setFechaReporte(fechaFija);

        AvistamientoDTO guardadoDto = new AvistamientoDTO();
        guardadoDto.setId(1L);
        guardadoDto.setEspecie("Perro");
        guardadoDto.setDescripcionFisica("Perro negro de tamaño mediano");
        guardadoDto.setLatitud(40.7128);
        guardadoDto.setLongitud(-74.0060);
        guardadoDto.setNombreReportador("Juan Pérez");
        guardadoDto.setTelefonoContacto("123456789");
        guardadoDto.setFechaReporte(fechaFija);

        when(modelMapper.map(dto, Avistamiento.class)).thenReturn(avistamiento);
        when(avistamientoRepository.save(avistamiento)).thenReturn(guardado);
        when(modelMapper.map(guardado, AvistamientoDTO.class)).thenReturn(guardadoDto);

     
        AvistamientoDTO resultado = avistamientoService.crear(dto);

       
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("Perro", resultado.getEspecie());
        verify(avistamientoRepository, times(1)).save(avistamiento);
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), eq(guardadoDto));
    }

    @Test
    public void testListarRecientes() {
      
        Avistamiento avistamiento1 = new Avistamiento();
        avistamiento1.setId(1L);
        avistamiento1.setEspecie("Perro");
        avistamiento1.setFechaReporte(fechaFija.minusDays(1));

        Avistamiento avistamiento2 = new Avistamiento();
        avistamiento2.setId(2L);
        avistamiento2.setEspecie("Gato");
        avistamiento2.setFechaReporte(fechaFija);

        List<Avistamiento> avistamientos = Arrays.asList(avistamiento2, avistamiento1);

        AvistamientoDTO dto1 = new AvistamientoDTO();
        dto1.setId(1L);
        dto1.setEspecie("Perro");
        dto1.setFechaReporte(fechaFija.minusDays(1));

        AvistamientoDTO dto2 = new AvistamientoDTO();
        dto2.setId(2L);
        dto2.setEspecie("Gato");
        dto2.setFechaReporte(fechaFija);

        when(avistamientoRepository.findAllByOrderByFechaReporteDesc()).thenReturn(avistamientos);
        when(modelMapper.map(avistamiento2, AvistamientoDTO.class)).thenReturn(dto2);
        when(modelMapper.map(avistamiento1, AvistamientoDTO.class)).thenReturn(dto1);

       
        List<AvistamientoDTO> resultado = avistamientoService.listarRecientes();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Gato", resultado.get(0).getEspecie());
        assertEquals("Perro", resultado.get(1).getEspecie());
        verify(avistamientoRepository, times(1)).findAllByOrderByFechaReporteDesc();
    }

    @Test
    public void testListarRecientesVacio() {
     
        when(avistamientoRepository.findAllByOrderByFechaReporteDesc()).thenReturn(Arrays.asList());

      
        List<AvistamientoDTO> resultado = avistamientoService.listarRecientes();

       
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(avistamientoRepository, times(1)).findAllByOrderByFechaReporteDesc();
    }
}