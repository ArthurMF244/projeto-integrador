package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.UsuarioRequest;
import br.com.projeto.integrador.entity.Usuario;
import br.com.projeto.integrador.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController @RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;
    public UsuarioController(UsuarioService service) { this.service = service; }
    @GetMapping public Map<String,Object> listar(@RequestParam(defaultValue="false") boolean ativos) {
        return Map.of("data", service.listar(ativos));
    }
    @GetMapping("/{id}") public Usuario buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<Usuario> criar(@Valid @RequestBody UsuarioRequest r) {
        Usuario u = service.criar(r); return ResponseEntity.created(URI.create("/api/usuarios/" + u.getId())).body(u);
    }
    @PutMapping("/{id}") public Usuario atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioRequest r) {
        return service.atualizar(id, r);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) { service.excluir(id); }
}
