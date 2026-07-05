package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.*;
import br.com.projeto.integrador.service.ChamadoService;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.*;

@RestController
@RequestMapping("/api/chamados")
@Tag(name = "Chamados", description = "CRUD completo e movimentações de chamados")
public class ChamadoController {
    private final ChamadoService service;
    public ChamadoController(ChamadoService service) { this.service = service; }

    @GetMapping
    @Operation(summary = "Lista e filtra chamados")
    public Map<String, Object> listar(@RequestParam(required=false) String status,
        @RequestParam(required=false) String prioridade, @RequestParam(required=false) String area,
        @RequestParam(required=false) String responsavel, @RequestParam(required=false) String solicitante,
        @RequestParam(required=false) String q) {
        List<ChamadoResponse> data = service.listar(status, prioridade, area, responsavel, solicitante, q);
        long atendimento = data.stream().filter(c -> "Em atendimento".equals(c.status())).count();
        long finalizados = data.stream().filter(c -> "Finalizado".equals(c.status())).count();
        long abertos = data.stream().filter(c -> !"Finalizado".equals(c.status())).count();
        return Map.of("data", data, "kpis", Map.of("abertos", abertos,
            "atendimento", atendimento, "finalizados", finalizados));
    }
    @GetMapping("/{id}") @Operation(summary = "Busca um chamado")
    public Map<String, Object> buscar(@PathVariable Integer id) {
        ChamadoResponse c = service.buscar(id);
        return Map.of("data", c, "movimentacoes", c.movimentacoes());
    }
    @PostMapping @Operation(summary = "Cria um chamado")
    public ResponseEntity<ChamadoResponse> criar(@Valid @RequestBody ChamadoRequest request) {
        ChamadoResponse c = service.criar(request);
        return ResponseEntity.created(URI.create("/api/chamados/" + c.id())).body(c);
    }
    @PutMapping("/{id}") @Operation(summary = "Atualiza integralmente um chamado e registra movimentação")
    public ChamadoResponse atualizar(@PathVariable Integer id, @Valid @RequestBody ChamadoRequest request) {
        return service.atualizar(id, request);
    }
    @DeleteMapping("/{id}") @Operation(summary = "Exclui um chamado")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Integer id) { service.excluir(id); }
}
