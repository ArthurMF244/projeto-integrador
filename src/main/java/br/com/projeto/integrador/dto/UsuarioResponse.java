package br.com.projeto.integrador.dto;

import br.com.projeto.integrador.entity.Usuario;

public record UsuarioResponse(
    Integer id, String nome, String email, String setor, String perfil, String status, String nomeUsuario,
    String tema, String fotoUrl
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getSetor(),
            u.getPerfil(), u.getStatus(), u.getNomeUsuario(), u.getTema(),
            u.getFoto() == null ? null : "/api/perfil/foto/" + u.getFoto());
    }
}
