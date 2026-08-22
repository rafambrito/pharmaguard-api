package com.pharmaguard.api.inventory.adapters.in.dto.response;

import java.time.LocalDate;
import java.util.List;

public record SaldoEstoqueResponse(
        Long medicamentoId,
        Integer quantidadeDisponivel,
        Integer quantidadeReservada,
        LocalDate validadeMaisProxima,
        List<SaldoLoteResponse> lotesAtivos) {
}
