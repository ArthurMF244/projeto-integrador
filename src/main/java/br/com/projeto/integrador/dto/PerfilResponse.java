package br.com.projeto.integrador.dto;

import br.com.projeto.integrador.entity.Usuario;

public record PerfilResponse(
    Integer id,
    String nome,
    String nomeUsuario,
    String email,
    String setor,
    String perfil,
    String status,
    String tema,
    String fotoUrl
) {
    public static PerfilResponse from(Usuario usuario) {
        return new PerfilResponse(
            usuario.getId(), usuario.getNome(), usuario.getNomeUsuario(), usuario.getEmail(),
            usuario.getSetor(), usuario.getPerfil(), usuario.getStatus(), usuario.getTema(),
            usuario.getFoto() == null ? null : "/api/perfil/foto/" + usuario.getFoto()
        );
    }
}
