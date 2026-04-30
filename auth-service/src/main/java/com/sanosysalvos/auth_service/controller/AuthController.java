package com.sanosysalvos.auth_service.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sanosysalvos.auth_service.dto.AuthDto;
import com.sanosysalvos.auth_service.dto.TokenDto;
import com.sanosysalvos.auth_service.service.AuthService;
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody AuthDto dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}