package com.pharmaguard.api.reports.application;

public final class NivelEstoqueCalculator {

    private NivelEstoqueCalculator() {
    }

    public static NivelEstoqueCalculado calcular(IndicadoresEstatisticosConsumo indicadores, int saldoAtual, int leadTimeDias) {
        int leadTime = Math.max(1, leadTimeDias);
        double mediaAjustada = Math.max(0d, indicadores.mediaAjustada());
        double desvio = Math.max(0d, indicadores.desvioPadraoConsumo());

        int estoqueSeguranca = (int) Math.ceil(desvio * Math.sqrt(leadTime));
        int estoqueMinimo = (int) Math.ceil((mediaAjustada * leadTime) + estoqueSeguranca);
        int estoqueMaximo = (int) Math.ceil(estoqueMinimo + (mediaAjustada * leadTime));
        double cobertura = coberturaDias(saldoAtual, mediaAjustada);
        NivelEstoqueCalculado.RiscoRuptura risco = classificarRisco(saldoAtual, estoqueMinimo, cobertura, leadTime);

        return new NivelEstoqueCalculado(
                Math.max(0, estoqueMinimo),
                Math.max(0, Math.max(estoqueMaximo, estoqueMinimo)),
                Math.max(0, estoqueSeguranca),
                cobertura,
                risco);
    }

    private static double coberturaDias(int saldoAtual, double mediaAjustada) {
        if (saldoAtual <= 0) {
            return 0d;
        }
        if (mediaAjustada <= 0d) {
            return Double.POSITIVE_INFINITY;
        }
        return saldoAtual / mediaAjustada;
    }

    private static NivelEstoqueCalculado.RiscoRuptura classificarRisco(int saldoAtual, int estoqueMinimo,
            double coberturaDias, int leadTimeDias) {
        if (saldoAtual <= 0) {
            return NivelEstoqueCalculado.RiscoRuptura.CRITICO;
        }
        if (saldoAtual < estoqueMinimo || coberturaDias < leadTimeDias) {
            return NivelEstoqueCalculado.RiscoRuptura.ALTO;
        }
        if (coberturaDias < (leadTimeDias * 2d)) {
            return NivelEstoqueCalculado.RiscoRuptura.MEDIO;
        }
        return NivelEstoqueCalculado.RiscoRuptura.BAIXO;
    }
}