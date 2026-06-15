package com.sanosysalvos.ms_mascotas.service;

import com.sanosysalvos.ms_mascotas.dto.MascotaDTO;
import com.sanosysalvos.ms_mascotas.exception.ResourceNotFoundException;
import com.sanosysalvos.ms_mascotas.model.EstadoMascota;
import com.sanosysalvos.ms_mascotas.model.Mascota;
import com.sanosysalvos.ms_mascotas.repository.MascotaRepository;
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
import static org.mockito.Mockito.*;

public class MascotaServiceTest {

    @Mock
    private MascotaRepository mascotaRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private MascotaService mascotaService;

    private static Mascota MASCOTA_TEST;
    private static MascotaDTO MASCOTA_DTO_TEST;
    private static MascotaDTO MASCOTA_DTO_ACTUALIZADO_TEST;
    private static Mascota MASCOTA_ACTUALIZADA_TEST;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        MASCOTA_TEST = new Mascota();
        MASCOTA_TEST.setId(1L);
        MASCOTA_TEST.setNombre("Rex");
        MASCOTA_TEST.setEspecie("Perro");
        MASCOTA_TEST.setRaza("Labrador");
        MASCOTA_TEST.setColor("Dorado");
        MASCOTA_TEST.setTamaño("Grande");
        MASCOTA_TEST.setFotoUrl("http://ejemplo.com/rex.jpg");
        MASCOTA_TEST.setUltimaUbicacion("Parque Central");
        MASCOTA_TEST.setDescripcion("Perro amigable");
        MASCOTA_TEST.setFechaSuceso(LocalDateTime.of(2024, 1, 1, 12, 0));
        MASCOTA_TEST.setEstado(EstadoMascota.PERDIDA);
        MASCOTA_TEST.setIdUsuario(1L);
        MASCOTA_TEST.setFechaRegistro(LocalDateTime.of(2024, 1, 1, 12, 0));

        MASCOTA_DTO_TEST = new MascotaDTO();
        MASCOTA_DTO_TEST.setId(1L);
        MASCOTA_DTO_TEST.setNombre("Rex");
        MASCOTA_DTO_TEST.setEspecie("Perro");
        MASCOTA_DTO_TEST.setRaza("Labrador");
        MASCOTA_DTO_TEST.setColor("Dorado");
        MASCOTA_DTO_TEST.setTamaño("Grande");
        MASCOTA_DTO_TEST.setFotoUrl("http://ejemplo.com/rex.jpg");
        MASCOTA_DTO_TEST.setUltimaUbicacion("Parque Central");
        MASCOTA_DTO_TEST.setDescripcion("Perro amigable");
        MASCOTA_DTO_TEST.setFechaSuceso(LocalDateTime.of(2024, 1, 1, 12, 0));
        MASCOTA_DTO_TEST.setEstado(EstadoMascota.PERDIDA);
        MASCOTA_DTO_TEST.setIdUsuario(1L);
        MASCOTA_DTO_TEST.setFechaRegistro("2024-01-01T12:00:00");

        MASCOTA_DTO_ACTUALIZADO_TEST = new MascotaDTO();
        MASCOTA_DTO_ACTUALIZADO_TEST.setId(1L);
        MASCOTA_DTO_ACTUALIZADO_TEST.setNombre("Rexy");
        MASCOTA_DTO_ACTUALIZADO_TEST.setEspecie("Perro");
        MASCOTA_DTO_ACTUALIZADO_TEST.setRaza("Golden Retriever");
        MASCOTA_DTO_ACTUALIZADO_TEST.setColor("Amarillo");
        MASCOTA_DTO_ACTUALIZADO_TEST.setTamaño("Mediano");
        MASCOTA_DTO_ACTUALIZADO_TEST.setFotoUrl("http://ejemplo.com/rexy.jpg");
        MASCOTA_DTO_ACTUALIZADO_TEST.setUltimaUbicacion("Plaza Principal");
        MASCOTA_DTO_ACTUALIZADO_TEST.setDescripcion("Perro muy amigable");
        MASCOTA_DTO_ACTUALIZADO_TEST.setFechaSuceso(LocalDateTime.of(2024, 2, 1, 12, 0));
        MASCOTA_DTO_ACTUALIZADO_TEST.setEstado(EstadoMascota.ENCONTRADA);
        MASCOTA_DTO_ACTUALIZADO_TEST.setIdUsuario(1L);
        MASCOTA_DTO_ACTUALIZADO_TEST.setFechaRegistro("2024-01-01T12:00:00");

        MASCOTA_ACTUALIZADA_TEST = new Mascota();
        MASCOTA_ACTUALIZADA_TEST.setId(1L);
        MASCOTA_ACTUALIZADA_TEST.setNombre("Rexy");
        MASCOTA_ACTUALIZADA_TEST.setEspecie("Perro");
        MASCOTA_ACTUALIZADA_TEST.setRaza("Golden Retriever");
        MASCOTA_ACTUALIZADA_TEST.setColor("Amarillo");
        MASCOTA_ACTUALIZADA_TEST.setTamaño("Mediano");
        MASCOTA_ACTUALIZADA_TEST.setFotoUrl("http://ejemplo.com/rexy.jpg");
        MASCOTA_ACTUALIZADA_TEST.setUltimaUbicacion("Plaza Principal");
        MASCOTA_ACTUALIZADA_TEST.setDescripcion("Perro muy amigable");
        MASCOTA_ACTUALIZADA_TEST.setFechaSuceso(LocalDateTime.of(2024, 2, 1, 12, 0));
        MASCOTA_ACTUALIZADA_TEST.setEstado(EstadoMascota.ENCONTRADA);
        MASCOTA_ACTUALIZADA_TEST.setIdUsuario(1L);
        MASCOTA_ACTUALIZADA_TEST.setFechaRegistro(LocalDateTime.of(2024, 1, 1, 12, 0));
    }

    @Test
    public void testCrearMascota() {
        // Arrange
        when(modelMapper.map(MASCOTA_DTO_TEST, Mascota.class)).thenReturn(MASCOTA_TEST);
        when(mascotaRepository.save(MASCOTA_TEST)).thenReturn(MASCOTA_TEST);
        when(modelMapper.map(MASCOTA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_TEST);

        // Act
        MascotaDTO resultado = mascotaService.crearMascota(MASCOTA_DTO_TEST);

        // Assert
        assertNotNull(resultado);
        assertEquals(MASCOTA_DTO_TEST.getId(), resultado.getId());
        assertEquals(MASCOTA_DTO_TEST.getNombre(), resultado.getNombre());
        verify(mascotaRepository, times(1)).save(MASCOTA_TEST);
    }

    @Test
    public void testObtenerTodos() {
        // Arrange
        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setNombre("Luna");
        mascota2.setEspecie("Gato");
        mascota2.setRaza("Siames");
        mascota2.setColor("Blanco");
        mascota2.setTamaño("Pequeño");
        mascota2.setEstado(EstadoMascota.EN_CASA);
        mascota2.setIdUsuario(2L);

        MascotaDTO mascotaDTO2 = new MascotaDTO();
        mascotaDTO2.setId(2L);
        mascotaDTO2.setNombre("Luna");
        mascotaDTO2.setEspecie("Gato");
        mascotaDTO2.setRaza("Siames");
        mascotaDTO2.setColor("Blanco");
        mascotaDTO2.setTamaño("Pequeño");
        mascotaDTO2.setEstado(EstadoMascota.EN_CASA);
        mascotaDTO2.setIdUsuario(2L);

        List<Mascota> mascotas = Arrays.asList(MASCOTA_TEST, mascota2);
        when(mascotaRepository.findAll()).thenReturn(mascotas);
        when(modelMapper.map(MASCOTA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_TEST);
        when(modelMapper.map(mascota2, MascotaDTO.class)).thenReturn(mascotaDTO2);

        // Act
        List<MascotaDTO> resultado = mascotaService.obtenerTodos();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findAll();
    }

    @Test
    public void testObtenerPorId() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(MASCOTA_TEST));
        when(modelMapper.map(MASCOTA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_TEST);

        // Act
        MascotaDTO resultado = mascotaService.obtenerPorId(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(MASCOTA_DTO_TEST.getId(), resultado.getId());
        verify(mascotaRepository, times(1)).findById(1L);
    }

    @Test
    public void testObtenerPorIdNotFound() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.obtenerPorId(1L);
        });
        assertEquals("Mascota no encontrada con id: 1", exception.getMessage());
        verify(mascotaRepository, times(1)).findById(1L);
    }

    @Test
    public void testObtenerPorUsuario() {
        // Arrange
        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setNombre("Luna");
        mascota2.setEspecie("Gato");
        mascota2.setIdUsuario(1L);

        MascotaDTO mascotaDTO2 = new MascotaDTO();
        mascotaDTO2.setId(2L);
        mascotaDTO2.setNombre("Luna");
        mascotaDTO2.setEspecie("Gato");
        mascotaDTO2.setIdUsuario(1L);

        List<Mascota> mascotas = Arrays.asList(MASCOTA_TEST, mascota2);
        when(mascotaRepository.findByIdUsuario(1L)).thenReturn(mascotas);
        when(modelMapper.map(MASCOTA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_TEST);
        when(modelMapper.map(mascota2, MascotaDTO.class)).thenReturn(mascotaDTO2);

        // Act
        List<MascotaDTO> resultado = mascotaService.obtenerPorUsuario(1L);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findByIdUsuario(1L);
    }

    @Test
    public void testObtenerPorEstado() {
        // Arrange
        Mascota mascota2 = new Mascota();
        mascota2.setId(2L);
        mascota2.setNombre("Luna");
        mascota2.setEspecie("Gato");
        mascota2.setEstado(EstadoMascota.PERDIDA);

        MascotaDTO mascotaDTO2 = new MascotaDTO();
        mascotaDTO2.setId(2L);
        mascotaDTO2.setNombre("Luna");
        mascotaDTO2.setEspecie("Gato");
        mascotaDTO2.setEstado(EstadoMascota.PERDIDA);

        List<Mascota> mascotas = Arrays.asList(MASCOTA_TEST, mascota2);
        when(mascotaRepository.findByEstado(EstadoMascota.PERDIDA)).thenReturn(mascotas);
        when(modelMapper.map(MASCOTA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_TEST);
        when(modelMapper.map(mascota2, MascotaDTO.class)).thenReturn(mascotaDTO2);

        // Act
        List<MascotaDTO> resultado = mascotaService.obtenerPorEstado(EstadoMascota.PERDIDA);

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mascotaRepository, times(1)).findByEstado(EstadoMascota.PERDIDA);
    }

    @Test
    public void testActualizarMascota() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(MASCOTA_TEST));
        when(mascotaRepository.save(any(Mascota.class))).thenReturn(MASCOTA_ACTUALIZADA_TEST);
        when(modelMapper.map(MASCOTA_ACTUALIZADA_TEST, MascotaDTO.class)).thenReturn(MASCOTA_DTO_ACTUALIZADO_TEST);

        // Act
        MascotaDTO resultado = mascotaService.actualizarMascota(1L, MASCOTA_DTO_ACTUALIZADO_TEST);

        // Assert
        assertNotNull(resultado);
        assertEquals(MASCOTA_DTO_ACTUALIZADO_TEST.getNombre(), resultado.getNombre());
        assertEquals(MASCOTA_DTO_ACTUALIZADO_TEST.getEstado(), resultado.getEstado());
        verify(mascotaRepository, times(1)).findById(1L);
        verify(mascotaRepository, times(1)).save(any(Mascota.class));
    }

    @Test
    public void testActualizarMascotaNotFound() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.actualizarMascota(1L, MASCOTA_DTO_ACTUALIZADO_TEST);
        });
        assertEquals("Mascota no encontrada con id: 1", exception.getMessage());
        verify(mascotaRepository, times(1)).findById(1L);
        verify(mascotaRepository, never()).save(any(Mascota.class));
    }

    @Test
    public void testEliminarMascota() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.of(MASCOTA_TEST));
        doNothing().when(mascotaRepository).delete(MASCOTA_TEST);

        // Act
        mascotaService.eliminarMascota(1L);

        // Assert
        verify(mascotaRepository, times(1)).findById(1L);
        verify(mascotaRepository, times(1)).delete(MASCOTA_TEST);
    }

    @Test
    public void testEliminarMascotaNotFound() {
        // Arrange
        when(mascotaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            mascotaService.eliminarMascota(1L);
        });
        assertEquals("Mascota no encontrada con id: 1", exception.getMessage());
        verify(mascotaRepository, times(1)).findById(1L);
        verify(mascotaRepository, never()).delete(any(Mascota.class));
    }

    @Test
    public void testEliminarPorUsuario() {
        // Arrange
        doNothing().when(mascotaRepository).deleteByIdUsuario(1L);

        // Act
        mascotaService.eliminarPorUsuario(1L);

        // Assert
        verify(mascotaRepository, times(1)).deleteByIdUsuario(1L);
    }
}
