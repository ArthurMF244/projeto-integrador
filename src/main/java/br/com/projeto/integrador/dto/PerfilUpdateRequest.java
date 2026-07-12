package br.com.projeto.integrador.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PerfilUpdateRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Pattern(regexp = "light|dark|system", message = "Tema inválido.") String tema
) {}
