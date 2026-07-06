package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.*;
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
    @GetMapping("/{id}") public UsuarioResponse buscar(@PathVariable Integer id) { return service.buscar(id); }
    @PostMapping public ResponseEntity<UsuarioResponse> criar(@Valid @RequestBody UsuarioCreateRequest r) {
        UsuarioResponse u = service.criar(r);
        return ResponseEntity.created(URI.create("/api/usuarios/" + u.id())).body(u);
    }
    @PutMapping("/{id}") public UsuarioResponse atualizar(@PathVariable Integer id, @Valid @RequestBody UsuarioUpdateRequest r) {
        return service.atualizar(id, r);
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) { service.excluir(id); }
}
