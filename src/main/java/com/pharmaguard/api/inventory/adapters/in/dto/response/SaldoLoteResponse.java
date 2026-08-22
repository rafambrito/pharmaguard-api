package com.pharmaguard.api.inventory.adapters.in.dto.response;

import com.pharmaguard.api.inventory.domain.StatusValidade;
import java.time.LocalDate;

public record SaldoLoteResponse(
        Long medicamentoId,
        Long loteId,
        String numeroLote,
        LocalDate dataValidade,
        Integer quantidadeDisponivel,
        StatusValidade statusValidade) {
}
