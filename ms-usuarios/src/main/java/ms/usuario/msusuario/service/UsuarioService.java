package ms.usuario.msusuario.service;

import ms.usuario.msusuario.model.Usuario;
import ms.usuario.msusuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {
 @Autowired
    private UsuarioRepository usuarioRepository;

    public Usuario registrarUsuario(Usuario usuario) {
        // Aquí podrías validar si el email ya existe antes de guardar
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> obtenerTodos() {
    return usuarioRepository.findAll();
}
}
