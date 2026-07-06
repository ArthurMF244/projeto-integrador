package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.UsuarioResponse;
import br.com.projeto.integrador.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/auth")
public class AuthController {
    private final UsuarioRepository repository;
    public AuthController(UsuarioRepository repository) { this.repository = repository; }

    @GetMapping("/me") public UsuarioResponse me(Authentication authentication) {
        return repository.findByNomeUsuarioIgnoreCase(authentication.getName())
            .map(UsuarioResponse::from).orElseThrow();
    }
    @GetMapping("/csrf") public Map<String, String> csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        return Map.of("headerName", token.getHeaderName(), "token", token.getToken());
    }
}
