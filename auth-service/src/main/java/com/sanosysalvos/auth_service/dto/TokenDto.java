package com.sanosysalvos.auth_service.dto;
import  lombok.*;
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class TokenDto {
    private String token;
    private Long userId;
    private String nombreCompleto;
    private String email;
    private String rol;
}
