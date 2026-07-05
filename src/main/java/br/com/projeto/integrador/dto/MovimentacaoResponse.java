package br.com.projeto.integrador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import br.com.projeto.integrador.entity.Movimentacao;
import java.time.LocalDateTime;

public record MovimentacaoResponse(
    Integer id, String status, String prioridade,
    @JsonProperty("area_responsavel") String areaResponsavel,
    @JsonProperty("responsavel_nome") String responsavelNome,
    String descricao, @JsonProperty("criado_em") LocalDateTime criadoEm
) {
    public static MovimentacaoResponse from(Movimentacao m) {
        return new MovimentacaoResponse(m.getId(), m.getStatus(), m.getPrioridade(),
            m.getAreaResponsavel(), m.getResponsavelNome(), m.getDescricao(), m.getCriadoEm());
    }
}
