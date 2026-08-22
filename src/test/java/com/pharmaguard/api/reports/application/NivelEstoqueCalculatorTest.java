package com.pharmaguard.api.reports.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NivelEstoqueCalculatorTest {

    @Test
    void deveCalcularNiveisDeEstoqueComRiscoAltoQuandoSaldoAbaixoDoMinimo() {
        IndicadoresEstatisticosConsumo indicadores = new IndicadoresEstatisticosConsumo(
                10d,
                4d,
                IndicadoresEstatisticosConsumo.TendenciaConsumo.ESTAVEL,
                10d,
                250d,
                350d);

        NivelEstoqueCalculado nivel = NivelEstoqueCalculator.calcular(indicadores, 20, 7);

        assertTrue(nivel.estoqueSeguranca() > 0);
        assertTrue(nivel.estoqueMinimo() > 0);
        assertTrue(nivel.estoqueMaximo() >= nivel.estoqueMinimo());
        assertEquals(NivelEstoqueCalculado.RiscoRuptura.ALTO, nivel.riscoRuptura());
    }

    @Test
    void deveClassificarRiscoCriticoComSaldoZero() {
        IndicadoresEstatisticosConsumo indicadores = new IndicadoresEstatisticosConsumo(
                3d,
                1d,
                IndicadoresEstatisticosConsumo.TendenciaConsumo.CRESCIMENTO,
                3.5d,
                80d,
                110d);

        NivelEstoqueCalculado nivel = NivelEstoqueCalculator.calcular(indicadores, 0, 5);

        assertEquals(0d, nivel.coberturaDemandaDias());
        assertEquals(NivelEstoqueCalculado.RiscoRuptura.CRITICO, nivel.riscoRuptura());
    }
}