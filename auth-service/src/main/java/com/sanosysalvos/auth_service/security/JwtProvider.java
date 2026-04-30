package com.sanosysalvos.auth_service.security;

import  java.util.HashMap;
import java.util.Date;
import java.util.Map;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.sanosysalvos.auth_service.dto.UsuarioDto;
import io.jsonwebtoken.Jwts;
@Component
public class JwtProvider {
    @Value("${jwt.secret}")
    private String secret;

    public String createToken(UsuarioDto usuario) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", usuario.getId());
        claims.put("rol", usuario.getRol());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }
}
