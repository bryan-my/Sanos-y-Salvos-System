package com.sanosysalvos.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    // 1. Inyectamos la clave secreta que definimos en el properties/yml
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/api/usuarios/registro").permitAll() // Permitir registro
                .pathMatchers("/api/auth/**").permitAll()           // Permitir login
                .pathMatchers(HttpMethod.GET, "/api/mascotas/lista").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/mascotas/*").permitAll()
                .anyExchange().authenticated()                      // Todo lo demás requiere JWT
            )
            .cors(Customizer.withDefaults())
            // 2. Aquí es donde usamos el decodificador que crearemos abajo
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 3. EL BEAN QUE FALTA: Este es el "traductor" de tokens
    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = secret.getBytes();
        SecretKeySpec secretKey = new SecretKeySpec(keyBytes, "HmacSHA256");
        return NimbusReactiveJwtDecoder.withSecretKey(secretKey).build();
    }
}
