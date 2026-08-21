package com.pharmaguard.api.inventory.adapters.in.dto.response;

import java.time.LocalDateTime;

public record CategoriaResponse(
        Long id,
        String nome,
        String descricao,
        boolean ativo,
        LocalDateTime dataCriacao,
        LocalDateTime dataUltimaAlteracao) {
}
