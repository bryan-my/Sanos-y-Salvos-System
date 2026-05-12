package com.sanosysalvos.auth_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UsuarioDto {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String password;
    private String rol;
}
