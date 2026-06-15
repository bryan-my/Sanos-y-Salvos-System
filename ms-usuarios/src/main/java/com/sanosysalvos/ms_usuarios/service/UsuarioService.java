package com.sanosysalvos.ms_usuarios.service;

import com.sanosysalvos.ms_usuarios.model.Usuario;
import com.sanosysalvos.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import  java.util.List;
import com.sanosysalvos.ms_usuarios.dto.UsuarioDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;




@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ModelMapper modelMapper;
    private final RestTemplate restTemplate;
    private final String mascotasServiceUrl;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, ModelMapper modelMapper, RestTemplate restTemplate, @Value("${mascotas.service.url}") String mascotasServiceUrl) {
        this.usuarioRepository = usuarioRepository;
        this.modelMapper = modelMapper;
        this.restTemplate = restTemplate;
        this.mascotasServiceUrl = mascotasServiceUrl;
    }

    public UsuarioDTO crearUsuario(UsuarioDTO dto) {
        // 1. Convertimos el DTO a la Entidad
        Usuario usuario = modelMapper.map(dto, Usuario.class);

        // 2. ENCRIPTACIÓN
        String claveEncriptada = encoder.encode(usuario.getPassword());
        usuario.setPassword(claveEncriptada);

        // 3. Guardar (Cambiamos 'repository' por 'usuarioRepository')
        Usuario guardado = usuarioRepository.save(usuario); 

        return modelMapper.map(guardado, UsuarioDTO.class);
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public UsuarioDTO buscarPorEmail(String email) {
        // Cambiamos 'repository' por 'usuarioRepository'
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            
        return modelMapper.map(usuario, UsuarioDTO.class);
    }

    public void eliminarUsuario(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        try {
            restTemplate.delete(mascotasServiceUrl + "/api/mascotas/usuario/" + id);
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron eliminar las mascotas del usuario");
        }
        usuarioRepository.deleteById(id);
    }
}
