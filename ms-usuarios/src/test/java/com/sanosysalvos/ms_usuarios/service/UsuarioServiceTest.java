package com.sanosysalvos.ms_usuarios.service;

import com.sanosysalvos.ms_usuarios.dto.UsuarioDTO;
import com.sanosysalvos.ms_usuarios.model.UserRole;
import com.sanosysalvos.ms_usuarios.model.Usuario;
import com.sanosysalvos.ms_usuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UsuarioService usuarioService;

    private static final String MASCOTAS_URL = "http://test-mascotas";

    private Usuario usuarioTest;
    private UsuarioDTO usuarioDTOTest;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        usuarioTest = Usuario.builder()
                .id(1L)
                .nombreCompleto("Juan Perez")
                .email("juan@test.com")
                .password("password123")
                .telefono("123456789")
                .direccion("Calle Falsa 123")
                .rol(UserRole.DUEÑO)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuarioDTOTest = new UsuarioDTO();
        usuarioDTOTest.setId(1L);
        usuarioDTOTest.setNombreCompleto("Juan Perez");
        usuarioDTOTest.setEmail("juan@test.com");
        usuarioDTOTest.setPassword("password123");
        usuarioDTOTest.setTelefono("123456789");
        usuarioDTOTest.setDireccion("Calle Falsa 123");
        usuarioDTOTest.setRol("DUEÑO");
    }

    @Test
    public void testCrearUsuario() {
        // Arrange
        when(modelMapper.map(usuarioDTOTest, Usuario.class)).thenReturn(usuarioTest);
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
        when(modelMapper.map(usuarioTest, UsuarioDTO.class)).thenReturn(usuarioDTOTest);

        // Act
        UsuarioDTO result = usuarioService.crearUsuario(usuarioDTOTest);

        // Assert
        assertNotNull(result);
        assertEquals(usuarioDTOTest.getEmail(), result.getEmail());
        assertEquals(usuarioDTOTest.getNombreCompleto(), result.getNombreCompleto());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testObtenerTodos() {
        // Arrange
        Usuario usuario2 = Usuario.builder()
                .id(2L)
                .nombreCompleto("Maria Lopez")
                .email("maria@test.com")
                .password("password456")
                .rol(UserRole.CLINICA)
                .build();
        List<Usuario> usuarios = Arrays.asList(usuarioTest, usuario2);
        when(usuarioRepository.findAll()).thenReturn(usuarios);

        // Act
        List<Usuario> result = usuarioService.obtenerTodos();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(usuarioRepository, times(1)).findAll();
    }

    @Test
    public void testBuscarPorEmail() {
        // Arrange
        when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuarioTest));
        when(modelMapper.map(usuarioTest, UsuarioDTO.class)).thenReturn(usuarioDTOTest);

        // Act
        UsuarioDTO result = usuarioService.buscarPorEmail("juan@test.com");

        // Assert
        assertNotNull(result);
        assertEquals(usuarioDTOTest.getEmail(), result.getEmail());
        verify(usuarioRepository, times(1)).findByEmail("juan@test.com");
    }

    @Test
    public void testBuscarPorEmailNotFound() {
        // Arrange
        when(usuarioRepository.findByEmail("noexiste@test.com")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.buscarPorEmail("noexiste@test.com");
        });
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).findByEmail("noexiste@test.com");
    }

    @Test
    public void testEliminarUsuario() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doNothing().when(restTemplate).delete(anyString());
        doNothing().when(usuarioRepository).deleteById(1L);

        // Act
        usuarioService.eliminarUsuario(1L);

        // Assert
        verify(usuarioRepository, times(1)).existsById(1L);
        verify(restTemplate, times(1)).delete(anyString());
        verify(usuarioRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testEliminarUsuarioNotFound() {
        // Arrange
        when(usuarioRepository.existsById(99L)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(99L);
        });
        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(usuarioRepository, times(1)).existsById(99L);
        verify(restTemplate, never()).delete(anyString());
    }

    @Test
    public void testEliminarUsuarioRestTemplateFails() {
        // Arrange
        when(usuarioRepository.existsById(1L)).thenReturn(true);
        doThrow(new RuntimeException("Connection error")).when(restTemplate).delete(anyString());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            usuarioService.eliminarUsuario(1L);
        });
        assertEquals("No se pudieron eliminar las mascotas del usuario", exception.getMessage());
        verify(usuarioRepository, never()).deleteById(anyLong());
    }
}
