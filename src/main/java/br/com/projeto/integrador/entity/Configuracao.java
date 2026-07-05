package br.com.projeto.integrador.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "configuracoes")
public class Configuracao {
    @Id private Integer id = 1;
    @Column(name = "nome_sistema", nullable = false, length = 120) private String nomeSistema;
    @Column(nullable = false, length = 10) private String tema;
    @Column(name = "email_suporte", nullable = false, length = 150) private String emailSuporte;
    public Integer getId() { return id; }
    public String getNomeSistema() { return nomeSistema; }
    public void setNomeSistema(String v) { this.nomeSistema = v; }
    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }
    public String getEmailSuporte() { return emailSuporte; }
    public void setEmailSuporte(String v) { this.emailSuporte = v; }
}
