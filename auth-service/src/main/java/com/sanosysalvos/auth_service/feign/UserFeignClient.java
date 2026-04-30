package com.sanosysalvos.auth_service.feign;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.sanosysalvos.auth_service.dto.UsuarioDto;
@FeignClient(name = "ms-usuarios")
public interface UserFeignClient {
    @GetMapping("/api/usuarios/search")
    UsuarioDto findByEmail(@RequestParam("email") String email);
}