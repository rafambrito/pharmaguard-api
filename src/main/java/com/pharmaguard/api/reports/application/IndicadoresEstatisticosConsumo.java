package com.pharmaguard.api.reports.application;

public record IndicadoresEstatisticosConsumo(
        double consumoMedioDiario,
        double desvioPadraoConsumo,
        TendenciaConsumo tendenciaConsumo,
        double mediaAjustada,
        double previsaoMinimaPeriodo,
        double previsaoMaximaPeriodo) {

    public enum TendenciaConsumo {
        QUEDA,
        ESTAVEL,
        CRESCIMENTO
    }
}