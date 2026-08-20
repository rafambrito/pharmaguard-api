package com.pharmaguard.api.inventory.api.dto.response;

import java.time.LocalDateTime;

public record UnidadeMedidaResponse(
        Long id,
        String nome,
        String sigla,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao) {
}
