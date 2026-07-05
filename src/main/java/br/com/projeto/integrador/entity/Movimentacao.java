package br.com.projeto.integrador.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chamado_movimentacoes")
public class Movimentacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Integer id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) @JoinColumn(name = "chamado_id") private Chamado chamado;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "usuario_id") private Usuario usuario;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false, length = 20) private String prioridade;
    @Column(name = "area_responsavel", nullable = false, length = 100) private String areaResponsavel;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "responsavel_id") private Usuario responsavel;
    @Column(name = "responsavel_nome", length = 150) private String responsavelNome;
    @Column(columnDefinition = "TEXT") private String descricao;
    @Column(name = "anexo_nome") private String anexoNome;
    @Column(name = "criado_em", nullable = false) private LocalDateTime criadoEm;

    @PrePersist void prePersist() { if (criadoEm == null) criadoEm = LocalDateTime.now(); }
    public Integer getId() { return id; }
    public void setChamado(Chamado chamado) { this.chamado = chamado; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getAreaResponsavel() { return areaResponsavel; }
    public void setAreaResponsavel(String v) { this.areaResponsavel = v; }
    public void setResponsavel(Usuario v) { this.responsavel = v; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String v) { this.responsavelNome = v; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
}
