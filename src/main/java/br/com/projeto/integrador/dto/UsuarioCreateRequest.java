package br.com.projeto.integrador.dto;

import jakarta.validation.constraints.*;

public record UsuarioCreateRequest(
    @NotBlank @Size(max = 150) String nome,
    @NotBlank @Email @Size(max = 150) String email,
    @NotBlank @Size(max = 100) String setor,
    @NotBlank String perfil,
    @NotBlank String status,
    @NotBlank @Size(min = 4, max = 50) String nomeUsuario,
    @NotBlank @Size(min = 6, max = 100) String senha
) {}
