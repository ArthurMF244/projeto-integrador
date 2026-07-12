package br.com.projeto.integrador.controller;

import br.com.projeto.integrador.dto.PerfilResponse;
import br.com.projeto.integrador.dto.PerfilSenhaRequest;
import br.com.projeto.integrador.dto.PerfilUpdateRequest;
import br.com.projeto.integrador.service.PerfilFotoService;
import br.com.projeto.integrador.service.PerfilService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/perfil")
public class PerfilController {
    private final PerfilService perfilService;
    private final PerfilFotoService fotoService;

    public PerfilController(PerfilService perfilService, PerfilFotoService fotoService) {
        this.perfilService = perfilService;
        this.fotoService = fotoService;
    }

    @GetMapping
    public PerfilResponse buscar(Authentication authentication) {
        return perfilService.buscar(authentication.getName());
    }

    @PutMapping
    public PerfilResponse atualizar(Authentication authentication, @Valid @RequestBody PerfilUpdateRequest request) {
        return perfilService.atualizar(authentication.getName(), request);
    }

    @PutMapping("/senha")
    public Map<String, String> alterarSenha(
        Authentication authentication,
        @Valid @RequestBody PerfilSenhaRequest request
    ) {
        perfilService.alterarSenha(authentication.getName(), request);
        return Map.of("mensagem", "Senha alterada com sucesso.");
    }

    @PostMapping(value = "/foto", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PerfilResponse salvarFoto(Authentication authentication, @RequestParam("foto") MultipartFile foto) {
        return fotoService.salvar(authentication.getName(), foto);
    }

    @DeleteMapping("/foto")
    public PerfilResponse removerFoto(Authentication authentication) {
        return fotoService.remover(authentication.getName());
    }

    @GetMapping("/foto/{nome}")
    public ResponseEntity<org.springframework.core.io.Resource> foto(@PathVariable String nome) {
        PerfilFotoService.FotoArmazenada foto = fotoService.carregar(nome);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(foto.mimeType()))
            .cacheControl(CacheControl.noCache())
            .body(foto.recurso());
    }
}
