package br.com.projeto.integrador.dto;

import jakarta.validation.constraints.*;

public record UsuarioUpdateRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 100) String setor,
    @NotBlank String perfil,
    @NotBlank String status,
    @NotBlank @Size(min = 4, max = 50) String nomeUsuario,
    @Size(max = 100) String senha
) {}
