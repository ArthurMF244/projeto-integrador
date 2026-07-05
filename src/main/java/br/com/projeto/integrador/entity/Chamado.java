package br.com.projeto.integrador.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chamados")
public class Chamado {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 180) private String titulo;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "solicitante_id") private Usuario solicitante;
    @Column(name = "solicitante_nome", nullable = false, length = 150) private String solicitanteNome;
    @Column(name = "area_solicitante", nullable = false, length = 100) private String areaSolicitante;
    @Column(name = "area_responsavel", nullable = false, length = 100) private String areaResponsavel;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "responsavel_id") private Usuario responsavel;
    @Column(name = "responsavel_nome", length = 150) private String responsavelNome;
    @Column(nullable = false, length = 100) private String categoria;
    @Column(nullable = false, length = 20) private String prioridade;
    @Column(nullable = false, length = 20) private String impacto;
    @Column(nullable = false, length = 30) private String status;
    @Column(nullable = false, columnDefinition = "TEXT") private String descricao;
    @Column(name = "anexo_nome") private String anexoNome;
    @Column(name = "aberto_em", nullable = false) private LocalDateTime abertoEm;
    @Column(name = "finalizado_em") private LocalDateTime finalizadoEm;
    @Column(name = "updated_at", insertable = false, updatable = false) private LocalDateTime atualizadoEm;
    @OneToMany(mappedBy = "chamado", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("criadoEm DESC, id DESC")
    private List<Movimentacao> movimentacoes = new ArrayList<>();

    @PrePersist void prePersist() {
        if (abertoEm == null) abertoEm = LocalDateTime.now();
    }
    public Integer getId() { return id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public Usuario getSolicitante() { return solicitante; }
    public void setSolicitante(Usuario solicitante) { this.solicitante = solicitante; }
    public String getSolicitanteNome() { return solicitanteNome; }
    public void setSolicitanteNome(String v) { this.solicitanteNome = v; }
    public String getAreaSolicitante() { return areaSolicitante; }
    public void setAreaSolicitante(String v) { this.areaSolicitante = v; }
    public String getAreaResponsavel() { return areaResponsavel; }
    public void setAreaResponsavel(String v) { this.areaResponsavel = v; }
    public Usuario getResponsavel() { return responsavel; }
    public void setResponsavel(Usuario responsavel) { this.responsavel = responsavel; }
    public String getResponsavelNome() { return responsavelNome; }
    public void setResponsavelNome(String v) { this.responsavelNome = v; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }
    public String getImpacto() { return impacto; }
    public void setImpacto(String impacto) { this.impacto = impacto; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getAnexoNome() { return anexoNome; }
    public LocalDateTime getAbertoEm() { return abertoEm; }
    public LocalDateTime getFinalizadoEm() { return finalizadoEm; }
    public void setFinalizadoEm(LocalDateTime v) { this.finalizadoEm = v; }
    public List<Movimentacao> getMovimentacoes() { return movimentacoes; }
}
