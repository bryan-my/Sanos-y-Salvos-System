package com.sanosysalvos.auth_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.sanosysalvos.auth_service.dto.AuthDto;
import com.sanosysalvos.auth_service.dto.TokenDto;
import com.sanosysalvos.auth_service.dto.UsuarioDto;
import com.sanosysalvos.auth_service.feign.UserFeignClient;
import com.sanosysalvos.auth_service.security.JwtProvider;

@Service
public class AuthService {
    @Autowired private UserFeignClient userFeignClient;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtProvider jwtProvider;

    public TokenDto login(AuthDto dto) {
        UsuarioDto usuario = userFeignClient.findByEmail(dto.getEmail());
        if (usuario != null && passwordEncoder.matches(dto.getPassword(), usuario.getPassword())) {
            return new TokenDto(jwtProvider.createToken(usuario));
        }
        throw new RuntimeException("Credenciales incorrectas");
    }
}