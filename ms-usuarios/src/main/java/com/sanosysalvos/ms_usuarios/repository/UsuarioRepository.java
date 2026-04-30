package com.sanosysalvos.ms_usuarios.repository;
import com.sanosysalvos.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Buscador personalizado para el login o validaciones
    Optional<Usuario> findByEmail(String email);
}