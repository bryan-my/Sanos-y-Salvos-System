package com.sanosysalvos.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable()) // Desactivamos CSRF por ser una API
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/usuarios/registro").permitAll() // Permitir registro sin token
                .pathMatchers("/api/auth/**").permitAll()           // Permitir login sin token
                .anyExchange().authenticated()                     // Todo lo demás requiere JWT
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
            
        return http.build();
    }
}
