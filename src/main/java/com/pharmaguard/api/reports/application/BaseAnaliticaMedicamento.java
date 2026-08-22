package com.pharmaguard.api.reports.application;

import java.time.LocalDate;

public record BaseAnaliticaMedicamento(
        Long medicamentoId,
        String nomeMedicamento,
        Long categoriaId,
        Long unidadeMedidaId,
        LocalDate periodoInicio,
        LocalDate periodoFim,
        int diasNoPeriodo,
        int saldoAtual,
        double consumoTotalPeriodo,
        double consumoMedioDiario,
        int leadTimeDias,
        IndicadoresEstatisticosConsumo indicadoresEstatisticos,
        NivelEstoqueCalculado nivelEstoque,
        AnaliseValidadeMedicamento analiseValidade) {
}