package com.pharmaguard.api.reports.application;

import java.time.LocalDate;

public record FiltroVencimentos(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        Long medicamentoId,
        Long categoriaId,
                Long unidadeMedidaId,
                Long unidadeSaudeId) {
        public FiltroVencimentos(LocalDate inicio, LocalDate fim, Long medicamentoId, Long categoriaId,
                        Long unidadeMedidaId) {
                this(inicio, fim, medicamentoId, categoriaId, unidadeMedidaId, null);
        }
}
