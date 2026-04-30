package ms.usuario.msusuario.controller;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ms.usuario.msusuario.model.Usuario;
import ms.usuario.msusuario.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/lista")
    public List<Usuario> listarTodos() {
        return usuarioService.obtenerTodos(); 
}

    @PostMapping("/registro")
    public Usuario crearUsuario(@RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }
    
}