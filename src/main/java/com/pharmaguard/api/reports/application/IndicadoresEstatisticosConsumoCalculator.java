package com.pharmaguard.api.reports.application;

import java.util.List;

public final class IndicadoresEstatisticosConsumoCalculator {

    private IndicadoresEstatisticosConsumoCalculator() {
    }

    public static IndicadoresEstatisticosConsumo calcular(List<Double> consumoDiario, int diasNoPeriodo) {
        if (consumoDiario == null || consumoDiario.isEmpty()) {
            return new IndicadoresEstatisticosConsumo(
                    0d,
                    0d,
                    IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL,
                    0d,
                    0d,
                    0d);
        }

        int totalDias = Math.max(1, diasNoPeriodo);
        double media = consumoDiario.stream().mapToDouble(Double::doubleValue).sum() / totalDias;
        double desvio = desvioPadrao(consumoDiario, media);
        IndicadoresEstatisticosConsumo.TendenciaConsumo tendencia = tendencia(consumoDiario);
        double mediaAjustada = mediaAjustada(media, desvio, tendencia);
        double banda = desvio * Math.sqrt(totalDias);
        double previsaoCentral = mediaAjustada * totalDias;

        return new IndicadoresEstatisticosConsumo(
                media,
                desvio,
                tendencia,
                mediaAjustada,
                Math.max(0d, previsaoCentral - banda),
                Math.max(0d, previsaoCentral + banda));
    }

    private static double desvioPadrao(List<Double> serie, double media) {
        if (serie.size() <= 1) {
            return 0d;
        }
        double somaQuadrados = 0d;
        for (Double valor : serie) {
            double delta = valor - media;
            somaQuadrados += delta * delta;
        }
        return Math.sqrt(somaQuadrados / serie.size());
    }

    private static IndicadoresEstatisticosConsumo.TendenciaConsumo tendencia(List<Double> serie) {
        if (serie.size() < 4) {
            return IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL;
        }

        int meio = serie.size() / 2;
        double mediaPrimeiraMetade = media(serie.subList(0, meio));
        double mediaSegundaMetade = media(serie.subList(meio, serie.size()));

        if (mediaPrimeiraMetade == 0d && mediaSegundaMetade > 0d) {
            return IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO;
        }
        if (mediaPrimeiraMetade == 0d) {
            return IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL;
        }

        double variacaoRelativa = (mediaSegundaMetade - mediaPrimeiraMetade) / mediaPrimeiraMetade;
        if (variacaoRelativa >= 0.10d) {
            return IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO;
        }
        if (variacaoRelativa <= -0.10d) {
            return IndicadoresEstatisticosConsumo.TendenciaConsumo.QUEDA;
        }
        return IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL;
    }

    private static double mediaAjustada(double media, double desvio,
            IndicadoresEstatisticosConsumo.TendenciaConsumo tendencia) {
        double fatorTendencia = switch (tendencia) {
            case CRESCIMENTO -> 1.10d;
            case QUEDA -> 0.90d;
            case ESTAVEL -> 1.0d;
        };
        return Math.max(0d, (media * fatorTendencia) + (desvio * 0.25d));
    }

    private static double media(List<Double> valores) {
        if (valores.isEmpty()) {
            return 0d;
        }
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
    }
}