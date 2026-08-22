package com.pharmaguard.api.reports.application;

import java.time.LocalDate;

public record FiltroReposicao(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        Long medicamentoId,
        Long categoriaId,
        Long unidadeMedidaId,
        Long fornecedorId) {
}
