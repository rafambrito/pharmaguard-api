package com.pharmaguard.api.reports.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class IndicadoresEstatisticosConsumoCalculatorTest {

    @Test
    void deveIdentificarConsumoAltoComCrescimento() {
        List<Double> serie = List.of(2d, 3d, 4d, 5d, 8d, 9d);

        IndicadoresEstatisticosConsumo indicadores = IndicadoresEstatisticosConsumoCalculator.calcular(serie, 6);

        assertEquals(IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO, indicadores.tendenciaConsumo());
        assertTrue(indicadores.desvioPadraoConsumo() > 0d);
        assertTrue(indicadores.mediaAjustada() > indicadores.consumoMedioDiario());
        assertTrue(indicadores.previsaoMaximaPeriodo() >= indicadores.previsaoMinimaPeriodo());
    }

    @Test
    void deveIdentificarConsumoEstavel() {
        List<Double> serie = List.of(5d, 5d, 5d, 5d, 5d, 5d);

        IndicadoresEstatisticosConsumo indicadores = IndicadoresEstatisticosConsumoCalculator.calcular(serie, 6);

        assertEquals(IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL, indicadores.tendenciaConsumo());
        assertEquals(0d, indicadores.desvioPadraoConsumo());
        assertEquals(5d, indicadores.consumoMedioDiario());
    }

    @Test
    void deveIdentificarConsumoIrregularComAltaVariacao() {
        List<Double> serie = List.of(1d, 10d, 2d, 12d, 1d, 11d);

        IndicadoresEstatisticosConsumo indicadores = IndicadoresEstatisticosConsumoCalculator.calcular(serie, 6);

        assertTrue(indicadores.desvioPadraoConsumo() >= 4d);
        assertTrue(indicadores.previsaoMaximaPeriodo() > indicadores.previsaoMinimaPeriodo());
    }

    @Test
    void deveRetornarIndicadoresZeradosParaSerieVazia() {
        IndicadoresEstatisticosConsumo indicadores = IndicadoresEstatisticosConsumoCalculator.calcular(List.of(), 30);

        assertEquals(0d, indicadores.consumoMedioDiario());
        assertEquals(0d, indicadores.desvioPadraoConsumo());
        assertEquals(IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL, indicadores.tendenciaConsumo());
        assertEquals(0d, indicadores.mediaAjustada());
        assertEquals(0d, indicadores.previsaoMinimaPeriodo());
        assertEquals(0d, indicadores.previsaoMaximaPeriodo());
    }
}