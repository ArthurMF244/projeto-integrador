package br.com.projeto.integrador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilSenhaRequest(
    @NotBlank String senhaAtual,
    @NotBlank @Size(min = 6, max = 100) String novaSenha,
    @NotBlank String confirmacaoSenha
) {}
