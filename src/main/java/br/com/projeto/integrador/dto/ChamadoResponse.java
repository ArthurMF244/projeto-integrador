package br.com.projeto.integrador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.projeto.integrador.entity.Chamado;
import java.time.LocalDateTime;
import java.util.List;

public record ChamadoResponse(
    Integer id, String titulo,
    @JsonProperty("solicitante_id") Integer solicitanteId,
    @JsonProperty("solicitante_nome") String solicitanteNome,
    @JsonProperty("area_solicitante") String areaSolicitante,
    @JsonProperty("area_responsavel") String areaResponsavel,
    @JsonProperty("responsavel_id") Integer responsavelId,
    @JsonProperty("responsavel_nome") String responsavelNome,
    String categoria, String prioridade, String impacto, String status, String descricao,
    @JsonProperty("aberto_em") LocalDateTime abertoEm,
    @JsonProperty("finalizado_em") LocalDateTime finalizadoEm,
    List<MovimentacaoResponse> movimentacoes
) {
    public static ChamadoResponse from(Chamado c) {
        return new ChamadoResponse(c.getId(), c.getTitulo(),
            c.getSolicitante() == null ? null : c.getSolicitante().getId(), c.getSolicitanteNome(),
            c.getAreaSolicitante(), c.getAreaResponsavel(),
            c.getResponsavel() == null ? null : c.getResponsavel().getId(), c.getResponsavelNome(),
            c.getCategoria(), c.getPrioridade(), c.getImpacto(), c.getStatus(), c.getDescricao(),
            c.getAbertoEm(), c.getFinalizadoEm(),
            c.getMovimentacoes().stream().map(MovimentacaoResponse::from).toList());
    }
}
