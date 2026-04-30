package ms.usuario.msusuario.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor 
@Builder // Te permite crear objetos de forma más fácil: Usuario.builder().nombre(...).build()
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password; // Recuerda que luego la encriptaremos para Ciberseguridad

    private String telefono;
    
    private String direccion;

    @Enumerated(EnumType.STRING)
    private UserRole rol;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @PrePersist
    protected void onCreate() {
        fechaRegistro = LocalDateTime.now();
    }
}