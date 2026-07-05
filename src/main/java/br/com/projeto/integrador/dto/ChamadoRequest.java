package br.com.projeto.integrador.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChamadoRequest(
    @NotBlank @Size(max = 180) String titulo,
    @JsonProperty("solicitante_id") Integer solicitanteId,
    @JsonProperty("solicitante_nome") @Size(max = 150) String solicitanteNome,
    @JsonProperty("area_solicitante") @NotBlank @Size(max = 100) String areaSolicitante,
    @JsonProperty("area_responsavel") @NotBlank @Size(max = 100) String areaResponsavel,
    @JsonProperty("responsavel_id") Integer responsavelId,
    @JsonProperty("responsavel_nome") @Size(max = 150) String responsavelNome,
    @NotBlank @Size(max = 100) String categoria,
    @NotBlank String prioridade,
    String impacto,
    String status,
    @NotBlank String descricao,
    @JsonProperty("descricao_movimentacao") String descricaoMovimentacao
) {}
