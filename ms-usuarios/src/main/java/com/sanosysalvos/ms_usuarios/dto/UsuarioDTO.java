package com.sanosysalvos.ms_usuarios.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private String id; // O Long, dependiendo de tu base de datos
    private String nombreCompleto;
    private String email;
    private String password; // Es vital que viaje aquí para que el Auth Service la compare
    private String rol;
}