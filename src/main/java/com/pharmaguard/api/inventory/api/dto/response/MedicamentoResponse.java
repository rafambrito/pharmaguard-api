package com.pharmaguard.api.inventory.api.dto.response;

import java.time.LocalDateTime;

public record MedicamentoResponse(
        Long id,
        String nome,
        String apresentacao,
        String descricao,
        CategoriaResponse categoria,
        UnidadeMedidaResponse unidadeMedida,
        String criticidade,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao) {
}
