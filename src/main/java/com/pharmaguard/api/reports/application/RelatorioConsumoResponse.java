package com.pharmaguard.api.reports.application;

import java.time.LocalDate;
import java.util.List;

public record RelatorioConsumoResponse(
        LocalDate periodoInicio,
        LocalDate periodoFim,
        FiltroConsumo filtro,
        double totalConsumido,
        double mediaDiaria,
        TendenciaConsumo tendencia,
        List<ItemConsumo> itens) {

    public enum TendenciaConsumo {
        CRESCENTE,
        ESTAVEL,
        DECRESCENTE
    }

    public record ItemConsumo(
            Long medicamentoId,
            String nomeMedicamento,
            String categoriaNome,
            String unidadeMedidaSigla,
            double quantidadeConsumida,
            double mediaDiaria) {
    }
}
