package br.com.projeto.integrador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ConfiguracaoRequest(
    @JsonProperty("nome_sistema") @NotBlank String nomeSistema,
    String tema,
    @JsonProperty("email_suporte") @NotBlank @Email String emailSuporte
) {}
