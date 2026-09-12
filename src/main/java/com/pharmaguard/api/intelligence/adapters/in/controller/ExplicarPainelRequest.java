package com.pharmaguard.api.intelligence.adapters.in.controller;

import com.pharmaguard.api.intelligence.domain.TipoPainel;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record ExplicarPainelRequest(
        @NotNull TipoPainel tipoPainel,
        @NotNull LocalDate periodoInicio,
        @NotNull LocalDate periodoFim,
        Long medicamentoId,
        Long categoriaId,
        Long unidadeMedidaId,
        Long fornecedorId,
        Long unidadeSaudeId) {
}