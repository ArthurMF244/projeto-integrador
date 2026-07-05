package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.ConfiguracaoRequest;
import br.com.projeto.integrador.entity.Configuracao;
import br.com.projeto.integrador.repository.ConfiguracaoRepository;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController @RequestMapping("/api/configuracoes")
public class ConfiguracaoController {
    private final ConfiguracaoRepository repository;
    public ConfiguracaoController(ConfiguracaoRepository repository) { this.repository = repository; }
    @GetMapping public Map<String,Object> buscar() {
        return Map.of("data", repository.findById(1).map(this::response).orElse(Map.of()));
    }
    @PutMapping @Transactional
    public Map<String,String> salvar(@Valid @RequestBody ConfiguracaoRequest r) {
        if (!"".equals(r.tema()) && !"dark".equals(r.tema()))
            throw new br.com.projeto.integrador.exception.BusinessException("Tema inválido.");
        Configuracao c = repository.findById(1).orElseGet(Configuracao::new);
        c.setNomeSistema(r.nomeSistema().trim()); c.setTema(r.tema() == null ? "" : r.tema());
        c.setEmailSuporte(r.emailSuporte().trim()); repository.save(c);
        return Map.of("mensagem", "Configurações salvas com sucesso.");
    }
    private Map<String,Object> response(Configuracao c) {
        return Map.of("id", c.getId(), "nome_sistema", c.getNomeSistema(),
            "tema", c.getTema(), "email_suporte", c.getEmailSuporte());
    }
}
