package br.com.projeto.integrador.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 150) private String nome;
    @Column(nullable = false, unique = true, length = 150) private String email;
    @Column(nullable = false, length = 100) private String setor;
    @Column(nullable = false, length = 30) private String perfil = "Solicitante";
    @Column(nullable = false, length = 20) private String status = "Ativo";
    @Column(name = "nome_usuario", unique = true, length = 50) private String nomeUsuario;
    @Column(length = 100) private String senha;
    @Column(nullable = false, length = 20, columnDefinition = "varchar(20) default 'dark'") private String tema = "dark";
    @Column(length = 255) private String foto;
    @Column(name = "created_at", insertable = false, updatable = false) private LocalDateTime criadoEm;
    @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime atualizadoEm;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSetor() { return setor; }
    public void setSetor(String setor) { this.setor = setor; }
    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNomeUsuario() { return nomeUsuario; }
    public void setNomeUsuario(String nomeUsuario) { this.nomeUsuario = nomeUsuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
