package com.pharmaguard.api.inventory.api.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record LoteResponse(
        Long id,
        String numeroLote,
        LocalDate dataValidade,
        Integer quantidadeInicial,
        String statusValidade,
        Long medicamentoId,
        LocalDateTime dataCriacao) {
}
