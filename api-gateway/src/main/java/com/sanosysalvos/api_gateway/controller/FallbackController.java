package com.sanosysalvos.api_gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/default")
    public Mono<ResponseEntity<String>> defaultFallback() {
        String errorJson = "{\"error\": \"El servicio no está disponible temporalmente. Por favor, intente más tarde.\"}";
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorJson));
    }
}
