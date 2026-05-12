package com.sanosysalvos.ms_usuarios.controller;

import com.sanosysalvos.ms_usuarios.model.Usuario;
import com.sanosysalvos.ms_usuarios.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.sanosysalvos.ms_usuarios.dto.UsuarioDTO;


@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/registro")
    public ResponseEntity<UsuarioDTO> crearUsuario(@RequestBody UsuarioDTO usuarioDTO) { 
        return ResponseEntity.ok(usuarioService.crearUsuario(usuarioDTO)); 
    }

    @GetMapping("/lista")
    public List<Usuario> listarTodos() {
        return usuarioService.obtenerTodos(); 
    }
    @GetMapping("/search")
    public ResponseEntity<UsuarioDTO> findByEmail(@RequestParam String email) {
        return ResponseEntity.ok(usuarioService.buscarPorEmail(email));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
